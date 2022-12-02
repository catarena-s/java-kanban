package ru.yandex.practicum.kanban.model;

public enum TaskType {
    TASK("'Задача'"),
    EPIC("'Эпик'"),
    SUB_TASK("'Подзадача'");

    private final String value;

    public String getValue() {
        return value;
    }

    TaskType(String value) {
        this.value=value;
    }
}
