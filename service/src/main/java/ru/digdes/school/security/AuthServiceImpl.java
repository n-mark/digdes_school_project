package ru.digdes.school.security;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dto.security.LoginRequest;
import ru.digdes.school.dto.security.MessageResponse;
import ru.digdes.school.dto.security.SignupRequest;
import ru.digdes.school.dto.security.UserInfoResponse;
import ru.digdes.school.exception.EmployeeDeletedException;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.employee.RoleInSystem;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(EmployeeRepository employeeRepository,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           PasswordEncoder encoder) {
        this.employeeRepository = employeeRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    public JwtCookiePlusUserInfo authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getEmployeeStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeDeletedException("Unable to proceed: the account status is 'DELETED'");
        }

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(RoleInSystem.valueOf(roles.get(0)))
                .build();

        return new JwtCookiePlusUserInfo(jwtCookie, userInfoResponse);
    }

    public MessageResponse register(SignupRequest signUpRequest) {
        if (!employeeRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Your email isn't linked to any employee. " +
                    "You cannot register in the system.");
        }

        if (employeeRepository.existsByAccount(signUpRequest.getUsername())) {
            throw new IllegalArgumentException("Account name is already taken");
        }

        Employee employee = employeeRepository.getEmployeeByEmail(signUpRequest.getEmail());

        if (employee.getEmail().equals(signUpRequest.getEmail()) && employee.getAccount() != null
                && employee.getPassword() != null) {
            throw new IllegalArgumentException("The account related to that email is already registered");
        }

        employee.setAccount(signUpRequest.getUsername());
        employee.setPassword(encoder.encode(signUpRequest.getPassword()));
        employee.setRoleInSystem(RoleInSystem.ROLE_USER);

        employeeRepository.save(employee);

        return new MessageResponse("You can now proceed to login");
    }

    public ResponseCookie getCleanCookie() {
        return jwtUtils.getCleanJwtCookie();
    }
}
