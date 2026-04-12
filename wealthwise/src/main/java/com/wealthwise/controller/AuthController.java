package com.wealthwise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wealthwise.model.User;
import com.wealthwise.model.Notification;
import com.wealthwise.repository.UserRepository;
import com.wealthwise.service.NotificationService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();


    // SIGNUP API
    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        User existingUser =
                userRepository.findByEmail(user.getEmail());

        if(existingUser != null){
            return "User already exists";
        }

        // encrypt password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // save user
        User savedUser = userRepository.save(user);


        // 🔔 CREATE WELCOME NOTIFICATION
        notificationService.createNotification(
                new Notification(
                        savedUser.getUserId(),
                        "Welcome to WealthWise! Your account has been created successfully.",
                        "SYSTEM_ALERT",
                        "LOW"
                )
        );


        return "User registered successfully";
    }


    // SIGNIN API
    @PostMapping("/signin")
    public String signin(@RequestBody User user) {

        User existingUser =
                userRepository.findByEmail(user.getEmail());

        if(existingUser != null &&
                passwordEncoder.matches(
                        user.getPassword(),
                        existingUser.getPassword()
                )) {

            return "Login successful";
        }

        return "Invalid email or password";
    }
}