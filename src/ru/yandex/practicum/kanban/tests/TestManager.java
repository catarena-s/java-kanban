package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TaskManager;

public class TestManager {
    private TestManager(){}

    public static Tester get(TaskManager manager)
    {
        if (manager instanceof FileBackedTasksManager) {
            return new TestBackend(manager);
        } else if (manager instanceof InMemoryTaskManager) {
            return new TestInMemory( manager);
        }
        return null;
    }


}
