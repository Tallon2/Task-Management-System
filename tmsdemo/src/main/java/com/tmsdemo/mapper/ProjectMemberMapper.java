package com.tmsdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmsdemo.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
