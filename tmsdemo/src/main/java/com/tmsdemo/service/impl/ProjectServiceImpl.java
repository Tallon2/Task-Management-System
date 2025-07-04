package com.tmsdemo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmsdemo.dto.ProjectDTO;
import com.tmsdemo.dto.ProjectMemberDTO;
import com.tmsdemo.entity.Project;
import com.tmsdemo.entity.ProjectMember;
import com.tmsdemo.exception.BusinessException;
import com.tmsdemo.mapper.ProjectMapper;
import com.tmsdemo.mapper.ProjectMemberMapper;
import com.tmsdemo.mapper.TaskMapper;
import com.tmsdemo.service.IProjectService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.tmsdemo.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl
        extends ServiceImpl<ProjectMapper, Project>
        implements IProjectService {

    private final TaskMapper taskMapper;
    @Resource
    private final ProjectMapper projectMapper;
    @Resource
    private final ProjectMemberMapper projectMemberMapper;
    @Transactional
    public Project createProject(ProjectDTO projectDTO, Long userId) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setUserId(userId);
        project.setCreatedAt(LocalDateTime.now());
        projectMapper.insert(project);

        // 新增：项目创建者自动成为管理员
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(userId);
        member.setRole("admin");
        member.setCreatedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);

        return project;
    }

    @Override
    public List<Project> getUserProjects(Long userId) {
        return this.lambdaQuery()
                .eq(Project::getUserId, userId)
                .list();
    }

    @Override
    public IPage<Project> getUserProjectsPage(Integer pageNum, Integer pageSize, Long userId) {
        Page<Project> page = new Page<>(pageNum, pageSize);
        return this.lambdaQuery()
                .eq(Project::getUserId, userId)
                .page(page);
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        Project project = this.getById(projectId);
        if (project == null || !project.getUserId().equals(userId)) {
            throw new BusinessException("项目不存在或无权操作");
        }

        // 删除项目下所有任务
        taskMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Task>()
                .eq(Task::getProjectId, projectId));

        // 删除项目
        this.removeById(projectId);
    }

    @Override
    public ProjectMember inputMember(Long projectId, ProjectMemberDTO projectMemberDTO) {
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(projectMemberDTO.getUserId());
        member.setRole(projectMemberDTO.getRole());
        member.setCreatedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);

        return member;
    }
}

