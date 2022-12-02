package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

public class TestBackend implements Tester {
    protected TaskManager taskManager;

    public TestBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void runTest(int answer, boolean printToConsole) {
        TestCommand command = TestCommand.getCommand(answer);
        if (command == null) return;
        if (command.equals(TestCommand.PRINT)) {
            Printer.printAllTaskManagerList(taskManager);
            Helper.printSeparator();
            Printer.printHistory(taskManager);
            Helper.printSeparator();
        } else {
            command.getTest().runTest(taskManager, printToConsole);
        }
    }

    @Override
    public void runTest(int answer) {
        runTest(answer, false);
    }

}
