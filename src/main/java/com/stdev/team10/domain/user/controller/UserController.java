package com.stdev.team10.domain.user.controller;

import com.stdev.team10.domain.user.dto.UserDto;
import com.stdev.team10.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Tag(name = "로그인 할떄", description = "이름이랑 ,조직 넣으시면 됩니다")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }
}