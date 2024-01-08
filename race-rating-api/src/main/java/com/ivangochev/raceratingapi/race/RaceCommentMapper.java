package com.ivangochev.raceratingapi.race;

import com.ivangochev.raceratingapi.racecomment.RaceComment;
import com.ivangochev.raceratingapi.racecomment.RaceCommentResponseDTO;
import com.ivangochev.raceratingapi.rest.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class RaceCommentMapper {
    public RaceCommentResponseDTO toRaceCommentResponseDTO(RaceComment comment) {
       RaceCommentResponseDTO responseDTO = new RaceCommentResponseDTO();
       responseDTO.setId(comment.getId());
       responseDTO.setRaceId(comment.getRaceId());
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
}
