package org.example.taskFlow.service.security;

import lombok.RequiredArgsConstructor;
import org.example.taskFlow.enums.PasswordStrengthLevel;
import org.example.taskFlow.exception.user.PasswordNotFoundException;
import org.example.taskFlow.model.Password;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.PasswordRepository;
import org.example.taskFlow.service.UserService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserService userService;
    private final PasswordRepository passwordRepository;

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 4);

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }

    public void save(long userId, String password) {
        User user = userService.getUserById(userId);
        Password createdPassword = createPassword(user, password);
        passwordRepository.save(createdPassword);
    }

    public void update(long userId, String password) {
        Password foundedPassword = getPasswordByUserId(userId);
        Password createdPassword = createPassword(foundedPassword.getUser(), password);
        passwordRepository.save(createdPassword);
    }

    public byte getStrengthNumberLevel(String password) {
          if (password == null || password.isBlank()) return 0;

          byte strengthScore = 0;
          if (password.length() >= 8) strengthScore++;
          if (password.length() >= 16) strengthScore+=2;
          if (password.length() >= 32) strengthScore+=4;
          if (password.length() >= 64) strengthScore+=7;
          if (password.matches(".*[A-Z].*")) strengthScore++;
          if (password.matches(".*[a-z].*")) strengthScore++;
          if (password.matches(".*[А-ЯЁ].*"))  strengthScore++;
          if (password.matches(".*[a-яё].*")) strengthScore++;
          if (password.matches(".*\\d.*")) strengthScore++;
          if (password.matches(".*[*@#$%^&+=?!].*")) strengthScore+=2;
          if (password.matches(".*[()\\-_{}\\[\\]:;\"'|<>,./~`].*")) strengthScore+=2;
          return strengthScore;
    }

    public PasswordStrengthLevel getStrengthLevel(String password) {
        byte strengthScore = getStrengthNumberLevel(password);
        if (strengthScore < 5) return PasswordStrengthLevel.WEAK;
        if (strengthScore >= 5) return PasswordStrengthLevel.MEDIUM;
        if (strengthScore >= 8) return PasswordStrengthLevel.STRONG;
        return PasswordStrengthLevel.VERY_STRONG;
    }

    public boolean passwordIsValid(long userId, String password) {
        if (password == null || password.isBlank()) return false;
        userService.getUserById(userId);
        Password findingPassword = getPasswordByUserId(userId);
        return matches(password, findingPassword.getPasswordHash());
    }

    public Password getPasswordByUserId(long userId) {
        userService.getUserById(userId);
        Optional<Password> password = passwordRepository.findByUserId(userId);
        if (password.isEmpty()) throw new PasswordNotFoundException(userId);
        return password.get();
    }

    private Password createPassword(User user, String password) {
        Password newPassword = new Password();
        newPassword.setPasswordHash(hash(password));
        newPassword.setPasswordStrengthLevel(getStrengthNumberLevel(password));
        newPassword.setPasswordStrengthLevelType(getStrengthLevel(password));
        newPassword.setUser(user);
        return newPassword;
    }
}
