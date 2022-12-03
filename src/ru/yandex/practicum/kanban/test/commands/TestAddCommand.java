package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Helper;

public class TestAddCommand extends AbstractTest {
    public TestAddCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    public TestAddCommand() {
    }

    private static Epic lastEpic = null;

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.ADD, this::insert);
    }

    protected void insert(String line) {
        String[] records = line.split(",");
        Record newData = new Record();
        newData.epicID = lastEpic != null ? lastEpic.getTaskID() : "";
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
        initTask(records, newData);
        try {
            Task newTask = createNewTask(newData, type);

            if (newTask instanceof Epic) lastEpic = (Epic) newTask;
        } catch (TaskGetterException | TaskAddException e) {
            Helper.printMessage(e.getDetailMessage());
        }
    }

    private void initTask(String[] records, Record newdata) {
        for (int i = 2; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "id":
                    newdata.id = data[1].trim();
                    break;
                case "name":
                    newdata.name = data[1].trim();
                    break;
                case "description":
                    newdata.description = data[1].trim();
                    break;
                case "epicId": {
                    String[] type = records[1].trim().split("=");
                    if ("SUB_TASK".equals(type[1].toUpperCase().trim())) {
                        newdata.epicID = data[1].trim();
                    }
                    break;
                }
                default:
            }
        }
    }

    private Task createNewTask(Record newData, TaskType type) throws TaskGetterException, TaskAddException {
        if (type == null) throw new TaskAddException("не указан тип задачи.\n");
        Task task = type.create();
        if (task != null) {
            task.init(newData.id, newData.name, newData.description, newData.epicID);
            task.setStatus(TaskStatus.NEW);
            taskManager.add(task);
        }
        return task;
    }

    private static class Record {
        String id = "";
        String name = "";
        String description = "";
        String epicID = "";

    }
}
