package com.tmsdemo.controller;


import com.tmsdemo.dto.LoginDTO;
import com.tmsdemo.dto.RegisterDTO;
import com.tmsdemo.dto.Result;
import com.tmsdemo.entity.User;
import com.tmsdemo.service.IUserService;
import com.tmsdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        String token = jwtUtil.generateToken(user.getId().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success("登录成功", data);
    }
}
