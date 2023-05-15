package ru.digdes.school.dao.facade;

import java.util.List;

public interface RepositoryFacade<T> {
    T create(T object);
    T getById(Long id);
    List<T> getAll();
    T update(T object);
    void deleteById(Long id);
    //TODO:
    //List<T> getByCriteria(List<? extends Conditional> conditions);
}
