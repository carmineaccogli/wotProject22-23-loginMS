package it.safesiteguard.ms.loginms_ssguard.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setHeader("WWW-Authenticate", "Bearer, error=\"unauthorized\", error_description=\"" + authException.getMessage() + "\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
