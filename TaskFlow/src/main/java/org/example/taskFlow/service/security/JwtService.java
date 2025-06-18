package org.example.taskFlow.service.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.example.taskFlow.dto.user_security.JwtRefreshToken;
import org.example.taskFlow.exception.security.AccessTokenUsedInsteadOfRefreshTokenException;
import org.example.taskFlow.exception.security.IncorrectTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


@Service
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secretKeyString;

    private SecretKey key;

    private final RedisTemplate<String, String> redisTemplate;
    private final String redisAccessTokenKey =  "accessToken:userId:";
    private final String redisRefreshTokenKey =  "refreshToken:userId:";

    public JwtService(RedisTemplate<String, String> redisTemplate) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, String userEmail, String sessionId) {
        long accessExpirationMs = 1000 * 60 * 10;
        return Jwts.builder()
                .subject(userId)
                .claim("email", userEmail)
                .claim("sessionId", sessionId)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userId, String role, String userEmail, String sessionId) {
        long refreshExpirationMs = 1000 * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .claim("email", userEmail)
                .claim("sessionId", sessionId)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extractUserRole(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role",  String.class);
    }

    public String extractUserEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email",  String.class);
    }

    public String extractSessionId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sessionId",  String.class);
    }

    public String extractType(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type",  String.class);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } throw new IncorrectTokenException();
    }

    public JwtRefreshToken extractRefreshPayload(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Токен не может быть пустым");
        }

        Claims claims = parseClaims(token);

        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new AccessTokenUsedInsteadOfRefreshTokenException();
        }

        if (claims.getSubject() == null || claims.get("email") == null || claims.get("sessionId") == null) {
            throw new IncorrectTokenException();
        }

        return new JwtRefreshToken(
                claims.getSubject(),
                claims.get("role", String.class),
                claims.get("email", String.class),
                claims.get("sessionId", String.class)
        );
    }

    private Claims parseClaims(String token) {
       return Jwts.parser()
               .verifyWith(key)
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }

    public boolean tokensInRedis(long userId, String sessionId) {
        String accessToken = redisTemplate.opsForValue().get(redisAccessTokenKey + userId + sessionId);
        String refreshToken = redisTemplate.opsForValue().get(redisRefreshTokenKey + userId + sessionId);
        return accessToken != null && refreshToken != null && !accessToken.isBlank() && !refreshToken.isBlank();
    }
}
