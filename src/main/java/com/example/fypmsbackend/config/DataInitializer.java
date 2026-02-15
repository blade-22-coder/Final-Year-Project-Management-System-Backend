package com.example.fypmsbackend.config;

import com.example.fypmsbackend.model.Role;
import com.example.fypmsbackend.user.User;
import com.example.fypmsbackend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@uni.ac.ug";

            //check if admin already exists
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setFullName("Blade");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin@123")); //default password
                admin.setRole(Role.ADMIN); //super-ADMIN
                admin.setOnboarded(true);

                userRepository.save(admin);
                System.out.println("Admin has been created: "  + adminEmail);

            }
        };
    }
}
