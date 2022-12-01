package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

public class TestRemoveCommand extends AbstractTest {
    public TestRemoveCommand() {
    }

    public TestRemoveCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.REMOVE, this::remove);
    }

    protected void remove(String line) {
        String[] records = line.split(",");
        try {
            if (records.length == 2) {
                runSimpleRemoveOperation(records);
            }else {
                removeTaskById(records);
            }
        } catch (TaskGetterException|TaskRemoveException e) {
            Helper.printMessage(e.getDetailMessage());
        }
        if (isPrintHistory) Printer.printAllTaskManagerList(taskManager);
    }

    private void removeTaskById(String[] records) throws TaskGetterException, TaskRemoveException {
        for (int i = 2; i < records.length; i++) {
            String typeOperation = records[1].trim().toLowerCase();
            switch (typeOperation) {
                case "task":
                    taskManager.removeTask(records[i].trim());
                    break;
                case "epic":
                    taskManager.removeEpic(records[i].trim());
                    break;
                case "sub_task":
                    taskManager.removeSubtask(records[i].trim());
                    break;
                default:
            }
        }
    }

    private void runSimpleRemoveOperation(String[] records) throws TaskGetterException {
        switch (records[1].trim().toLowerCase()) {
            case "allepic": {
                taskManager.removeAllEpics();
                return;
            }
            case "alltask": {
                taskManager.removeAllTasks();
                return;
            }
            case "allsubtask": {
                taskManager.removeAllSubtasks();
                return;
            }
            case "all": {
                taskManager.clear();
                return;
            }
            default:
        }
    }
}
