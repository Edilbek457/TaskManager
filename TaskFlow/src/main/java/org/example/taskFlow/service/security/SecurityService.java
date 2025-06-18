package org.example.taskFlow.service.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.user_security.*;
import org.example.taskFlow.exception.user.IncorrectCodeInputException;
import org.example.taskFlow.exception.user.PasswordIncorrectException;
import org.example.taskFlow.exception.user.UserUnregisteredException;
import org.example.taskFlow.model.User;
import org.example.taskFlow.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplateUser;
    private final RedisTemplate<String, String> redisTemplate;
    private final String redisAccessTokenKey =  "accessToken:userId:";
    private final String redisRefreshTokenKey =  "refreshToken:userId:";
    private final String redisCodeAndSessionId = "codeAndSessionId:";

    public String register(UserCreateRequest userCreateRequest, String sessionId) throws JsonProcessingException {
        if (userService.thisIsNewUserByEmail(userCreateRequest.email())) {
            String generatedCode = emailService.generateUniqueCode();
            String json = objectMapper.writeValueAsString(userCreateRequest);
            redisTemplateUser.opsForValue().set(redisCodeAndSessionId + sessionId + generatedCode, json, Duration.ofMinutes(3));
            emailService.sendVerificationCode(userCreateRequest.email(), generatedCode);
            return "Код отправлен";
        } else {
            throw new UserUnregisteredException(userCreateRequest.email());
        }
    }

    public UserRegistrationResponse verifyCode(String code, String sessionId) throws IOException {
        Object object = redisTemplateUser.opsForValue().get(redisCodeAndSessionId + sessionId + code);
        if (object != null) {
            UserCreateRequest userCreateRequest = objectMapper.readValue((String) object, UserCreateRequest.class);
            User user = userService.saveUser(userCreateRequest);
            passwordService.save(user.getId(), userCreateRequest.password());
            AuthResponse authResponse = generateAndPutToRedis(user, sessionId);
            UserRegistrationResponse response = new UserRegistrationResponse(authResponse, user);
            redisTemplateUser.delete(redisCodeAndSessionId + sessionId + code);
            return response;
        } else {
            throw new IncorrectCodeInputException();
        }
    }


    public AuthResponse login(LoginRequest loginRequest, String sessionId) {
        User user = userService.getUserByEmailWithoutRedis(loginRequest.email());
        if (passwordService.passwordIsValid(user.getId(), loginRequest.password())) {
            return generateAndPutToRedis(user, sessionId);
        } else {
            throw new PasswordIncorrectException();
        }
    }

    public void logout(long userId, String sessionId) {
        redisTemplate.delete(redisAccessTokenKey + userId + sessionId);
        redisTemplate.delete(redisRefreshTokenKey + userId + sessionId);
    }

    public AuthResponse refresh(JwtRefreshToken jwtRefreshToken, String newSessionId) {
        String userId = jwtRefreshToken.userId();
        String oldSessionId = jwtRefreshToken.session();

        redisTemplate.delete(redisAccessTokenKey + userId + oldSessionId);
        redisTemplate.delete(redisRefreshTokenKey + userId + oldSessionId);

        User user = userService.getUserById(Long.parseLong(userId));
        return generateAndPutToRedis(user, newSessionId);
    }


    private AuthResponse generateAndPutToRedis(User user, String sessionId) {
        String userId = String.valueOf(user.getId()), userEmail = user.getEmail(), userRole = String.valueOf(user.getRole());
        String access = jwtService.generateAccessToken(userId, userEmail, sessionId);
        String refresh = jwtService.generateRefreshToken(userId, userRole, userEmail, sessionId);
        redisTemplate.opsForValue().set(redisAccessTokenKey + userId + sessionId, access, Duration.ofMinutes(15));
        redisTemplate.opsForValue().set(redisRefreshTokenKey + userId + sessionId, refresh, Duration.ofDays(7));
        return new AuthResponse(access, refresh);
    }
}
