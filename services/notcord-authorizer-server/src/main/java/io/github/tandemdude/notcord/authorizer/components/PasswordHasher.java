package io.github.tandemdude.notcord.authorizer.components;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean comparePasswords(String password, String storedHash) {
        return passwordEncoder.matches(password, storedHash);
    }
}
