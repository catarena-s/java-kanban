package ru.yandex.practicum.kanban.tests.utils.commands;

import ru.yandex.practicum.kanban.managers.TaskManager;

public interface TestRunner {
    void runTest(TaskManager taskManager, boolean isPrintHistory);
}
