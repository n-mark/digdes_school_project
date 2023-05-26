package ru.digdes.school.mapping.impl;

import org.springframework.stereotype.Component;
import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.JobTitle;
import ru.digdes.school.model.employee.Position;

@Component
public class EmployeeMapperImpl implements Mapper<Employee, EmployeeDto> {
    @Override
    public EmployeeDto modelToDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .lastName(employee.getLastName())
                .name(employee.getName())
                .middleName(employee.getMiddleName())
                .position(employee.getPosition() == null ? null : employee.getPosition().getPositionAsString())
                .jobTitle(employee.getJobTitle() == null ? null : employee.getJobTitle().getJobTitleAsString())
                .account(employee.getAccount())
                .email(employee.getEmail())
                .build();
    }

    @Override
    public Employee dtoToModel(EmployeeDto employeeDto) {
        return Employee.builder()
                .id(employeeDto.getId())
                .lastName(employeeDto.getLastName())
                .name(employeeDto.getName())
                .middleName(employeeDto.getMiddleName())
                .position(employeeDto.getPosition() == null ? null : Position.valueOf(employeeDto.getPosition()))
                .jobTitle(employeeDto.getJobTitle() == null ? null : JobTitle.valueOf(employeeDto.getJobTitle()))
                .account(employeeDto.getAccount())
                .email(employeeDto.getEmail())
                .build();
    }

    @Override
    public void updateMerge(Employee employee, EmployeeDto employeeDto) {
        employee.setLastName(employeeDto.getLastName() == null ? employee.getLastName() : employeeDto.getLastName());
        employee.setName(employeeDto.getName() == null ? employee.getName() : employeeDto.getName());
        employee.setMiddleName(employeeDto.getMiddleName() == null ? employee.getMiddleName() : employeeDto.getMiddleName());
        employee.setPosition(employeeDto.getPosition() == null ? employee.getPosition() : Position.valueOf(employeeDto.getPosition()));
        employee.setJobTitle(employeeDto.getJobTitle() == null ? employee.getJobTitle() : JobTitle.valueOf(employeeDto.getJobTitle()));
        employee.setAccount(employeeDto.getAccount() == null ? employee.getAccount() : employeeDto.getAccount());
        employee.setEmail(employeeDto.getEmail() == null ? employee.getEmail() : employeeDto.getEmail());
    }
}
