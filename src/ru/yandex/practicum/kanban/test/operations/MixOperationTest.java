package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class MixOperationTest extends Tester {
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        run(taskManager, "mix",MixOperationTest::runTestOperation);
/*        this.taskManager = taskManager;
        String file = Helper.getFile("mix");
        Epic lastEpic = null;
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (!line.isBlank()) {
                    if (TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);
                    //   lastEpic = (Epic) runTestOperation(line, lastEpic, isPrintHistory);
                    runTestOperation(line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }*/
    }
    private static Epic lastEpic = null;
    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager, false);
    }

    //    private Task runTestOperation(String line, Task epic, boolean isPrintHistory) {
    private static void runTestOperation(String line) {
        String[] records = line.split(",");
        Helper.printMessage("Test: [ %s ]%n", line);
        switch (records[0].trim().toLowerCase()) {
            case "add":
                AdditionalTest addTest = new AdditionalTest(taskManager, isPrintHistory);
//                epic = addTest.insert(records, (Epic) epic);
//                epic =
                lastEpic = (Epic) addTest.insert(line);
                break;
            case "del":
                RemoveTest removeTest = new RemoveTest(taskManager, isPrintHistory);
                removeTest.remove(line);
                break;
            case "upd":
                UpdateTest updateTest = new UpdateTest(taskManager, isPrintHistory);
                updateTest.update(line);
                break;
            case "get":
                GetTest getTest = new GetTest(taskManager, isPrintHistory);
                getTest.get(line);
                break;
            default:
                Helper.printMessage(Helper.WRONG_RECORD, line);
        }
//        return epic;
    }
}
