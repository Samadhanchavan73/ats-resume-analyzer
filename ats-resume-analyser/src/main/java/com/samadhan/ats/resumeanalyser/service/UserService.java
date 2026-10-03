package com.samadhan.ats.resumeanalyser.service;

import com.samadhan.ats.resumeanalyser.model.User;
import com.samadhan.ats.resumeanalyser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates and saves a new user.
     */
    public User registerNewUser(String username, String password, String email) throws Exception {

        // ✅ Check username exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists: " + username);
        }

        // ✅ Check email exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already exists: " + email);
        }

        // ✅ Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // ✅ Create user
        User newUser = new User(username, hashedPassword, email);

        return userRepository.save(newUser);
    }

    // ✅ Find by email (IMPORTANT for AnalysisService)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ Find by username (optional use)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}