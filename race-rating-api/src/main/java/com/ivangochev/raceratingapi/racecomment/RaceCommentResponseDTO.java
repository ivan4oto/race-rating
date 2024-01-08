package com.ivangochev.raceratingapi.racecomment;

import com.ivangochev.raceratingapi.rest.dto.UserResponseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Data
public class RaceCommentResponseDTO {
    private Long id;
    private Long raceId;

    private String commentText;
    private Date createdAt;
    private UserResponseDTO author;
}
