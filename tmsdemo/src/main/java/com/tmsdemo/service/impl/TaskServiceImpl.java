package com.tmsdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmsdemo.dto.TaskDTO;
import com.tmsdemo.dto.TaskUpdateDTO;
import com.tmsdemo.entity.ProjectMember;
import com.tmsdemo.entity.Task;
import com.tmsdemo.entity.TaskDependency;
import com.tmsdemo.exception.BusinessException;
import com.tmsdemo.mapper.ProjectMapper;
import com.tmsdemo.mapper.ProjectMemberMapper;
import com.tmsdemo.mapper.TaskDependencyMapper;
import com.tmsdemo.mapper.TaskMapper;
import com.tmsdemo.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    private final ProjectMapper projectMapper;

    private final ProjectMemberMapper projectMemberMapper;

    private final TaskMapper taskMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final TaskDependencyMapper taskDependencyMapper;

    private void clearProjectCache(Long projectId) {
        // 删除该项目下所有task缓存
        Set<String> keys = redisTemplate.keys("task:filter:project:" + projectId + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    @Transactional
    @Override
    public Task createTask(TaskDTO taskDTO, Long userId) {
        checkProjectPermission(taskDTO.getProjectId(), userId,true);
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setProjectId(taskDTO.getProjectId());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        this.save(task);
        clearProjectCache(taskDTO.getProjectId());
        return task;
    }

    @Override
    public List<Task> getProjectTasks(Long projectId, Long userId) {
        checkProjectPermission(projectId, userId,false);
        return this.lambdaQuery()
                .eq(Task::getProjectId, projectId)
                .list();
    }

    @Override
    public IPage<Task> getProjectTasksPage(Page<Task> page, Long projectId, Long userId) {
        checkProjectPermission(projectId, userId,false);
        return this.lambdaQuery()
                .eq(Task::getProjectId, projectId)
                .page(page);
    }

    @Override
    public IPage<Task> filterTasksByStatus(Page<Task> page, Long projectId, String status, Long userId) {
        checkProjectPermission(projectId, userId,false);

        String key = String.format("task:filter:project:%d:status:%s:page:%d:%d",
                projectId, status, page.getCurrent(), page.getSize());

        // 先从 Redis 缓存获取
        IPage<Task> cachedPage = (IPage<Task>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            return cachedPage;
        }

        // 缓存未命中，从数据库查询
        IPage<Task> taskPage = this.lambdaQuery()
                .eq(Task::getProjectId, projectId)
                .eq(Task::getStatus, status)
                .page(page);

        // 写入缓存，设置过期时间 5 分钟
        redisTemplate.opsForValue().set(key, taskPage, 5, TimeUnit.MINUTES);

        return taskPage;
    }


    @Override
    public IPage<Task> filterTasksByPriority(Page<Task> page, Long projectId, String priority, Long userId) {
        checkProjectPermission(projectId, userId,false);

        String key = String.format("task:filter:project:%d:priority:%s:page:%d:%d",
                projectId, priority, page.getCurrent(), page.getSize());

        IPage<Task> cachedPage = (IPage<Task>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            return cachedPage;
        }

        IPage<Task> taskPage = this.lambdaQuery()
                .eq(Task::getProjectId, projectId)
                .eq(Task::getPriority, priority)
                .page(page);

        redisTemplate.opsForValue().set(key, taskPage, 5, TimeUnit.MINUTES);
        return taskPage;
    }

    @Transactional
    @Override
    public Task updateTask(Long taskId, TaskUpdateDTO updateDTO, Long userId) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        checkProjectPermission(task.getProjectId(), userId,true);

        if (updateDTO.getTitle() != null) task.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null) task.setDescription(updateDTO.getDescription());
        if (updateDTO.getStatus() != null) task.setStatus(updateDTO.getStatus());
        if (updateDTO.getPriority() != null) task.setPriority(updateDTO.getPriority());
        task.setUpdatedAt(LocalDateTime.now());

        this.updateById(task);
        clearProjectCache(task.getProjectId());
        return task;
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId, Long userId) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        checkProjectPermission(task.getProjectId(), userId,true);
        clearProjectCache(task.getProjectId());
        this.removeById(taskId);
    }

    @Override
    public IPage<Task> sortTasks(Page<Task> page, Long projectId, String sortBy, boolean asc, Long userId) {
        checkProjectPermission(projectId, userId,false);

        return this.lambdaQuery()
                .eq(Task::getProjectId, projectId)
                .orderBy(true, asc,
                        "priority".equals(sortBy) ? Task::getPriority : Task::getCreatedAt
                )
                .page(page);
    }

    @Override
    public void addDependency(Long taskId, Long dependsOnTaskId, Long userId) {
        Task task = taskMapper.selectById(taskId);
        Task dependsOnTask = taskMapper.selectById(dependsOnTaskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        checkProjectPermission(task.getProjectId(), userId,true);
        checkProjectPermission(dependsOnTask.getProjectId(), userId,true);

        TaskDependency dependency = new TaskDependency();
        dependency.setTaskId(taskId);
        dependency.setDependsOnTaskId(dependsOnTaskId);
        taskDependencyMapper.insert(dependency);

    }

    private void checkProjectPermission(Long projectId, Long userId, boolean requireAdmin) {
        ProjectMember member = projectMemberMapper.selectOne(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId));

        if (member == null) {
            throw new BusinessException("没有加入该项目，无权访问");
        }

        if (requireAdmin && !"admin".equals(member.getRole())) {
            throw new BusinessException("非管理员用户，无权修改");
        }
    }
}
