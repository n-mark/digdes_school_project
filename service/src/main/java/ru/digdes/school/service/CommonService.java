package ru.digdes.school.service;

import java.util.List;

public interface CommonService<T, R> {
    R getOne(Long id);
    List<R> getAll();

}
