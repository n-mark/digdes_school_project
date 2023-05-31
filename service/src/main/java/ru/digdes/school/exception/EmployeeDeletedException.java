package ru.digdes.school.exception;

public class EmployeeDeletedException extends RuntimeException {
    public EmployeeDeletedException() {
        super();
    }

    public EmployeeDeletedException(String message) {
        super(message);
    }
}
