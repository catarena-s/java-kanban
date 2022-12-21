package ru.yandex.practicum.kanban.exceptions;

public class TaskGetterException extends TaskException{
    public TaskGetterException(String message, Object... args) {
        super(message, args);
    }

    @Override
    public String getDetailMessage() {
        return String.format("Ошибка получения: %s", message);
    }


}
