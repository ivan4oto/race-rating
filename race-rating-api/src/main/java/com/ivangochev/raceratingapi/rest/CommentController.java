package com.ivangochev.raceratingapi.rest;

import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.repository.RaceCommentRepository;
import com.ivangochev.raceratingapi.rest.dto.RaceCommentResponseDTO;
import com.ivangochev.raceratingapi.service.RaceCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
public class CommentController {
    private final RaceCommentService commentService;

    public CommentController(RaceCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments/race/{raceId}")
    public ResponseEntity<List<RaceCommentResponseDTO>> getAllCommentsForRace(@PathVariable Long raceId) {
        List<RaceCommentResponseDTO> allComments = commentService.getRaceCommentsByRaceId(raceId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }
}
