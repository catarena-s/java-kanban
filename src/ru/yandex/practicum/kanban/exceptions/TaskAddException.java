package ru.yandex.practicum.kanban.exceptions;

import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public class TaskAddException extends TaskException {
    public TaskAddException(String message, Object... args) {
        super(message,args);
    }

    @Override
    public String getDetailMessage(){
        String format = Helper.getColoredString( ">> Ошибка добавления: %s",Helper.getItalic(Colors.RED));
        return String.format(format, message);
    }
}
