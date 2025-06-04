package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.race.Race;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RaceCommentMapper {
    public RaceCommentResponseDTO toRaceCommentResponseDTO(RaceComment comment) {
       RaceCommentResponseDTO responseDTO = new RaceCommentResponseDTO();
       responseDTO.setId(comment.getId());
       responseDTO.setRaceId(comment.getRace().getId());
       responseDTO.setCommentText(comment.getCommentText());
       responseDTO.setCreatedAt(comment.getCreatedAt());
       responseDTO.setAuthor(new UserResponseDTO(
               comment.getAuthor().getId(),
               comment.getAuthor().getUsername(),
               comment.getAuthor().getName(),
               comment.getAuthor().getEmail(),
               comment.getAuthor().getImageUrl()
       ));
       return responseDTO;
    }

    public RaceCommentWithVotesDto toRaceCommentWithVotesDto(RaceComment comment) {
        RaceCommentWithVotesDto responseDTO = new RaceCommentWithVotesDto();
        responseDTO.setId(comment.getId());
        responseDTO.setCommentText(comment.getCommentText());
        responseDTO.setCreatedAt(comment.getCreatedAt());
        responseDTO.setAuthorName(comment.getAuthor().getName());
        responseDTO.setAuthorImageUrl(comment.getAuthor().getImageUrl());
        responseDTO.setUpvoteCount(0L);
        responseDTO.setDownvoteCount(0L);
        return responseDTO;
    }

    public RaceComment toRaceComment(RaceCommentRequestDTO requestDTO, User author, Race race) {
        RaceComment comment = new RaceComment();
        comment.setCommentText(requestDTO.getCommentText());
        comment.setRace(race);
        comment.setAuthor(author);
        comment.setCreatedAt(new Date());
        return comment;
    }
}
