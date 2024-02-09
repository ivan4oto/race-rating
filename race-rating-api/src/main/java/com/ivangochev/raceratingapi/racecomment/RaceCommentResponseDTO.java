package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.user.dto.UserResponseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class RaceCommentResponseDTO {
    private Long id;
    private Long raceId;

    private String commentText;
    private Date createdAt;
    private UserResponseDTO author;
}
