
package com.pharmatrack.pharmatrack.service;

import com.pharmatrack.pharmatrack.entity.AppUser;
import com.pharmatrack.pharmatrack.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(
                        password, user.getPassword()))
                .orElse(false);
    }

    public AppUser createUser(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must have at least 8 characters");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));

        // Every public registration creates a STAFF account.
        return userRepository.save(user);
    }

    public AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}