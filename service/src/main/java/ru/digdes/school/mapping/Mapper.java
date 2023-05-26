package ru.digdes.school.mapping;

public interface Mapper<T, U> {
    U modelToDto(T t);
    T dtoToModel(U u);
    void updateMerge(T t, U u);
}
