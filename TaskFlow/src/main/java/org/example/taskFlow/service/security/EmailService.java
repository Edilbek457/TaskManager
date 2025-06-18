package org.example.taskFlow.service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String mailSendEmail;
    private final String verificationCode = "verification:code:";
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public EmailService(JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🔐 Код подтверждения");
        message.setText("Ваш код подтверждения: " + code);
        message.setFrom(mailSendEmail);
        mailSender.send(message);
    }

    public String generateUniqueCode() {
        for (int i = 0; i < 25; i++) {
            int number = ThreadLocalRandom.current().nextInt(0, 100_000);
            String code = String.format("%05d", number);
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(verificationCode + code, "active", Duration.ofMinutes(3));
            if (Boolean.TRUE.equals(success)) {
                return code;
            }
        } throw new IllegalStateException("Не удалось сгенерировать уникальный код");
    }

    public boolean verifyCode(String code) {
        return !redisTemplate.opsForValue().get(verificationCode + code).equals("active");
    }
}
