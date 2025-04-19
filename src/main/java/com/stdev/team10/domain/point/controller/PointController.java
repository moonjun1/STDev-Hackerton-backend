package com.stdev.team10.domain.point.controller;

import com.stdev.team10.domain.point.dto.PointDto;
import com.stdev.team10.domain.point.dto.PointUserElementStatusDto;
import com.stdev.team10.domain.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "포인트 추가, 차감 api 입니다", description = "유저의 포인트를 추가하거나 차감합니다.")
    @PostMapping
    public ResponseEntity<?> point(@RequestBody PointDto pointDto) {
        return pointService.point(pointDto);
    }

    @GetMapping("/search/{userName}")
    public ResponseEntity<?> searchUserPoint(@PathVariable String userName) {
        return pointService.searchUserPoint(userName);
    }

    @Operation(summary = "원소 구매 api 입니다", description = "구매하는 유저의 ip 값과 구매할 원소를 전달합니다.")
    @PostMapping("/element/add")
    public ResponseEntity<?> buyElement(@RequestBody PointUserElementStatusDto pointUserElementStatusDto) {
        return pointService.addElement(pointUserElementStatusDto);
    }

    @Operation(summary = "유저가 구매한 원소를 반환하는 api", description = "유저가 구매한 원소를 리스트로 반환합니다.")
    @GetMapping("/element/find/all/{userId}")
    public ResponseEntity<?> findAllElement(@PathVariable Long userId) {
        return pointService.findAllElement(userId);
    }



}
