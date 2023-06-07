package ru.digdes.school.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.digdes.school.model.employee.RoleInSystem;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private RoleInSystem role;
}
