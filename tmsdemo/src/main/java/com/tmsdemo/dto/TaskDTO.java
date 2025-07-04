package com.tmsdemo.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题长度不能超过100个字符")
    private String title;

    @Size(max = 500, message = "任务描述长度不能超过500个字符")
    private String description;

    @NotBlank(message = "任务状态不能为空")
    private String status; // TODO, IN_PROGRESS, DONE

    @NotBlank(message = "任务优先级不能为空")
    private String priority; // HIGH, MEDIUM, LOW
}