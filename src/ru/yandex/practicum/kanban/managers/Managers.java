package ru.yandex.practicum.kanban.managers;

public class Managers<T extends Manager> {
    private final T manager;

    public Managers(T manager) {
        this.manager = manager;
    }

    public T getDefault() {
        return manager;
    }
}
