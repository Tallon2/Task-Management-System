package com.tmsdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

@Data
public class DependencyDTO {
    @NotNull(message = "依赖不可为空")
    private Long dependsOnTaskId;
}
