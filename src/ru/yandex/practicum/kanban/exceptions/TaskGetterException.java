package ru.yandex.practicum.kanban.exceptions;

import java.util.function.Supplier;

public class TaskGetterException extends TaskException{
    public TaskGetterException(String message, Object... args) {
        super(message, args);
    }

    @Override
    public String getDetailMessage() {
        return String.format("Ошибка получения: %s", message);
    }


}
