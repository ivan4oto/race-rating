package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.user.User;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Override
    public RaceCommentResponseDTO createRaceComment(RaceCommentRequestDTO comment, User user, Long raceId) throws IllegalArgumentException {
        if (commentRepository.existsByAuthorIdAndRaceId(user.getId(), raceId)) {
            throw new IllegalArgumentException("User has already commented");
        }

        RaceComment newComment = new RaceComment();
        newComment.setAuthor(user);
        newComment.setCommentText(comment.getCommentText());
        newComment.setRaceId(raceId);
        newComment.setCreatedAt(new Date());

        RaceComment savedComment = commentRepository.save(newComment);
        return commentMapper.toRaceCommentResponseDTO(savedComment);
    }
}
