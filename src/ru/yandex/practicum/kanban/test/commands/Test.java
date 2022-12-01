package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.managers.TaskManager;

public interface Test {
    void runTest(TaskManager taskManager, boolean isPrintHistory);
}
