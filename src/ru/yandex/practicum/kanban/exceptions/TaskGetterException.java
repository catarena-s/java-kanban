package ru.yandex.practicum.kanban.exceptions;

import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public class TaskGetterException extends TaskException {
    public TaskGetterException(String message, Object... args) {
        super(message,args);
    }

    @Override
    public String getDetailMessage(){
        return String.format("Ошибка получения: %s", message);
    }
}
