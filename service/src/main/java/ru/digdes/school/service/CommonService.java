package ru.digdes.school.service;

import java.util.List;

public interface CommonService<T> {
    void create(T t);
    T getOne(Long id);
    List<T> getAll();
    T update(T t);
    void delete(Long id);
}
