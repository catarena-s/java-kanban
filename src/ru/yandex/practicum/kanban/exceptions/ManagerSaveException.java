package ru.yandex.practicum.kanban.exceptions;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(String message) {
        super(message);
    }
}
