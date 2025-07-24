package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteResponseDTO;
import com.ivangochev.raceratingapi.racecomment.vote.UserCommentVoteStatus;
import com.ivangochev.raceratingapi.security.CustomUserDetails;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserService;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final RaceCommentService commentService;
    private final UserService userService;

    public CommentController(RaceCommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/comments/race/{raceId}")
    @PermitAll
    public ResponseEntity<List<RaceCommentWithVotesDto>> getAllCommentsForRace(@PathVariable Long raceId) {
        List<RaceCommentWithVotesDto> allComments = commentService.getRaceCommentsByRaceId(raceId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }

    /**
     * Retrieves the vote status (upvote or downvote) for the given comment IDs
     * by the currently authenticated user.
     *
     * @param commentIds List of comment IDs to retrieve vote status for
     * @param currentUser Authenticated user (injected from security context)
     * @return List of vote statuses (comment ID and vote direction)
     */
    @GetMapping("/comment-votes")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<UserCommentVoteStatus>> getUserVotes(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam List<Long> commentIds
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        List<UserCommentVoteStatus> votes = commentService.getCommentVotesByCommentId(user.getId(), commentIds);
        return ResponseEntity.ok(votes);
    }

    @PostMapping("/comments/{raceId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<RaceCommentWithVotesDto> createComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long raceId,
            @RequestBody RaceCommentRequestDTO comment) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        if (StringUtils.isEmpty(comment.getCommentText())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            RaceCommentWithVotesDto createdComment = commentService.createRaceComment(comment, user, raceId);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/comments/{raceId}/{commentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long raceId,
            @PathVariable Long commentId
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        if (!user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        boolean isDeleteSuccess = commentService.deleteComment(commentId, raceId, user);
        if (isDeleteSuccess) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/comments/vote")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<CommentVoteResponseDTO> voteForComment(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody VoteRequest voteRequest
    ) {
        User user = userService.validateAndGetUserByUsername(currentUser.getUsername());
        CommentVoteResponseDTO commentVoteResponseDTO = commentService.voteComment(voteRequest.commentId(), voteRequest.isUpVote(), user);
        return new ResponseEntity<>(commentVoteResponseDTO, HttpStatus.OK);
    }
}
