package com.tmsdemo.entity;

import lombok.Data;

@Data
public class TaskDependency {
    private Long id;
    private Long taskId;
    private Long dependsOnTaskId;
}
