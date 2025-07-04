package com.tmsdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tmsdemo.entity.TaskDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskDependencyMapper extends BaseMapper<TaskDependency> {
    List<TaskDependency> selectByTaskId(@Param("taskId") Long taskId);
}
