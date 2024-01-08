package com.ivangochev.raceratingapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRatingRequest {

    @Schema(example = "tt0117998")
    @NotBlank
    private String imdb;

    @Schema(example = "Twister")
    @NotBlank
    private String title;

    @Schema(example = "https://m.media-amazon.com/images/M/MV5BODExYTM0MzEtZGY2Yy00N2ExLTkwZjItNGYzYTRmMWZlOGEzXkEyXkFqcGdeQXVyNDk3NzU2MTQ@._V1_SX300.jpg")
    private String poster;
}
