package com.tmsdemo.controller;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmsdemo.dto.DependencyDTO;
import com.tmsdemo.entity.Task;
import com.tmsdemo.dto.TaskDTO;
import com.tmsdemo.dto.TaskUpdateDTO;
import com.tmsdemo.service.ITaskService;
import com.tmsdemo.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService ITaskService;

    @PostMapping
    public Result<Task> createTask(@RequestBody TaskDTO taskDTO, @RequestAttribute Long userId) {
        Task task = ITaskService.createTask(taskDTO, userId);
        return Result.success("任务创建成功", task);
    }

    @GetMapping("/project/{projectId}")
    public Result<List<Task>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestAttribute Long userId) {
        List<Task> tasks = ITaskService.getProjectTasks(projectId, userId);
        return Result.success(tasks);
    }

    @GetMapping("/project/{projectId}/page")
    public Result<IPage<Task>> getProjectTasksPage(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        IPage<Task> taskPage = ITaskService.getProjectTasksPage(page, projectId, userId);
        return Result.success(taskPage);
    }

    @GetMapping("/project/{projectId}/filter/status")
    public Result<IPage<Task>> filterTasksByStatus(
            @PathVariable Long projectId,
            @RequestParam String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        IPage<Task> taskPage = ITaskService.filterTasksByStatus(page, projectId, status, userId);
        return Result.success(taskPage);
    }
    @GetMapping("/project/{projectId}/sort")
    public Result<IPage<Task>> sortTasks(
            @PathVariable Long projectId,
            @RequestParam String orderBy, // "priority" 或 "createdAt"
            @RequestParam(defaultValue = "true") boolean asc,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId
    ) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        IPage<Task> taskPage = ITaskService.sortTasks(page, projectId, orderBy, asc, userId);
        return Result.success(taskPage);
    }

    @GetMapping("/project/{projectId}/filter/priority")
    public Result<IPage<Task>> filterTasksByPriority(
            @PathVariable Long projectId,
            @RequestParam String priority,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        IPage<Task> taskPage = ITaskService.filterTasksByPriority(page, projectId, priority, userId);
        return Result.success(taskPage);
    }

    @PutMapping("/{taskId}")
    public Result<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateDTO updateDTO,
            @RequestAttribute Long userId) {
        Task task = ITaskService.updateTask(taskId, updateDTO, userId);
        return Result.success("任务更新成功", task);
    }
    @PostMapping("/{taskId}/dependency")
    public Result<?> addDependency(
            @PathVariable Long taskId,
            @RequestBody DependencyDTO dependencyDTO,
            @RequestAttribute Long userId) {
        ITaskService.addDependency(taskId, dependencyDTO.getDependsOnTaskId(), userId);
        return Result.success("任务依赖关系已添加");
    }

    @DeleteMapping("/{taskId}")
    public Result<?> deleteTask(@PathVariable Long taskId, @RequestAttribute Long userId) {
        ITaskService.deleteTask(taskId, userId);
        return Result.success("任务删除成功");
    }
}