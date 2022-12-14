package ru.yandex.practicum.kanban.exceptions;

public class TaskAddException extends TaskException {
    public TaskAddException(String message, Object... args) {
        super(message, args);
    }

    @Override
    public String getDetailMessage() {
        return String.format("Ошибка добавления: %s", message);
    }
}
