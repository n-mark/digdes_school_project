package ru.digdes.school.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.digdes.school.dto.security.LoginRequest;
import ru.digdes.school.dto.security.MessageResponse;
import ru.digdes.school.dto.security.SignupRequest;
import ru.digdes.school.security.AuthService;
import ru.digdes.school.security.AuthServiceImpl;
import ru.digdes.school.security.JwtCookiePlusUserInfo;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtCookiePlusUserInfo jwtCookiePlusUserInfo = authService.authenticate(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookiePlusUserInfo.getCookie().toString())
                .body(jwtCookiePlusUserInfo.getUserInfo());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = authService.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out."));
    }
}
