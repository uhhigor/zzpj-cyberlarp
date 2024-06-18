package com.example.cyberlarpapi.e2e.secutity;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class CustomSecurityPostProcessor {
    public static RequestPostProcessor applySecurityForUser1() {
        OidcLoginRequestPostProcessor oidcLogin = SecurityMockMvcRequestPostProcessors.oidcLogin()
                .idToken(token -> token.claim("email", "user1@example.com"));
        return oidcLogin;
    }

    public static RequestPostProcessor applySecurityForUser2() {
        return SecurityMockMvcRequestPostProcessors.oidcLogin()
                .idToken(token -> token.claim("email", "user2@example.com"));
    }

    public static RequestPostProcessor applySecurity(String username) {
        return SecurityMockMvcRequestPostProcessors.oidcLogin()
                .idToken(token -> token.claim("email", username + "@example.com"));
    }
}
