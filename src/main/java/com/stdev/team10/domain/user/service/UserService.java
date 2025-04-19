package com.stdev.team10.domain.user.service;

import com.stdev.team10.domain.user.dto.UserDto;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> registerUser(UserDto userDto) {
        try {
            if(userRepository.existsByUserName(userDto.getUserName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 이름입니다.", null));
            } else {
                userDto.setUserPoint(0L); // 초기 포인트 설정
                UserEntity userEntity = userRepository.save(userDto.toEntity());
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "사용자 정보 저장 성공", userEntity));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보 저장 실패: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<?> getUsernameById(Long userId) {
        try {
            if(userRepository.existsById(userId)){
                UserEntity userEntity = userRepository.findById(userId).orElse(null);
                return ResponseEntity.ok()
                        .body(ResponseDto.response(HttpStatus.OK, "사용자 이름 조회 성공", userEntity.getUserName()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 이름 조회 실패", null));
        }
    }


    public ResponseEntity<?> getIdByUsername(String userName) {
        try {
            if(userRepository.existsByUserName(userName)){
                UserEntity userEntity = userRepository.findByUserName(userName);
                return ResponseEntity.ok()
                        .body(ResponseDto.response(HttpStatus.OK, "사용자 id 조회 성공", userEntity.getUserId()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 id 조회 실패", null));
        }
    }


}