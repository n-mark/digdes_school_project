package ru.digdes.school.service;

import java.util.List;

public interface CommonService<T, U> {
    U create(T t);
    T update(T t);
    List<T> search();

    List<T> getAll();
    void delete(Long id);
}
