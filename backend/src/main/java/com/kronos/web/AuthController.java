package com.kronos.web;

import com.kronos.service.AuthService;
import com.kronos.web.dto.AuthDtos.AuthResponse;
import com.kronos.web.dto.AuthDtos.LoginRequest;
import com.kronos.web.dto.AuthDtos.MeResponse;
import com.kronos.web.dto.AuthDtos.RefreshRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    /** Who am I? Requires a valid access token. */
    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        return new MeResponse(authentication.getName(),
                authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }
}
