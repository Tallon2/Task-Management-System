package com.tmsdemo.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmsdemo.dto.ProjectDTO;
import com.tmsdemo.dto.ProjectMemberDTO;
import com.tmsdemo.dto.Result;
import com.tmsdemo.entity.Project;
import com.tmsdemo.entity.ProjectMember;
import com.tmsdemo.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final IProjectService projectService;

    @PostMapping
    public Result<Project> createProject(@RequestBody ProjectDTO projectDTO, @RequestAttribute Long userId) {
        Project project = projectService.createProject(projectDTO, userId);
        return Result.success("项目创建成功", project);
    }

    @GetMapping
    public Result<List<Project>> getUserProjects(@RequestAttribute Long userId) {
        List<Project> projects = projectService.getUserProjects(userId);
        return Result.success(projects);
    }

    @GetMapping("/page")
    public Result<IPage<Project>> getUserProjectsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId) {
        IPage<Project> projectPage = projectService.getUserProjectsPage(pageNum, pageSize, userId);
        return Result.success(projectPage);
    }

    @DeleteMapping("/{projectId}")
    public Result<?> deleteProject(@PathVariable Long projectId, @RequestAttribute Long userId) {
        projectService.deleteProject(projectId, userId);
        return Result.success("项目删除成功");
    }

    @PostMapping("/{projectId}/members")
    public Result<?> InputMember(@PathVariable Long projectId, @RequestBody ProjectMemberDTO projectMemberDTO){
          ProjectMember projectMember=projectService.inputMember(projectId,projectMemberDTO);
          return  Result.success(projectMember);
    }
}
