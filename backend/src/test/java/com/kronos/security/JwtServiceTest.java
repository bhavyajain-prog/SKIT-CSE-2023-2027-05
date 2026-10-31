package com.kronos.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=";

    private final UserDetails user = User.withUsername("a@b.com").password("x")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_FACULTY"))).build();

    @Test
    void accessTokenRoundTrip() {
        JwtService service = new JwtService(SECRET, 60_000, 120_000);
        String token = service.generateAccessToken(user);
        assertEquals("a@b.com", service.parse(token).getSubject());
        assertTrue(service.isValid(token, user, JwtService.TYPE_ACCESS));
    }

    @Test
    void refreshTokenIsNotAnAccessToken() {
        JwtService service = new JwtService(SECRET, 60_000, 120_000);
        String refresh = service.generateRefreshToken(user);
        assertFalse(service.isValid(refresh, user, JwtService.TYPE_ACCESS));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1000, -1000);
        String token = service.generateAccessToken(user);
        assertThrows(ExpiredJwtException.class, () -> service.parse(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService service = new JwtService(SECRET, 60_000, 120_000);
        String token = service.generateAccessToken(user) + "x";
        assertThrows(JwtException.class, () -> service.parse(token));
    }
}
