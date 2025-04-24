package com.senla.auth_server.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!"custom_login".equals(request.getParameter("grant_type"))) {
            return null;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        return new ResourceOwnerAuthenticationToken(username, password);
    }

}
