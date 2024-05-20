package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.cyberlarpapi.User;
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequest request) {
            User user = new User();
            user.setUsername(request.username);
            return ResponseEntity.ok(userService.save(user));
    }


    // only for testing purposes
    public static class UserRequest {
        private String username;
    }
}
