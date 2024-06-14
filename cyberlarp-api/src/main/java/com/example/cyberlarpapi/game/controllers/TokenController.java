package com.example.cyberlarpapi.game.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TokenController {

    @GetMapping("/token")
    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OidcUser oidcUser = (OidcUser) oauth2Token.getPrincipal();
            return oidcUser.getIdToken().getTokenValue();
        }
        return "No token found";
    }

    @GetMapping("/email")
    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OidcUser oidcUser = (OidcUser) oauth2Token.getPrincipal();
            String email = oidcUser.getEmail();
            return email != null ? email : "Email not found in token";
        }
        return "No authenticated user found";
    }
}
