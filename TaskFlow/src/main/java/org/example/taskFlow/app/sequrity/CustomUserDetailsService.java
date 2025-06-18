package org.example.taskFlow.app.sequrity;

import org.example.taskFlow.exception.user.PasswordNotFoundException;
import org.example.taskFlow.model.Password;
import org.example.taskFlow.model.User;
import org.example.taskFlow.repository.PasswordRepository;
import org.example.taskFlow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден по email: " + email));

        Password password = passwordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new PasswordNotFoundException(user.getId()));

        return new CustomUserDetails(user, password.getPasswordHash());
    }
}
