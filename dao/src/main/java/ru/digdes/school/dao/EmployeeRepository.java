package ru.digdes.school.dao;

public interface EmployeeRepository<T, ID> {
    T getOne(ID id);
}
