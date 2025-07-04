package com.tmsdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmsdemo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    // 什么都不用写，直接用 MyBatis-Plus 提供的方法
}