package ru.yandex.practicum.kanban.managers;

public class Managers<T extends Manager> {
    private final T manager;

    public Managers(T manager) {
        this.manager = manager;
    }

    public T getDefault() {
        return manager;
    }

//    public Test getTester() {
//        if (manager instanceof FileBackedTasksManager) {
//            return new TesterBackend((TaskManager) manager);
//        } else if (manager instanceof InMemoryTaskManager) {
//            return new TesterInMemory((TaskManager) manager);
//        }
//        return null;
//    }
}