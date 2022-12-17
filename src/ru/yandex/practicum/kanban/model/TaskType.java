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

    public Task create(){
        switch (this){
            case TASK: return new SimpleTask();
            case EPIC: return new Epic();
            case SUB_TASK:return new SubTask();
        }
        return null;
    }
}
