package ru.digdes.school.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;
import ru.digdes.school.dto.security.UserInfoResponse;

@AllArgsConstructor
@Getter
public class JwtCookiePlusUserInfo {
    private final ResponseCookie cookie;
    private final UserInfoResponse userInfo;
}
