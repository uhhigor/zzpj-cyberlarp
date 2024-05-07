package com.example.cyberlarpapi.Auth0.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.UserRepository;
import com.example.cyberlarpapi.UserRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        String email = userRequest.getEmail();


        Optional<User> existingUserOptional = userRepository.findByEmail(email);
        if (existingUserOptional.isPresent()) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Użytkownik o podanym adresie e-mail już istnieje.");
        }

        User user = new User();
        user.setEmail(email);
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }
}