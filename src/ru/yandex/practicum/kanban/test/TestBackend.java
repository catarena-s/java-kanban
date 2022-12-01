package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.TaskManager;

public class TestBackend implements Tester{
    protected TaskManager taskManager;

    public TestBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void runTest(int answer, boolean printToConsole) {
        TestCommand command = TestCommand.getCommand(answer);
        if (command == null) return;
        command.getTest().runTest(taskManager, printToConsole);
    }

    @Override
    public void runTest(int answer) {
        runTest(answer, false);
    }

}
