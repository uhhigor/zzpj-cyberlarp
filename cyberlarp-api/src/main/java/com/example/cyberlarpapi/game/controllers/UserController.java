package com.example.cyberlarpapi.game.controllers;

import java.util.Optional;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterNotFoundException;
import com.example.cyberlarpapi.game.exceptions.CharacterException.CharacterServiceException;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import com.example.cyberlarpapi.game.services.CharacterService;
import com.example.cyberlarpapi.game.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.cyberlarpapi.game.model.user.User;
@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final CharacterService characterService;

    public UserController(UserService userService, CharacterService characterService) {
        this.userService = userService;
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserRequest request) {
            User user = new User();
            user.setUsername(request.username);
            return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String email = oidcUser.getEmail();
        try {
            Optional<User> existingUser = userService.getUserByEmail(email);
            if (existingUser.isEmpty()) {
                User user = new User();
                user.setEmail(email);
                userService.save(user);
            }
        } catch (UserServiceException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.ok("User email saved: " + email);
    }

    @GetMapping("/characters")
    public ResponseEntity<?> getUserCharacters(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String email = oidcUser.getEmail();
        try {
            Optional<User> existingUser = userService.getUserByEmail(email);
            if (existingUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            User user = existingUser.get();
            return ResponseEntity.ok(characterService.getCharactersByUserId(user.getId()));
        } catch (UserServiceException | CharacterServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/characters/{id}")
    public ResponseEntity<?> selectCharacter(@AuthenticationPrincipal OidcUser oidcUser, @PathVariable Integer id) {
        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        try {
            return ResponseEntity.ok(characterService.getById(id));
        } catch (CharacterNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    // only for testing purposes
    public static class UserRequest {
        private String username;
    }
}
