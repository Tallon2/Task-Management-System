package com.tmsdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmsdemo.dto.LoginDTO;
import com.tmsdemo.dto.RegisterDTO;
import com.tmsdemo.entity.User;

public interface IUserService extends IService<User> {
    User register(RegisterDTO registerDTO);
    User login(LoginDTO loginDTO);
    User getById(Long userId);
}