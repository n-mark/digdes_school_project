package ru.digdes.school.dao.jdbcimpl;

import ru.digdes.school.dao.facade.RepositoryFacade;
import ru.digdes.school.dto.employeetask.EmployeeTaskDto;
import ru.digdes.school.model.employee.Employee;
import ru.digdes.school.model.employee.EmployeeStatus;
import ru.digdes.school.model.employee.JobTitle;
import ru.digdes.school.model.employee.Position;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepoFacadeJdbcImpl implements RepositoryFacade<Employee> {

    public EmployeeRepoFacadeJdbcImpl() {

    }

    @Override
    public Employee create(Employee object) {
        String query = "INSERT INTO employee " +
                "(last_name, name, middle_name, position, job_title, account, email, status)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setEmployee(preparedStatement, object);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long generatedId = resultSet.getLong(1);
                resultSet.close();
                return getById((generatedId));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee getById(Long id) {
        String query = "SELECT * FROM employee WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return getEmployee(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Employee> getAll() {
        String query = "SELECT * FROM employee";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            List<Employee> employeeList = new ArrayList<>();
            while (resultSet.next()) {
                Employee employee = getEmployee(resultSet);
                employeeList.add(employee);
            }
            return employeeList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee update(Employee object) {
        String query = "UPDATE employee " +
                "set last_name = ?, name = ?, middle_name = ?, " +
                "position = ?, job_title = ?, account = ?, email = ?, status = ? " +
                "where id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            Employee current = getById(object.getId());
            Employee merged = mergeEmployees(current, object);
            setEmployee(preparedStatement, merged);
            preparedStatement.setLong(9, object.getId());
            preparedStatement.executeUpdate();


            return getById(object.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM employee WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmployeeTaskDto> search(String request) {
        String query = "SELECT emp.last_name, emp.name, t.task_name, t.description\n" +
                "FROM employee emp\n" +
                "LEFT JOIN task t on emp.id = t.responsible_id\n" +
                "WHERE emp.last_name LIKE ? OR emp.name LIKE ? OR t.task_name LIKE ? OR t.description LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String toSearch = "%" + request + "%";
            preparedStatement.setString(1, toSearch);
            preparedStatement.setString(2, toSearch);
            preparedStatement.setString(3, toSearch);
            preparedStatement.setString(4, toSearch);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<EmployeeTaskDto> employeeTaskDtos = new ArrayList<>();
            while (resultSet.next()) {
                EmployeeTaskDto employeeTaskDto = getEmplTaskDto(resultSet);
                employeeTaskDtos.add(employeeTaskDto);
            }
            return employeeTaskDtos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    _________________________________________________________________________

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/digdes-school-proj",
                    "user", "password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Employee getEmployee(ResultSet resultSet) {
        try {
            return Employee.builder()
                    .id(resultSet.getLong(1))
                    .lastName(resultSet.getString(2))
                    .name(resultSet.getString(3))
                    .middleName(resultSet.getString(4))
                    .position(resultSet.getString(5) == null ? null : Position.valueOf(resultSet.getString(5)))
                    .jobTitle(resultSet.getString(6) == null ? null : JobTitle.valueOf(resultSet.getString(6)))
                    .account(resultSet.getString(7))
                    .email(resultSet.getString(8))
                    .status(resultSet.getString(9) == null ? null : EmployeeStatus.valueOf(resultSet.getString(9)))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setEmployee(PreparedStatement statement, Employee object) {
        try {
            statement.setString(1, object.getLastName());
            statement.setString(2, object.getName());
            statement.setString(3, object.getMiddleName());
            statement.setString(4, object.getPosition() == null ? null : object.getPosition().name());
            statement.setString(5, object.getJobTitle() == null ? null : object.getJobTitle().name());
            statement.setString(6, object.getAccount());
            statement.setString(7, object.getEmail());
            statement.setString(8, EmployeeStatus.ACTIVE.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Employee mergeEmployees(Employee current, Employee object) {
        Employee employee = Employee.builder()
                .lastName(object.getLastName() == null ? current.getLastName() : object.getLastName())
                .name(object.getName() == null ? current.getName() : object.getName())
                .middleName(object.getMiddleName() == null ? current.getMiddleName() : object.getMiddleName())
                .position(object.getPosition() == null ? current.getPosition() : object.getPosition())
                .jobTitle(object.getJobTitle() == null ? current.getJobTitle() : object.getJobTitle())
                .account(object.getAccount() == null ? current.getAccount() : object.getAccount())
                .email(object.getEmail() == null ? current.getEmail() : object.getEmail())
                .status(object.getStatus() == null ? current.getStatus() : object.getStatus())
                .build();

        return employee;
    }

    private EmployeeTaskDto getEmplTaskDto(ResultSet resultSet) {
        try {
            return EmployeeTaskDto.builder()
                    .lastName(resultSet.getString(1))
                    .name(resultSet.getString(2))
                    .taskName(resultSet.getString(3))
                    .description(resultSet.getString(4))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}