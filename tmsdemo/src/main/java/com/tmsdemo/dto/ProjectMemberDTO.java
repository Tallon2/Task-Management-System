package com.tmsdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectMemberDTO {
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 10, message = "用户ID长度不能超过10个字符")
    private Long userId;

    @Size(max =10, message = "角色长度不能超过10个字符")
    private String role;
}
