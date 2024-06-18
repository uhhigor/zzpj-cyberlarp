package com.example.cyberlarpapi.e2e.secutity;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class CustomSecurityPostProcessor {
    public static RequestPostProcessor applySecurity() {
        OidcLoginRequestPostProcessor oidcLogin = SecurityMockMvcRequestPostProcessors.oidcLogin()
                .idToken(token -> token.claim("email", "user1@example.com"));
        return oidcLogin;
    }
}
