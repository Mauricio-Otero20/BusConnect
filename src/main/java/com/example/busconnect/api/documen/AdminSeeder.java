package com.example.busconnect.api.documen;

import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.UserRole;
import com.example.busconnect.domine.entities.enums.UserStatus;
import com.example.busconnect.domine.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

//@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@busconnect.com").isEmpty()) {
            User admin = User.builder()
                    .username("andres Carlos")
                    .email("andres24@gmail.com")
                    .phone("3001234567")
                    .dateOfBirth(LocalDate.of(2002,03,27))
                    .passwordHash(passwordEncoder.encode("admin1234"))
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(" Admin user created: "+ admin.getEmail()+" / ADMIN");
        }
    }
}
