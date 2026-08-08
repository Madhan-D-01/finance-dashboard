package com.example.finance_dashboard.controller;

import com.example.finance_dashboard.dto.LoginRequest;
import com.example.finance_dashboard.dto.LoginResponse;
import com.example.finance_dashboard.model.Status;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.UserRepository;
import com.example.finance_dashboard.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (user.getStatus() != Status.ACTIVE) {
			throw new BadCredentialsException("Account is inactive");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

		return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole().name()));
	}
}