package com.example.cyberlarpapi.game.controllers;

import java.util.List;
import java.util.Optional;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.model.character.Character;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.user._User;
@Tag(name = "User Operations", description = "Operations related to users")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final CharacterService characterService;

    public UserController(UserService userService, CharacterService characterService) {
        this.userService = userService;
        this.characterService = characterService;
    }

    @Operation(summary = "Create a new user", description = "Create a new user in the system, providing username and email")
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequest request) {
            _User user = new _User();
            user.setUsername(request.username);
            user.setEmail(request.email);
            return ResponseEntity.ok(userService.save(user));
    }

    @Operation(summary = "Get user info", description = "Get user info")
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
                userService.save(user);
            }
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.ok(existingUser);
    }

    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        try {
            _User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get user characters", description = "Get user characters by user id")
    @GetMapping("/characters/{userId}")
    public ResponseEntity<?> getUserCharacters(@PathVariable Integer userId) {
        try {
            _User user = userService.getUserById(userId);
            return ResponseEntity.ok(new UserResponse(user.getCharacters().size(), user.getCharacters()));
        } catch (UserServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UserRequest {
        private String username;

        private String email;
    }

    @Getter
    @NoArgsConstructor

    public static class UserResponse {
        private int size;
        private List<Character> characters;

        public UserResponse(int size, List<Character> characters) {
            this.size = size;
            this.characters = characters;
        }
    }
}
