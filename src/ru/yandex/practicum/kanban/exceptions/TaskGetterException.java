package ru.yandex.practicum.kanban.exceptions;

import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public class TaskGetterException extends TaskException {
    public TaskGetterException(String message, Object... args) {
        super(message,args);
    }

    @Override
    public String getDetailMessage(){
        String format = Helper.getColoredString( ">> Ошибка получения: %s",Helper.getItalic(Colors.RED));
        return String.format(format, message);
    }
}
