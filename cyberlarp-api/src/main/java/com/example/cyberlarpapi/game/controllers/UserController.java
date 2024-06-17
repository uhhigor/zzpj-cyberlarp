package com.example.cyberlarpapi.game.controllers;

import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Tag(name = "User Operations", description = "Operations related to users")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user", description = "Get current user info or create a new user if not exists")
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        Optional<_User> existingUser;
        String email = oidcUser.getEmail();
        try {
            existingUser = userService.getUserByEmail(email);
            if (existingUser.isEmpty()) {
                _User user = new _User();
                user.setEmail(email);
                existingUser = Optional.ofNullable(userService.save(user));
            }
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new UserResponse(existingUser.get()));
    }

    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Integer userId) {
        try {
            _User user = userService.getUserById(userId);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (UserServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UserResponse {
        private Integer id;
        private String username;
        private String email;

        UserResponse(_User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }
}
