package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.AuthRequest;
import com.example.taskmanagement.dto.AuthResponse;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.security.JwtUtil;
import com.example.taskmanagement.service.AuthService;
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
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody User user) {
        User registeredUser = authService.registerUser(user);
        String token = jwtUtil.generateToken(registeredUser.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, registeredUser.getEmail(), registeredUser.getRole()));
    }

    @PostMapping("/login")
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
