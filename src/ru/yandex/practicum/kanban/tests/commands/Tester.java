package ru.yandex.practicum.kanban.tests.commands;

import ru.yandex.practicum.kanban.managers.TaskManager;

public interface Tester {
    void runTest(TaskManager taskManager, boolean isPrintHistory);
}
