package com.example.finance_dashboard.service;

import com.example.finance_dashboard.exception.ResourceNotFoundException;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User createUser(User user) {
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User updateUser(Long id, User updatedUser) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setRole(updatedUser.getRole());
		user.setStatus(updatedUser.getStatus());

		return userRepository.save(user);
	}
}