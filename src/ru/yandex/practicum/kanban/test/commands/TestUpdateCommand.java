package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestCommand;
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
        String[] records = line.split(",");
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
        String[] dataId = records[2].split("=");
        String id = dataId[1].trim();
        try {
            Task task = getTask(type, id);
            if (task != null)
                updateData(records, task);
        } catch (TaskGetterException ex) {
            Helper.printMessage(ex.getDetailMessage());
        }
    }

    private Task getTask(TaskType type, String id) throws TaskGetterException {
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

    private void updateData(String[] records, Task task) {
        for (int i = 3; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getDetailMessage());
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
