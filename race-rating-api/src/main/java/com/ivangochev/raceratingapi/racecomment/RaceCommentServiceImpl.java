package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.notification.event.RaceCommentCreatedEvent;
import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceNotFoundException;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVote;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteRepository;
import com.ivangochev.raceratingapi.racecomment.vote.CommentVoteResponseDTO;
import com.ivangochev.raceratingapi.racecomment.vote.UserCommentVoteStatus;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RaceCommentServiceImpl implements RaceCommentService{
    private final RaceCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RaceCommentMapper commentMapper;
    private final RaceRepository raceRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RaceCommentServiceImpl(
            UserRepository userRepository,
            RaceCommentRepository commentRepository,
            RaceCommentMapper commentMapper,
            RaceRepository raceRepository,
            CommentVoteRepository commentVoteRepository, ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.raceRepository = raceRepository;
        this.commentVoteRepository = commentVoteRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<RaceCommentWithVotesDto> getRaceCommentsByRaceId(Long raceId) {
        return commentRepository.findCommentsWithVoteCountsByRaceId(raceId);
    }

    @Override
    @Transactional
    public RaceCommentWithVotesDto createRaceComment(RaceCommentRequestDTO commentDto, User user, Long raceId) throws IllegalArgumentException {
        if (commentRepository.existsByAuthorIdAndRaceId(user.getId(), raceId)) {
            throw new IllegalArgumentException("User has already commented");
        }
        Race race = getRaceIfUserHasNotCommented(user, raceId);

        RaceComment newComment = commentMapper.toRaceComment(commentDto, user, race);
        RaceComment savedComment = commentRepository.save(newComment);
        user.getCommentedForRaces().add(savedComment.getRace());
        userRepository.save(user);

        eventPublisher.publishEvent(
                new RaceCommentCreatedEvent(this, savedComment.getId(), raceId, user.getId())
        );
        return commentMapper.toRaceCommentWithVotesDto(savedComment);
    }

    @Override
    public CommentVoteResponseDTO voteComment(Long commentId, boolean isUpvote, User user) {
        RaceComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentVote> existingVoteOpt = commentVoteRepository
                .findByCommentAndVoter(comment, user);
        if (existingVoteOpt.isPresent()) {
            CommentVote existingVote = existingVoteOpt.get();
            if (existingVote.isUpvote() != isUpvote) {
                log.info("User {} changed vote from {} to {}", user.getUsername(), existingVote.isUpvote(), isUpvote);
                existingVote.setUpvote(isUpvote);
                commentVoteRepository.save(existingVote);
                return new CommentVoteResponseDTO(true, isUpvote);// Change vote direction
            } else {
                // Vote is the same – no change
                return new CommentVoteResponseDTO(false, isUpvote);
            }
        } else {
            CommentVote newVote = new CommentVote();
            newVote.setComment(comment);
            newVote.setVoter(user);
            newVote.setUpvote(isUpvote);
            newVote.setVotedAt(new Date());
            log.info("User {} voted {} for comment {}", user.getUsername(), isUpvote, commentId);
            commentVoteRepository.save(newVote);
            return new CommentVoteResponseDTO(true, isUpvote);
        }
    }

    public List<UserCommentVoteStatus> getCommentVotesByCommentId(Long voterId, List<Long> commentIds) {
        List<UserCommentVoteStatus> commentVoteStatuses = commentVoteRepository.findByVoterIdAndCommentIdIn(voterId, commentIds);
        return commentVoteStatuses;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long raceId, User user) {
        log.info("Deleting comment with id {} for race with id {}", commentId, raceId);
        if (!commentRepository.existsById(commentId)) {
            return false; // Not found
        }
        user.getCommentedForRaces().removeIf(race -> race.getId().equals(raceId));
        userRepository.save(user);
        commentRepository.deleteById(commentId);
        return !commentRepository.existsById(commentId);

    }


    private Race getRaceIfUserHasNotCommented(User user, Long raceId) {
        return raceRepository.findById(raceId).map(race -> {
            if (userHasCommentedForRace(user, race)) {
                throw new UserAlreadyCommentedException("User has already commented for this race!");
            }
            return race;
        }).orElseThrow(() -> new RaceNotFoundException(
                String.format("Race with id %s not found", raceId))
        );
    }
    private boolean userHasCommentedForRace(User user, Race race) {
        return user.getCommentedForRaces().contains(race);
    }
}
