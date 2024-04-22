package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.race.RaceNotFoundException;
import com.ivangochev.raceratingapi.race.RaceRepository;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RaceCommentServiceImpl implements RaceCommentService{
    private final RaceCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RaceCommentMapper commentMapper;
    private final RaceRepository raceRepository;

    public RaceCommentServiceImpl(
            UserRepository userRepository,
            RaceCommentRepository commentRepository,
            RaceCommentMapper commentMapper,
            RaceRepository raceRepository
    ) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.raceRepository = raceRepository;
    }

    @Override
    public List<RaceCommentResponseDTO> getRaceCommentsByRaceId(Long raceId) {
        List<RaceComment> comments = commentRepository.findAllByRaceId(raceId);
        return comments.stream().map(commentMapper::toRaceCommentResponseDTO).toList();
    }

    @Override
    public RaceCommentResponseDTO createRaceComment(RaceCommentRequestDTO commentDto, User user, Long raceId) throws IllegalArgumentException {
        if (commentRepository.existsByAuthorIdAndRaceId(user.getId(), raceId)) {
            throw new IllegalArgumentException("User has already commented");
        }
        Race race = getRaceIfUserHasNotCommented(user, raceId);

        RaceComment newComment = commentMapper.toRaceComment(commentDto, user, race);
        RaceComment savedComment = commentRepository.save(newComment);
        user.getCommentedForRaces().add(savedComment.getRace());
        userRepository.save(user);
        return commentMapper.toRaceCommentResponseDTO(savedComment);
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
