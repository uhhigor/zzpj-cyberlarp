package com.example.cyberlarpapi.game.services;

import java.util.Optional;
import com.example.cyberlarpapi.game.model.user._User;
import com.example.cyberlarpapi.game.repositories.UserRepository;
import com.example.cyberlarpapi.game.exceptions.UserException.UserServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public _User save(_User user) {
        return userRepository.save(user);
    }

    public _User getUserById(int id) throws UserServiceException {
        return userRepository.findById(id).orElseThrow(() -> new UserServiceException("User not found"));
    }

    public Optional<_User> getUserByEmail(String email) throws UserServiceException {
        return userRepository.findByEmail(email);
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OidcUser oidcUser = (OidcUser) oauth2Token.getPrincipal();
            String email = oidcUser.getEmail();
            if (email != null) {
                Optional<_User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isEmpty()) {
                    _User user = new _User();
                    user.setEmail(email);
                    userRepository.save(user);
                }
            }
            return email != null ? email : "Email not found in token";
        }
        return "No authenticated user found";
    }

    public _User getCurrentUser() throws UserServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OidcUser oidcUser = (OidcUser) oauth2Token.getPrincipal();
            String email = oidcUser.getEmail();
            if (email != null) {
                Optional<_User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isEmpty()) {
                    _User user = new _User();
                    user.setEmail(email);
                    user.setUsername(email.split("@")[0]);
                    userRepository.save(user);
                }
            }
            return userRepository.findByEmail(email).orElse(null);
        }
        throw new UserServiceException("No authenticated user found");
    }
}
