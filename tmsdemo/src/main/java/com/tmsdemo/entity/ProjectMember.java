package com.tmsdemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectMember {
    private Long id;
    private Long projectId;
    private Long userId;
    private String role; // admin 或 member
    private LocalDateTime createdAt;
}
