package ru.yandex.practicum.kanban.exceptions;

public class TaskException extends Exception {
    protected final String message;
    public TaskException(String message, Object... args) {
        super(message);
        this.message = String.format(getMessage(),args);
    }

    public String getDetailMessage(){
        return String.format(">>Ошибка : %s",message);
    }
}
