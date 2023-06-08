package ru.digdes.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.digdes.school.dto.Stateable;
import ru.digdes.school.dto.employee.*;
import ru.digdes.school.model.employee.RoleInSystem;
import ru.digdes.school.service.impl.EmployeeServiceImpl;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeServiceImpl employeeService;
    @Autowired
    private ObjectMapper objectMapper;
    private EmployeeDto employeeDtoInput;
    private EmployeeDto employeeDtoOutput;

    @BeforeEach
    void setUp() {
        employeeDtoInput = EmployeeDto.builder()
                .lastName("TestLastName")
                .name("TestName")
                .account("testAccount")
                .build();

        employeeDtoOutput = EmployeeDto.builder()
                .id(1L)
                .lastName("TestLastName")
                .name("TestName")
                .account("testAccount")
                .build();
    }

    @Test
    void checkCreateEmployee() throws Exception {
        when(employeeService.create(any(EmployeeDto.class))).thenReturn(employeeDtoOutput);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDtoInput)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employeeDtoOutput.getName()));
    }

    @Test
    void checkSearch() throws Exception {
        EmployeePaging employeePaging = new EmployeePaging();
        EmployeeFilterObject employeeFilterObject = new EmployeeFilterObject();
        employeeFilterObject.setSearchString("");

        Page<EmployeeDto> pageResult = new PageImpl<>(Collections.singletonList(employeeDtoOutput));

        when(employeeService.search(any(EmployeePaging.class), any(EmployeeFilterObject.class)))
                .thenReturn(pageResult);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/employees/search")
                        .queryParam("searchString", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    }

    @Test
    void checkGetById() throws Exception {
        Long employeeId = 1L;
        when(employeeService.getOneById(employeeId)).thenReturn(employeeDtoOutput);

        mockMvc.perform(get("/employees/id/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employeeDtoOutput.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(employeeDtoOutput.getLastName()));
    }

    @Test
    void checkGetByAccount() throws Exception {
        String account = "testAccount";
        when(employeeService.getOneByStringValue(account)).thenReturn(employeeDtoOutput);

        mockMvc.perform(get("/employees/account/testAccount")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value((Long) employeeDtoOutput.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(employeeDtoOutput.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account").value(employeeDtoOutput.getAccount()));
    }

    @Test
    void checkUpdateEmployee() throws Exception {
        Long employeeId = 1L;

        EmployeeDto employeeDtoUpdated = EmployeeDto.builder()
                .id(1L)
                .lastName("TestLastNameUpdated")
                .name("TestNameUpdated")
                .account("testAccountUpdated")
                .build();

        when(employeeService.update(any(EmployeeDto.class))).thenReturn(employeeDtoUpdated);

        mockMvc.perform(patch("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDtoUpdated)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employeeDtoUpdated.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(employeeDtoUpdated.getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employeeDtoUpdated.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account").value(employeeDtoUpdated.getAccount()));
    }

    @Test
    void checkDeleteEmployee() throws Exception {
        String reply = "The employee with id = " + 1 + " status has been set to 'DELETED'";
        when(employeeService.changeState(any(Stateable.class))).thenReturn(reply);

        Long deleteId = 1L;
        DeleteEmployeeDto deleteEmployeeDto = new DeleteEmployeeDto();
        deleteEmployeeDto.setId(deleteId);

        mockMvc.perform(delete("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteEmployeeDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(reply));
    }

    @Test
    void chahgeSystemRole() throws Exception {
        SystemRolePatchDto systemRolePatchDto = new SystemRolePatchDto();
        systemRolePatchDto.setId(1L);
        systemRolePatchDto.setRoleInSystem(RoleInSystem.ROLE_ADMIN);

        when(employeeService.changeSystemRole(any(SystemRolePatchDto.class))).thenReturn(systemRolePatchDto);

        mockMvc.perform(patch("/employees/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(systemRolePatchDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(systemRolePatchDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roleInSystem").value(systemRolePatchDto.getRoleInSystem().toString()));
    }
}