package com.example.taskmanagement.dto;

import com.example.taskmanagement.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными аутентификации")
public class AuthResponse {
    private String token;
    private String email;
    private Role role;
}
