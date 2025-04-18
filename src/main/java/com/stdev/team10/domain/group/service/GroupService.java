package com.stdev.team10.domain.group.service;

import com.stdev.team10.domain.group.entity.GroupEntity;
import com.stdev.team10.domain.group.repository.GroupRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public ResponseEntity<?> groupFindAll() {
        try {
            List<GroupEntity> groupEntityList = groupRepository.findAll();
            if(groupEntityList.isEmpty()) {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "그룹이 없습니다.", null));
            } else {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "그룹 조회 성공", groupEntityList));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "그룹 조회 실패", null));
        }
    }

    public ResponseEntity<?> groupFindByName(String groupName) {
        try {
            List<GroupEntity> groupEntityList = groupRepository.findByGroupNameContaining(groupName);
            if(groupEntityList.isEmpty()) {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "그룹이 없습니다.", null));
            } else {
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "그룹 조회 성공", groupEntityList));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "그룹 조회 실패", null));
        }
    }
}
