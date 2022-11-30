package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class RemoveTest extends Tester {
    public RemoveTest() {
    }

    public RemoveTest(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager,"del", RemoveTest::remove);
/*        this.taskManager = taskManager;
        String file = Helper.getFile("del");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (!line.isBlank()) {
                    if (TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);
                    String[] records = line.split(",");
                    remove(line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }*/
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager,false);
    }
    protected static void remove(String line) {
        String[] records = line.split(",");
        try {
            if (records.length == 2) {
                runSimpleRemoveOperation(records);
                return;
            }
            removeTaskById(records);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private static void removeTaskById(String[] records) throws TaskGetterException {
        for (int i = 2; i < records.length; i++) {
            if (records[i].isBlank()) continue;
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
                    break;
            }
        }
    }

    private static void runSimpleRemoveOperation(String[] records) throws TaskGetterException {
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
                return;
        }
    }
}
