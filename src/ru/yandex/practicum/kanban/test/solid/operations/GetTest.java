package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetTest extends Tester {
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        String file = Helper.getFile("get");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (line.isBlank()) continue;
                if (!TestValidator.validateLine(line)) {
                    Helper.printMessage(Helper.WRONG_RECORD, line);
                    continue;
                }
                String[] records = line.split(",");
                get(records, 1);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager,false);
    }

    public GetTest() {
    }

    public GetTest(TaskManager taskManager) {
        super(taskManager);
    }

    protected void get(String[] records, int index) {
        for (int i = index + 1; i < records.length; i++) {
            if (records[i].isBlank()) continue;
            String objectType = records[index].trim().toLowerCase();
            try {
                List<Task> tasks = new ArrayList<>();
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
                    case "all":
                        tasks.addAll(taskManager.getAll());
                        break;
                    case "allepic":
                        tasks.addAll(taskManager.getAllEpics());
                        break;
                    case "alltask":
                        tasks.addAll(taskManager.getAllTasks());
                        break;
                    case "allsubtask":
                        tasks.addAll(taskManager.getAllSubTasks());
                        break;
                    case "EpicSubtask":
                        tasks.addAll(taskManager.getAllSubtaskByEpic((Epic) taskManager.getEpic(records[i].trim())));
                        break;
                    default:
                        break;
                }
                if (tasks.isEmpty()) return;
                Printer.printSortedTasks(tasks);
            } catch (TaskGetterException e) {
                Helper.printMessage(e.getMessage());
            }
        }
    }
}
