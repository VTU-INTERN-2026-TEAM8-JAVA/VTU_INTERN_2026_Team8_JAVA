package com.wealthwise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wealthwise.model.User;
import com.wealthwise.repository.UserRepository;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        User existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser != null){
            return "User already exists";
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/signin")
    public String signin(@RequestBody User user) {

        User existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return "Login successful";
        }

        return "Invalid email or password";
    }
}