package ru.digdes.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    @Operation(summary = "Вход в систему",
            description = "Токен доступа возвращается в виде cookie. " +
                    "Имя пользователя и пароль администратора по умолчанию: root")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtCookiePlusUserInfo jwtCookiePlusUserInfo = authService.authenticate(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookiePlusUserInfo.getCookie().toString())
                .body(jwtCookiePlusUserInfo.getUserInfo());
    }

    @PostMapping("/signup")
    @Operation(summary = "Регистрация учетной записи",
            description = "На данный момент регистрация реализована следующим образом -  " +
                    "сотрудник может зарегистрировать учетную запись, если администратор добавил его карточку " +
                    "в базу и заполнил поле email. Это поле проверяется на соответствие вводимому при регистрации. " +
                    "Сотруднику будет отказано в регистрации УЗ, если электронная почта, которую он введет " +
                    "при регистрации, не привязана ни к одной учетной записи. УЗ создается с привелегией 'ROLE_USER'. " +
                    "При необходимости роль сотрудника может быть изменена администратором. " +
                    "В дальнейшем необходимо добавить еще и подтверждение в виде отправки ссылки на указанный email."
    )
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }

    @PostMapping("/signout")
    @Operation(summary = "Выход из системы",
            description = "Очищает cookie с токеном доступа.")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = authService.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out."));
    }
}
