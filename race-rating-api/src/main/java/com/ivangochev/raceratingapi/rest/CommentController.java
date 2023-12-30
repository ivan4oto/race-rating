package com.ivangochev.raceratingapi.rest;

import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.repository.RaceCommentRepository;
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
    private final RaceCommentRepository commentRepository;

    public CommentController(RaceCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comments/race/{raceId}")
    public ResponseEntity<List<RaceComment>> getAllCommentsForRace(@PathVariable Long raceId) {
        List<RaceComment> allComments = commentRepository.findAllByRaceId(raceId);
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }
}
