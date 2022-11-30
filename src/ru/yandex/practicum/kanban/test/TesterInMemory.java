package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

public class TesterInMemory extends TesterBackend implements Test {

    public TesterInMemory(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void initTaskManager() {
        super.initTaskManager();
        Printer.printAllTaskManagerList(taskManager);
        Helper.printSeparator();
        Printer.printHistory(taskManager);
        Helper.printSeparator();
    }

    @Override
    public void testUpdateTasks() {
        super.testUpdateTasks();
        Printer.printAllTaskManagerList(taskManager);
        Helper.printSeparator();
        Printer.printHistory(taskManager);
        Helper.printSeparator();
    }

    @Override
    public void testRemoveTasks() {
        super.testRemoveTasks();
        Printer.printAllTaskManagerList(taskManager);
        Helper.printSeparator();
        Printer.printHistory(taskManager);
        Helper.printSeparator();
    }

    @Override
    public void testGetOperations() {
        super.testGetOperations();
        Printer.printHistory(taskManager);
    }

    @Override
    public void testMixOperations() {
        super.testMixOperations();
        Printer.printAllTaskManagerList(taskManager);
        Helper.printSeparator();
        Printer.printHistory(taskManager);
        Helper.printSeparator();
    }


}
