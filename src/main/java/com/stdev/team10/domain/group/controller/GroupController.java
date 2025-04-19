package com.stdev.team10.domain.group.controller;

import com.stdev.team10.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
@Tag(name = "그룹 관련 api입니다", description = "국내 초,중,고 이을 조회할 수 있습니다.")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "그룹 목록 전체 조회 api", description = "모든 그룹 정보를 조회합니다.")
    @GetMapping("/find/all")
    public ResponseEntity<?> groupFindAll() {
        return groupService.groupFindAll();
    }

    @Operation(summary = "그룹 이름으로 조회 api", description = "그룹 이름으로 조회합니다.")
    @GetMapping("/find/{groupName}")
    public ResponseEntity<?> groupFindByName(@PathVariable String groupName) {
        return groupService.groupFindByName(groupName);
    }
}
