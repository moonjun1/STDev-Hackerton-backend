package com.stdev.team10.domain.user.service;

import com.stdev.team10.domain.user.dto.UserDto;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> saveUser(UserDto userDto) {
        try {
            UserEntity userEntity = userDto.toEntity();
            userEntity = userRepository.save(userEntity);
            userDto.setUserId(userEntity.getUserId());
            return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "사용자 정보 저장 성공", userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보 저장 실패: " + e.getMessage(), null));
        }



    }
}