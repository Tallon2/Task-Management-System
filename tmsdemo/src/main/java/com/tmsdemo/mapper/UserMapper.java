package com.tmsdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmsdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 不需要任何额外方法
}
