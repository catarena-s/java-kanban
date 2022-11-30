package ru.yandex.practicum.kanban.test.solid;

import ru.yandex.practicum.kanban.managers.TaskManager;

public interface Test1 {
    void runTest(TaskManager taskManager,boolean isPrintHistory);
    void runTest(TaskManager taskManager);
}
