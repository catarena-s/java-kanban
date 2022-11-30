package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.OperationType;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class MixOperationTest extends Tester {
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        String file = Helper.getFile("mix");
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
                lastEpic = (Epic) runTestOperation(line, lastEpic);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager,false);
    }

    public MixOperationTest() {
    }

    public MixOperationTest(TaskManager taskManager) {
        super(taskManager);
    }

    private Task runTestOperation(String line, Task epic) {
        String[] records = line.split(",");
        OperationType operationType = OperationType.getByName(records[0].trim().toLowerCase());
        Helper.printMessage("Test: [ %s ]%n", line);
        switch (operationType) {
            case ADD:
                AdditionalTest addTest = new AdditionalTest(taskManager);
                epic = addTest.insert(records, 1, (Epic) epic);
                break;
            case DEL:
                RemoveTest removeTest = new RemoveTest(taskManager);
                removeTest.remove(records, 1);
                break;
            case UPDATE:
                UpdateTest updateTest = new UpdateTest(taskManager);
                updateTest.update(records, 1);
                break;
            case GET:
                GetTest getTest = new GetTest(taskManager);
                getTest.get(records, 1);
                break;
            default:
                Helper.printMessage(Helper.WRONG_RECORD, line);
        }
        return epic;
    }
}
