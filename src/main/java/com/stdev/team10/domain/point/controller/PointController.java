package com.stdev.team10.domain.point.controller;

import com.stdev.team10.domain.point.dto.PointDto;
import com.stdev.team10.domain.point.service.PointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@Tag(name = "포인트 관련 api입니다", description = "포인트 추가, 차감, 조회를 할 수 있습니다. type = add, sub")
public class PointController {

    @Autowired
    private PointService pointService;

    @PostMapping
    public ResponseEntity<?> point(@RequestBody PointDto pointDto) {
        return pointService.point(pointDto);
    }

    @GetMapping("/search/{userName}")
    public ResponseEntity<?> searchUserPoint(@PathVariable String userName) {
        return pointService.searchUserPoint(userName);
    }


}
