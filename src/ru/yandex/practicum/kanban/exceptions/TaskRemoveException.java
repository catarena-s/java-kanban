package ru.yandex.practicum.kanban.exceptions;

public class TaskRemoveException extends TaskException {
    public TaskRemoveException(String message, Object... args) {
        super(message, args);
    }

    @Override
    public String getDetailMessage() {
        return String.format("Ошибка удаления: %s", message);
    }
}
