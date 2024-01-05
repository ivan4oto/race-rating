package com.ivangochev.raceratingapi.service;

import com.ivangochev.raceratingapi.mapper.RaceCommentMapper;
import com.ivangochev.raceratingapi.model.RaceComment;
import com.ivangochev.raceratingapi.repository.RaceCommentRepository;
import com.ivangochev.raceratingapi.rest.dto.RaceCommentResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceCommentServiceImpl implements RaceCommentService{
    private final RaceCommentRepository commentRepository;
    private final RaceCommentMapper commentMapper;

    public RaceCommentServiceImpl(RaceCommentRepository commentRepository, RaceCommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<RaceCommentResponseDTO> getRaceCommentsByRaceId(Long raceId) {
        List<RaceComment> comments = commentRepository.findAllByRaceId(raceId);
        return comments.stream().map(commentMapper::toRaceCommentResponseDTO).toList();
    }
}
