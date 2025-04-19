package com.stdev.team10.domain.user.controller;

import com.stdev.team10.domain.user.dto.UserDto;
import com.stdev.team10.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "유저 관련 api 입니다", description = "")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "유저 등록", description = "이름이랑 조직 넣으시면 됩니다")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @GetMapping("/getUsernameById/{userId}")
    @Operation(summary = "유저 닉네임 조회", description = "유저 아이디로 닉네임을 조회합니다")
    public ResponseEntity<?> getUsernameById(@PathVariable Long userId) {
        return userService.getUsernameById(userId);
    }

    @GetMapping("/getIdByUsername/{userName}")
    @Operation(summary = "유저 아이디 조회", description = "유저 닉네임으로 아이디를 조회합니다")
    public ResponseEntity<?> getIdByUsername(@PathVariable String userName) {
        return userService.getIdByUsername(userName);
    }
}