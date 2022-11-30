package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class AdditionalTest extends Tester {
    public AdditionalTest(TaskManager taskManager) {
        super(taskManager);
    }

    public AdditionalTest() {

    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        String file = Helper.getFile("add");
        Epic lastEpic = null;
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (line.isBlank()) continue;
                if (!TestValidator.validateLine(line)) {
                    Helper.printMessage(Helper.WRONG_RECORD, line);
                    continue;
                }
                String[] records = line.split(",");
                lastEpic = (Epic) insert(records, 1, lastEpic);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager, false);
    }

    protected Task insert(String[] records, int index, Epic lastEpic) {
        Record newData = new Record();
        newData.epicID = lastEpic != null ? lastEpic.getTaskID() : "";
        String[] type = records[index].trim().split("=");

        initNewTask(records, index, newData);
        try {
            Task newTask = createNewTask(newData, type);
            if (newTask instanceof Epic) return newTask;
            else return lastEpic;
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
        return null;
    }

    private void initNewTask(String[] records, int index, Record newdata) {
        for (int i = index + 1; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "name":
                    newdata.name = data[1].trim();
                    break;
                case "description":
                    newdata.description = data[1].trim();
                    break;
                case "epicId": {
                    if (records[index - 1].trim().equals("SUB_TASK")) {
                        newdata.epicID = data[1].trim();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private Task createNewTask(Record newData, String[] type) throws TaskGetterException {
        if (type[1].toUpperCase().trim().equals("SUB_TASK")) {
            SubTask subTask = new SubTask(newData.name, newData.description, newData.epicID);
            taskManager.addSubtask(subTask);
            return subTask;
        } else if (type[1].toUpperCase().trim().equals("EPIC")) {
            Epic epic = new Epic(newData.name, newData.description);
            taskManager.addEpic(epic);
            return epic;
        } else if (type[1].toUpperCase().trim().equals("TASK")) {
            Task task = new Task(newData.name, newData.description);
            taskManager.addTask(task);
            return task;
        }
        return null;
    }

    private class Record {
        String name = "";
        String description = "";
        String epicID = "";

        String status = "";

    }
}
