package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteResponseDTO;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class CommentController {
    private final RaceCommentService commentService;
    private final UserService userService;

    public CommentController(RaceCommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/comments/race/{raceId}")
    public ResponseEntity<List<RaceCommentWithVotesDto>> getAllCommentsForRace(@PathVariable Long raceId) {
        List<RaceCommentWithVotesDto> allComments = commentService.getRaceCommentsByRaceId(raceId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }

    @PostMapping("/comments/{raceId}")
    public ResponseEntity<RaceCommentResponseDTO> createComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long raceId,
            @RequestBody RaceCommentRequestDTO comment) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        try {
            RaceCommentResponseDTO createdComment = commentService.createRaceComment(comment, user, raceId);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/comment/vote")
    public ResponseEntity<CommentVoteResponseDTO> voteForComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody VoteRequest voteRequest
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        CommentVoteResponseDTO commentVoteResponseDTO = commentService.voteComment(voteRequest.commentId(), voteRequest.isUpVote(), user);
        return new ResponseEntity<>(commentVoteResponseDTO, HttpStatus.OK);
    }
}
