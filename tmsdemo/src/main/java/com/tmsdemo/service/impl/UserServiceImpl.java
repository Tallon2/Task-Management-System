package com.tmsdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmsdemo.dto.LoginDTO;
import com.tmsdemo.dto.RegisterDTO;
import com.tmsdemo.entity.User;
import com.tmsdemo.exception.BusinessException;
import com.tmsdemo.mapper.UserMapper;
import com.tmsdemo.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements IUserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterDTO registerDTO) {
        boolean existsUsername = this.lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .count() > 0;
        if (existsUsername) {
            throw new BusinessException("用户名已存在");
        }

        boolean existsEmail = this.lambdaQuery()
                .eq(User::getEmail, registerDTO.getEmail())
                .count() > 0;
        if (existsEmail) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());

        this.save(user);
        return user;
    }

    @Override
    public User login(LoginDTO loginDTO) {
        User user = this.lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername())
                .one();
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user;
    }

    @Override
    public User getById(Long userId) {
        return baseMapper.selectById(userId);
    }
}
