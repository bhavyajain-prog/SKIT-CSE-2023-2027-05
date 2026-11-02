package com.kronos.service;

import com.kronos.security.CustomUserDetailsService;
import com.kronos.security.JwtService;
import com.kronos.web.dto.AuthDtos.AuthResponse;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       CustomUserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /** Throws an AuthenticationException (mapped to 401) for a wrong email or password. */
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return tokensFor(userDetailsService.loadUserByUsername(email));
    }

    /** Exchanges a valid refresh token for a new token pair. */
    public AuthResponse refresh(String refreshToken) {
        Claims claims = jwtService.parse(refreshToken);
        if (!JwtService.TYPE_REFRESH.equals(claims.get("type", String.class))) {
            throw new BadCredentialsException("Not a refresh token");
        }
        UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account disabled");
        }
        return tokensFor(user);
    }

    private AuthResponse tokensFor(UserDetails user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer",
                jwtService.getAccessExpirySeconds());
    }
}
