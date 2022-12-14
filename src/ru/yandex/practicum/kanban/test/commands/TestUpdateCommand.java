package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public class TestUpdateCommand extends AbstractTest {
    public TestUpdateCommand() {
    }

    public TestUpdateCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.UPDATE, this::update);
    }

    protected void update(String line) {
        try {
            executeString(line, taskManager,true);
        } catch (TaskGetterException ex) {
            Helper.printMessage(Colors.RED, ex.getDetailMessage());
        }
    }

    public static Task executeString(String line, TaskManager taskManager, boolean isPrint) throws TaskGetterException {
        String[] records = line.split(",");
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
        String[] dataId = records[2].split("=");
        String id = dataId[1].trim();
        Task task = getTask(type, id, taskManager);
        if (task != null) {
            if (isPrint)
                Helper.printMessage("Task before update: %s", task.toCompactString());
            updateData(records, task, taskManager);
        }
        return task;
    }

    private static Task getTask(TaskType type, String id, TaskManager taskManager) throws TaskGetterException {
        switch (type) {
            case TASK:
                return taskManager.getTask(id);
            case SUB_TASK:
                return taskManager.getSubtask(id);
            case EPIC:
                return taskManager.getEpic(id);
        }
        return null;
    }

    private static void updateData(String[] records, Task task, TaskManager taskManager) {
        for (int i = 3; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(Colors.RED, e.getDetailMessage());
        }
    }

    private static void setNewDataTask(Task task, String[] data) {
        switch (data[0].trim()) {
            case "name":
                task.builder().name(data[1].trim());
                break;
            case "status":
                task.builder().status(TaskStatus.valueOf(data[1].toUpperCase().trim()));
                break;
            case "description":
                task.builder().description(data[1].trim());
                break;
            default:
                break;
        }
    }
}
