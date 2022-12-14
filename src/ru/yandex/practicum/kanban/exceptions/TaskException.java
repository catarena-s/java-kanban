package ru.yandex.practicum.kanban.exceptions;

import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public abstract class TaskException extends Exception {
    protected final String message;

    public TaskException(String message, Object... args) {
        super(message);
        this.message = String.format(getMessage(), args);
    }

    public String getDetailMessage() {
//        String format = Helper.getColoredString(">> Ошибка: %s",Helper.getItalic(Colors.RED));
        String format = "Ошибка: %s";
        return String.format(format, message);
    }
}
