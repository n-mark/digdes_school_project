package ru.digdes.school.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.digdes.school.dao.repository.EmployeeRepository;
import ru.digdes.school.dao.repository.specifications.SpecificationFiltering;
import ru.digdes.school.dto.employee.*;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.mapping.impl.EmployeeMapperImpl;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.employee.RoleInSystem;

import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Spy
    private Mapper<Employee, EmployeeDto> employeeMapper = new EmployeeMapperImpl();
    @Mock
    private SpecificationFiltering<Employee> specificationFiltering;
    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;
    private Employee employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .lastName("TestLastName")
                .name("TestName")
                .status(EmployeeStatus.ACTIVE)
                .account("TestAccount")
                .build();

        employeeDto = EmployeeDto.builder()
                .id(1L)
                .lastName("TestLastName")
                .name("TestName")
                .account("TestAccount")
                .build();
    }

    @Test
    void checkCreate() {
        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        EmployeeDto returnedEmplDto = employeeServiceImpl.create(employeeDto);
        Assertions.assertThat(returnedEmplDto).isEqualTo(employeeDto);
    }

    @Test
    void checkUpdate() {
        Long updateId = 1L;
        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .lastName("TestUpdatedLastName")
                .name("TestName")
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeDto updatedEmployeeDtoInput = EmployeeDto.builder()
                .id(1L)
                .lastName("TestUpdatedLastName")
                .name("TestName")
                .build();

        when(employeeRepository.existsById(updateId)).thenReturn(true);
        when(employeeRepository.getReferenceById(updateId)).thenReturn(employee);
        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeDto updatedEmployeeDtoOutput = employeeServiceImpl.update(updatedEmployeeDtoInput);

        Assertions.assertThat(updatedEmployeeDtoOutput).isEqualTo(updatedEmployeeDtoInput);
    }

    @Test
    void checkSearch() {
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee));
        EmployeePaging employeePaging = new EmployeePaging();
        EmployeeFilterObject employeeFilterObject = new EmployeeFilterObject();
        employeeFilterObject.setSearchString("testString");

        when(employeeRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDto> employeeDtos = employeeServiceImpl.search(employeePaging, employeeFilterObject);

        Assertions.assertThat(employeeDtos.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(employeeDtos.getContent().get(0)).isEqualTo(employeeDto);

    }

    @Test
    void checkChangeState() {
        Long deleteId = 1L;
        DeleteEmployeeDto deleteEmployeeDto = new DeleteEmployeeDto();
        deleteEmployeeDto.setId(deleteId);
        when(employeeRepository.existsById(deleteId)).thenReturn(true);
        when(employeeRepository.getReferenceById(deleteId)).thenReturn(employee);

        Employee setToDeleted = Employee.builder()
                .lastName(employee.getLastName())
                .name(employee.getName())
                .build();
        setToDeleted.setStatus(EmployeeStatus.DELETED);

        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(setToDeleted);


        Assertions.assertThat(employeeServiceImpl.changeState(deleteEmployeeDto))
                .isEqualTo("The employee with id = " + deleteEmployeeDto.getId() + " status has been set to 'DELETED'");

    }

    @Test
    void checkGetOneById() {
        Long getId = 1L;
        when(employeeRepository.existsById(getId)).thenReturn(true);
        when(employeeRepository.getReferenceById(getId)).thenReturn(employee);

        Assertions.assertThat(employeeServiceImpl.getOneById(getId)).isEqualTo(employeeDto);
    }

    @Test
    void checkGetOneByStringValue() {
        String account = "TestAccount";
        when(employeeRepository.existsByAccount(account)).thenReturn(true);
        when(employeeRepository.getEmployeeByAccount(account)).thenReturn(employee);

        Assertions.assertThat(employeeServiceImpl.getOneByStringValue(account)).isEqualTo(employeeDto);
    }

    @Test
    void changeSystemRole() {
        SystemRolePatchDto systemRolePatchDto = new SystemRolePatchDto();
        systemRolePatchDto.setId(1L);
        systemRolePatchDto.setRoleInSystem(RoleInSystem.ROLE_ADMIN);

        Employee changedRole = Employee.builder()
                .id(employee.getId())
                .lastName(employee.getLastName())
                .name(employee.getName())
                .build();
        changedRole.setRoleInSystem(systemRolePatchDto.getRoleInSystem());

        when(employeeRepository.existsById(systemRolePatchDto.getId())).thenReturn(true);
        when(employeeRepository.getReferenceById(systemRolePatchDto.getId())).thenReturn(employee);
        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(changedRole);

        Assertions.assertThat(employeeServiceImpl.changeSystemRole(systemRolePatchDto)).isEqualTo(systemRolePatchDto);
    }
}