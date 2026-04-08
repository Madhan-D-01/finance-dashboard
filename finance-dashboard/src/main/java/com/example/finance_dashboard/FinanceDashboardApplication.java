package com.example.finance_dashboard;

import org.springframework.boot.SpringApplication;
import com.example.finance_dashboard.model.Role;
import com.example.finance_dashboard.model.Status;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDashboardApplication.class, args);
		System.out.println("Successfully Started......");
	}
	@Bean
	CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    return args -> {
	        
	        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
	            User admin = new User();
	            admin.setName("Admin User");
	            admin.setEmail("admin@example.com");
	            admin.setPassword(passwordEncoder.encode("admin123")); 
	            admin.setRole(Role.ADMIN);
	            admin.setStatus(Status.ACTIVE);
	            userRepository.save(admin);
	            System.out.println("Admin user created: admin@example.com / admin123");
	        }
	    };
	}

}
