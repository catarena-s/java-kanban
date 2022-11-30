package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.managers.TaskManager;

public interface Test {
    void runTest(TaskManager taskManager,boolean isPrintHistory);
    void runTest(TaskManager taskManager);
}
