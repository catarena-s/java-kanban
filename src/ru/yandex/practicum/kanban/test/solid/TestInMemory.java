package ru.yandex.practicum.kanban.test.solid;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

public class TestInMemory extends TestBackend{

    public TestInMemory(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void runTest(int answer) {
        super.runTest(answer);
        Helper.printSeparator();
        Printer.printAllTaskManagerList(taskManager);
        Helper.printSeparator();
        Printer.printHistory(taskManager);
        Helper.printSeparator();
    }
}
