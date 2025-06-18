package org.example.taskFlow.controller.auth;

import org.example.taskFlow.service.security.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        String code = emailService.generateUniqueCode();
        emailService.sendVerificationCode(email, code);
        return ResponseEntity.ok("Код отправлен на " + email);
    }
}

