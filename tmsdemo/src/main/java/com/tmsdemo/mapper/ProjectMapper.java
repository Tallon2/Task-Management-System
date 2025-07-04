package com.tmsdemo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmsdemo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 可以什么都不写
}
