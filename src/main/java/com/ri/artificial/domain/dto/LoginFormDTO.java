package com.ri.artificial.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ri
 * @date 2026-10-01 19:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}