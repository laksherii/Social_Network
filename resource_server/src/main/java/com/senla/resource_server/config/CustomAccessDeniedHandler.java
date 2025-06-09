package com.senla.resource_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.resource_server.service.dto.ErrorDto;
import com.senla.resource_server.service.dto.ErrorDto.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException accessDeniedException) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);

        res.getWriter().write(mapper.writeValueAsString(new ErrorDto(ErrorStatus.CLIENT_ERROR, "Forbidden")));
    }
}
