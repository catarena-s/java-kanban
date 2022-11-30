package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class UpdateTest  extends Tester {
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        String file = Helper.getFile("upd");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (line.isBlank()) continue;
                if (!TestValidator.validateLine(line)) {
                    Helper.printMessage(Helper.WRONG_RECORD, line);
                    continue;
                }
                String[] records = line.split(",");
                update(records, 1);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager,false);
    }

    public UpdateTest() {
    }

    public UpdateTest(TaskManager taskManager) {
        super(taskManager);
    }

    protected void update(String[] records, int index) {
        String[] dataType = records[index].split("=");
        TaskType type = TaskType.valueOf(dataType[1].trim().toUpperCase());
        String[] dataId = records[index + 1].split("=");
        String id = dataId[1].trim();
        try {
            Task task = taskManager.getById(id);
            if (task != null)
                updateData(records, task, index + 2);
        } catch (TaskGetterException ex) {
            Helper.printMessage(ex.getMessage());
        }
    }

    private void updateData(String[] records, Task task, int index) {
        for (int i = index; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private void setNewDataTask(Task task, String[] data) {
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
