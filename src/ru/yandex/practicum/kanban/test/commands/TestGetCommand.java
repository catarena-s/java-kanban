package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.util.ArrayList;
import java.util.List;

public class TestGetCommand extends AbstractTest {
    public TestGetCommand() {
    }

    public TestGetCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.GET,this::get);
    }

    protected void get(String line) {
        String[] records = line.split(",");
        List<Task> tasks = new ArrayList<>();
        String objectType = records[1].trim().toLowerCase();
        if (records.length == 2) {
            tasks.addAll(testShortOperation(objectType));
        } else {
            tasks.addAll(getWithParams(records, objectType));
        }
        if (tasks.isEmpty()) return;
        if (isPrintHistory) Printer.printSortedTasks(tasks);
    }

    private List<Task> getWithParams(String[] records, String objectType) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 2; i < records.length; i++) {
            if (records[i].isBlank()) continue;
            try {
                switch (objectType) {
                    case "task":
                        tasks.add(taskManager.getTask(records[i].trim()));
                        break;
                    case "epic":
                        tasks.add(taskManager.getEpic(records[i].trim()));
                        break;
                    case "sub_task":
                        tasks.add(taskManager.getSubtask(records[i].trim()));
                        break;
                    case "epicsubtask":
                        tasks.addAll(taskManager.getAllSubtaskByEpic((Epic) taskManager.getEpic(records[i].trim())));
                        break;
                    default:
                        break;
                }
            } catch (TaskGetterException e) {
                Helper.printMessage(e.getDetailMessage());
            }
        }
        return tasks;
    }

    private List<Task> testShortOperation(String objectType) {
        switch (objectType) {
            case "all":
                return taskManager.getAll();
            case "allepic":
                return taskManager.getAllEpics();
            case "alltask":
                return taskManager.getAllTasks();
            case "allsubtask":
                return taskManager.getAllSubTasks();
            default:
                return new ArrayList<>();
        }
    }
}
