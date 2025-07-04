package com.tmsdemo.dto;



import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskUpdateDTO {
    @Size(max = 100, message = "任务标题长度不能超过100个字符")
    private String title;

    @Size(max = 500, message = "任务描述长度不能超过500个字符")
    private String description;

    private String status; // TODO, IN_PROGRESS, DONE

    private String priority; // HIGH, MEDIUM, LOW
}