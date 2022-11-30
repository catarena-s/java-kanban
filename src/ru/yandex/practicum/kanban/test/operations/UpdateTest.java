package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class UpdateTest extends Tester {
    public UpdateTest() {
    }

    public UpdateTest(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager,"upd", UpdateTest::update);
/*
        this.taskManager = taskManager;
        String file = Helper.getFile("upd");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (!line.isBlank()) {
                    if (TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);

                    update(line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
*/
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager, false);
    }

    protected static void update(String line) {
        String[] records = line.split(",");
        String[] dataId = records[2].split("=");
        String id = dataId[1].trim();
        try {
            Task task = taskManager.getById(id);
            if (task != null)
                updateData(records, task);
        } catch (TaskGetterException ex) {
            Helper.printMessage(ex.getMessage());
        }
    }

    private static void updateData(String[] records, Task task) {
        for (int i = 3; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private static void setNewDataTask(Task task, String[] data) {
        switch (data[0].trim()) {
            case "name":
                task.setName(data[1].trim());
                break;
            case "status":
                task.setStatus(TaskStatus.valueOf(data[1].toUpperCase().trim()));
                break;
            case "description":
                task.setDescription(data[1].trim());
                break;
            default:
                break;
        }
    }
}
