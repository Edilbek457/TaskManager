package org.example.taskFlow.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskFlow.dto.user_security.*;
import org.example.taskFlow.enums.Role;
import org.example.taskFlow.model.User;
import org.example.taskFlow.service.UserService;
import org.example.taskFlow.service.security.SecurityService;
import org.example.taskFlow.service.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityController {

    private final JwtService jwtService;
    private final SecurityService securityService;
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserCreateRequest userCreateRequest, HttpServletRequest request) throws JsonProcessingException {
        String sessionId = request.getSession().getId();
        return ResponseEntity.status(HttpStatus.OK).body(securityService.register(userCreateRequest, sessionId));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<UserRegistrationResponse> verifyCode(@RequestParam String code, HttpServletRequest request) throws IOException {
        String sessionId = request.getSession().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(securityService.verifyCode(code, sessionId));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        String sessionId = newSession.getId();
        AuthResponse authResponse = securityService.login(loginRequest, sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @DeleteMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        String userId = jwtService.extractUserId(token);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        } securityService.logout(Long.parseLong(userId), request.getSession().getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/refresh")
    @PreAuthorize("hasAnyRole('USER', 'PROJECT_MANAGER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        String sessionId = newSession.getId();
        String toker = jwtService.extractToken(request);
        JwtRefreshToken jwtRefreshToken = jwtService.extractRefreshPayload(toker);
        AuthResponse authResponse = securityService.refresh(jwtRefreshToken, sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PutMapping("/change/role/{userId}")
    @PreAuthorize("hasAnyRole('SUPERVISOR')")
    public ResponseEntity<UserResponse> changeRole(HttpServletRequest request, @PathVariable long userId, @RequestParam Role role) {
        User user = userService.grantRoleByUserId(userId, role);
        UserResponse userResponse = UserResponse.from(user);
        securityService.logout(userId, request.getSession().getId());
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
