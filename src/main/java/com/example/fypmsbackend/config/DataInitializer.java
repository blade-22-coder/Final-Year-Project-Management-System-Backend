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
    CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {

            //check if admin already exists
            if (userRepo.findByEmail("admin@uni.ac.ug").isEmpty()) {
                User admin = new User();
                admin.setFullName("Blade");
                admin.setEmail("admin@uni.ac.ug");
                admin.setPassword(passwordEncoder.encode("admin@123")); //default password
                admin.setRole(Role.ADMIN); //super-ADMIN
                admin.setOnboarded(true);

                userRepo.save(admin);
            }

            if(userRepo.findByEmail("supervisor@uni.ac.ug").isEmpty()) {
                User supervisor = new User();
                supervisor.setFullName("Supervisor");
                supervisor.setEmail("supervisor@uni.ac.ug");
                supervisor.setPassword(passwordEncoder.encode("supervisor@123"));
                supervisor.setRole(Role.SUPERVISOR);
                supervisor.setOnboarded(true);

                userRepo.save(supervisor);
            }

            if(userRepo.findByEmail("student@uni.ac.ug").isEmpty()) {
                User student = new User();
                student.setFullName("Student");
                student.setEmail("student@uni.ac.ug");
                student.setPassword(passwordEncoder.encode("student@123"));
                student.setRole(Role.STUDENT);
                student.setOnboarded(true);

                userRepo.save(student);
            }
        };
    }
}
