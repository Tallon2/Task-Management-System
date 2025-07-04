package com.tmsdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmsdemo.entity.Task;
import com.tmsdemo.dto.TaskDTO;
import com.tmsdemo.dto.TaskUpdateDTO;

import java.util.List;

public interface ITaskService extends IService<Task> {
    Task createTask(TaskDTO taskDTO, Long userId);

    List<Task> getProjectTasks(Long projectId, Long userId);

    IPage<Task> getProjectTasksPage(Page<Task> page, Long projectId, Long userId);

    IPage<Task> filterTasksByStatus(Page<Task> page, Long projectId, String status, Long userId);

    IPage<Task> filterTasksByPriority(Page<Task> page, Long projectId, String priority, Long userId);

    Task updateTask(Long taskId, TaskUpdateDTO updateDTO, Long userId);

    void deleteTask(Long taskId, Long userId);

    IPage<Task> sortTasks(Page<Task> page, Long projectId, String sortBy, boolean asc, Long userId);

    void addDependency(Long taskId, Long dependsOnTaskId, Long userId);
}
