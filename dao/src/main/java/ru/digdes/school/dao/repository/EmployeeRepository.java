package ru.digdes.school.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.digdes.school.model.employee.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByAccount(String account);

    Employee getEmployeeByAccount(String account);

    @Query("SELECT e FROM Employee e " +
            "WHERE (LOWER(e.lastName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR " +
            "       LOWER(e.name) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR " +
            "       LOWER(e.middleName) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR " +
            "       LOWER(e.account) LIKE CONCAT('%', LOWER(:searchTerm), '%') OR " +
            "       LOWER(e.email) LIKE CONCAT('%', LOWER(:searchTerm), '%')) " +
            "AND e.status = 'ACTIVE'")
    List<Employee> foundByString(String searchTerm);
}
