package com.kronos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Creates and verifies signed JWTs. Access and refresh tokens carry a "type"
 * claim so one can never be used in place of the other.
 */
@Service
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpiryMs;
    private final long refreshExpiryMs;

    public JwtService(@Value("${app.jwt.secret}") String base64Secret,
                      @Value("${app.jwt.access-expiry-ms}") long accessExpiryMs,
                      @Value("${app.jwt.refresh-expiry-ms}") long refreshExpiryMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessExpiryMs = accessExpiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    public String generateAccessToken(UserDetails user) {
        return build(user, TYPE_ACCESS, accessExpiryMs);
    }

    public String generateRefreshToken(UserDetails user) {
        return build(user, TYPE_REFRESH, refreshExpiryMs);
    }

    private String build(UserDetails user, String type, long expiryMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", type)
                .claim("authorities", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(key)
                .compact();
    }

    /** Verifies signature and expiry; throws a JwtException for a bad or expired token. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token, UserDetails user, String expectedType) {
        Claims claims = parse(token);
        return claims.getSubject().equals(user.getUsername())
                && expectedType.equals(claims.get("type", String.class));
    }

    public long getAccessExpirySeconds() {
        return accessExpiryMs / 1000;
    }
}
