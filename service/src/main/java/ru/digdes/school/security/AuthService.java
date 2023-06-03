package ru.digdes.school.security;

import org.springframework.http.ResponseCookie;
import ru.digdes.school.dto.security.LoginRequest;
import ru.digdes.school.dto.security.MessageResponse;
import ru.digdes.school.dto.security.SignupRequest;

public interface AuthService {
    JwtCookiePlusUserInfo authenticate(LoginRequest loginRequest);
    MessageResponse register(SignupRequest signUpRequest);
    ResponseCookie getCleanCookie();
}
