package com.example.demo.services;
import com.example.demo.dtos.JwtResponse;
import com.example.demo.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    JwtResponse authenticateAndGenerateToken(User user);
    public Cookie loginAndCreateJwtCookie(User user) throws BadCredentialsException;
    void clearJwtCookie(HttpServletResponse response);

}

