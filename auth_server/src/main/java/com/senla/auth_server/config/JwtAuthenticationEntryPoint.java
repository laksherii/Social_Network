package com.senla.auth_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.auth_server.service.dto.ErrorDto;
import com.senla.auth_server.service.dto.ErrorDto.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write(mapper.writeValueAsString(new ErrorDto(ErrorStatus.CLIENT_ERROR,"Authentication required")));
    }
}

