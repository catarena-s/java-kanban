package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.solid.*;
import ru.yandex.practicum.kanban.utils.UserMenu;

public class TestMenager {

    public static TestBackend get(TaskManager manager)
    {
        if (manager instanceof FileBackedTasksManager) {
            return new TestBackend(manager);
        } else if (manager instanceof InMemoryTaskManager) {
            return new TestInMemory( manager);
        }
        return null;
    }


}
