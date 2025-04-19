package com.stdev.team10.domain.point.dto;

import com.stdev.team10.domain.point.entity.PointUserElementStatusEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

@Getter
@Setter
public class PointUserElementStatusDto {

    @Schema(hidden = true)
    private Long pointUserElementStatusId;

    @Schema(example = "1")
    private Long userId;

    @Schema(example = "H")
    private String molecularFormula;

    public PointUserElementStatusEntity toEntity() {
        return PointUserElementStatusEntity.builder()
                .pointUserElementStatusId(this.pointUserElementStatusId)
                .userId(this.userId)
                .molecularFormula(this.molecularFormula)
                .build();
    }
}
