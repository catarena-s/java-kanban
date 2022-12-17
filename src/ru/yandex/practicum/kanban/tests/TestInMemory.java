package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

public class TestInMemory extends TestBackend{
    public TestInMemory(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void runTest(int answer, boolean printToConsole) {
        super.runTest(answer,printToConsole);
        Helper.printSeparator();
        if(answer != TestCommand.GET.getValue()) {
            TaskPrinter.printAllTaskManagerList(taskManager);
            Helper.printSeparator();
        }
        TaskPrinter.printHistory(taskManager);
        Helper.printSeparator();
    }
    @Override
    public void runTest(int answer) {
        runTest(answer, true);
    }
}
