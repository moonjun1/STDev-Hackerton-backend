package com.stdev.team10.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointDto {

    @Schema(example = "홍길동")
    private String userName;

    @Schema(type = "string", example = "add")
    private String type;

    @Schema(example = "100")
    private Long point;
}
