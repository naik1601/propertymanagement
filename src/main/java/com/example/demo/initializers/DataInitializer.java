package com.example.demo.initializers;


import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {

            User u1=new User    (
                    null,
                    "Alice",
                    "Agent",
                    "alice@example.com",
                    passwordEncoder.encode("al.123"),
                    UserRole.BUYER,
                    LocalDateTime.now(),
                    "Alice");

            User u9 = new User(null,
                    "Grace",
                    "Mitchell",
                    "grace.mitchell@email.com",
                    passwordEncoder.encode("gm.123"),
                    UserRole.BUYER,
                    LocalDateTime.now(),
                    "Grace");

            userRepository.saveAll(List.of(u1));
            userRepository.saveAll(List.of(u9));

            System.out.println("🟢 Initial users and roles inserted.");
        } else {
            System.out.println("🟡 Users and roles already exist, skipping initialization.");
        }
    }
}
