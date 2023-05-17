package ru.digdes.school.dao.facade;

public class Criteria<T> implements Conditional {
    private T value;

    public Criteria(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
