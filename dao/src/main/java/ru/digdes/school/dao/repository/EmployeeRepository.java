package ru.digdes.school.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.digdes.school.model.employee.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByAccount(String account);
    boolean existsByEmail(String email);
    boolean existsByAccount(String account);
    Employee getEmployeeByAccount(String account);

    Employee getEmployeeByEmail(String email);

}
