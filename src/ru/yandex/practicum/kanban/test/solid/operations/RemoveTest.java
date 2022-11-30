package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

public class RemoveTest extends Tester {
    public RemoveTest(TaskManager taskManager) {
        super(taskManager);
    }

    public RemoveTest() {
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        String file = Helper.getFile("del");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (line.isBlank()) continue;
                if (!TestValidator.validateLine(line)) {
                    Helper.printMessage(Helper.WRONG_RECORD, line);
                    continue;
                }
                String[] records = line.split(",");
                remove(records, 1);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager,false);
    }
    protected void remove(String[] records, int index) {
        try {
            if (index == records.length - 1) {
                runSimpleRemoveOperation(records, index);
                return;
            }
            removeTaskById(records, index);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private void removeTaskById(String[] records, int index) throws TaskGetterException {
        for (int i = index + 1; i < records.length; i++) {
            if (records[i].isBlank()) continue;
            String typeOperation = records[index].trim().toLowerCase();
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

    private void runSimpleRemoveOperation(String[] records, int index) throws TaskGetterException {
        switch (records[index].trim().toLowerCase()) {
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
