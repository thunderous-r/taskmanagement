package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.AuthRequest;
import com.example.taskmanagement.dto.AuthResponse;
import com.example.taskmanagement.dto.RegisterRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.security.JwtUtil;
import com.example.taskmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и аутентификации пользователей")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает новую учетную запись пользователя и возвращает JWT токен для аутентификации"
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User registeredUser = authService.registerUser(registerRequest);
        String token = jwtUtil.generateToken(registeredUser.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, registeredUser.getEmail(), registeredUser.getRole()));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Аутентифицирует пользователя и возвращает JWT токен для дальнейшего доступа к API"
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        User user = authService.findByEmail(authRequest.getEmail());
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole()));
    }
}
