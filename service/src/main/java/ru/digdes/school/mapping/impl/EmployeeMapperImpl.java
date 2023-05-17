package ru.digdes.school.mapping.impl;

import ru.digdes.school.dto.employee.EmployeeDto;
import ru.digdes.school.mapping.Mapper;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.JobTitle;
import ru.digdes.school.model.employee.Position;

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


}
