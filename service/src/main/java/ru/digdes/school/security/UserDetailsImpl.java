package ru.digdes.school.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String account;

    private String email;

    @JsonIgnore
    private String password;
    private EmployeeStatus employeeStatus;

    private Collection<? extends GrantedAuthority> authorities;


    public UserDetailsImpl(Long id, String account, String email, String password,
                           EmployeeStatus employeeStatus,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.account = account;
        this.email = email;
        this.password = password;
        this.employeeStatus = employeeStatus;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Employee user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRoleInSystem().name()));

        return new UserDetailsImpl(
                user.getId(),
                user.getAccount(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
