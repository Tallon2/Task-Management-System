package com.tmsdemo.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tmsdemo.dto.ProjectDTO;
import com.tmsdemo.dto.ProjectMemberDTO;
import com.tmsdemo.entity.Project;
import com.tmsdemo.entity.ProjectMember;

import java.util.List;

public interface IProjectService extends IService<Project> {
    Project createProject(ProjectDTO projectDTO, Long userId);

    List<Project> getUserProjects(Long userId);

    IPage<Project> getUserProjectsPage(Integer pageNum, Integer pageSize, Long userId);

    void deleteProject(Long projectId, Long userId);

    ProjectMember inputMember(Long projectId, ProjectMemberDTO projectMemberDTO);
}
