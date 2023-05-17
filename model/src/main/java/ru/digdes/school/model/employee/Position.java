package ru.digdes.school.model.employee;

public enum Position {
    INTERN("Стажер"),
    JUNIOR("Младший"),
    MIDDLE("Средний"),
    SENIOR("Старший"),
    CHIEF("Главный");

    private final String position;
    Position(String pos) {
        this.position = pos;
    }

    public String getPositionAsString() {
        return position;
    }
}
