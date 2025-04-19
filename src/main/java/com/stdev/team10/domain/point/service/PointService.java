package com.stdev.team10.domain.point.service;

import com.stdev.team10.domain.point.dto.PointDto;
import com.stdev.team10.domain.point.dto.PointUserElementStatusDto;
import com.stdev.team10.domain.point.entity.PointUserElementStatusEntity;
import com.stdev.team10.domain.point.repository.PointUserElementStatusRepository;
import com.stdev.team10.domain.user.entity.UserEntity;
import com.stdev.team10.domain.user.repository.UserRepository;
import com.stdev.team10.global.common.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PointService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointUserElementStatusRepository pointUserElementStatusRepository;

    public ResponseEntity<?> point(PointDto pointDto) {
        try {
            if(pointDto.getType().equals("add")) {
                UserEntity userEntity = null;
                if (userRepository.existsByUserName(pointDto.getUserName())) {
                    userEntity = userRepository.findByUserName(pointDto.getUserName());
                    userEntity.setUserPoint(userEntity.getUserPoint() + pointDto.getPoint());
                    userRepository.save(userEntity);
                    return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "포인트 추가 성공", userEntity.getUserPoint()));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", userEntity.getUserPoint()));
                }
            }
            else if(pointDto.getType().equals("sub")){
                if(userRepository.existsByUserName(pointDto.getUserName())){
                    UserEntity userEntity = userRepository.findByUserName(pointDto.getUserName());

                    if(userEntity.getUserPoint() < pointDto.getPoint()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "포인트가 부족합니다.", userEntity.getUserPoint()));
                    }
                    userEntity.setUserPoint(userEntity.getUserPoint() - pointDto.getPoint());
                    userRepository.save(userEntity);
                    return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "포인트 차감 성공", userEntity.getUserPoint()));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "지원되지 않는 형식입니다.", null));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 처리 실패" + e.getMessage(), null));
        }
    }

    public ResponseEntity<?> searchUserPoint(String userName) {
        try {
            if (userRepository.existsByUserName(userName)) {
                UserEntity userEntity = userRepository.findByUserName(userName);
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "포인트 조회 성공", userEntity.getUserPoint()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 조회 실패" + e.getMessage(), null));
        }
    }

    public ResponseEntity<?> addElement(PointUserElementStatusDto pointUserElementStatusDto) {
        pointUserElementStatusDto.setMolecularFormula(pointUserElementStatusDto.getMolecularFormula().toUpperCase());
        List<PointUserElementStatusEntity> pointUserElementStatusEntityList = pointUserElementStatusRepository.findByUserId(pointUserElementStatusDto.getUserId());
        try {

            if(!userRepository.existsById(pointUserElementStatusDto.getUserId())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
            }

            if(!pointUserElementStatusEntityList.isEmpty()){
                for (PointUserElementStatusEntity pointUserElementStatusEntity : pointUserElementStatusEntityList) {
                    if (pointUserElementStatusEntity.getMolecularFormula().equals(pointUserElementStatusDto.getMolecularFormula())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "이미 구매한 원소입니다.", null));
                    }
                }
                pointUserElementStatusRepository.save(pointUserElementStatusDto.toEntity());
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "원소 구매 성공", pointUserElementStatusDto.getMolecularFormula()));
            } else {
                pointUserElementStatusRepository.save(pointUserElementStatusDto.toEntity());
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "원소 구매 성공", pointUserElementStatusDto.getMolecularFormula()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "원소 구매 실패", null));
        }
    }

    public ResponseEntity<?> findAllElement(Long userId) {
        try {
            if(!userRepository.existsById(userId)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseDto.response(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다.", null));
            } else {
                List<PointUserElementStatusEntity> pointUserElementStatusEntityList = pointUserElementStatusRepository.findByUserId(userId);
                return ResponseEntity.ok().body(ResponseDto.response(HttpStatus.OK, "조회 성공", pointUserElementStatusEntityList));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDto.response(HttpStatus.INTERNAL_SERVER_ERROR, "조회 실패", null));
        }
    }


}
