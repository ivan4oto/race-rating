package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.repository.RaceCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceCommentServiceImpl implements RaceCommentService{
    private final RaceCommentRepository commentRepository;

    public RaceCommentServiceImpl(RaceCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<RaceComment> getRaceCommentsByRaceId(Long raceId) {
        return commentRepository.findAllByRaceId(raceId);
    }
}
