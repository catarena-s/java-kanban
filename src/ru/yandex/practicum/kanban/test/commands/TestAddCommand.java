package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.Comparator;
import java.util.List;

public class TestAddCommand extends AbstractTest {
    public TestAddCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    public TestAddCommand() {
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.ADD, this::insert);
    }

    protected void insert(String line) {
        try {
            executeString(line, taskManager);
        } catch (TaskGetterException | TaskAddException e) {
            Helper.printMessage(Colors.RED, e.getDetailMessage());
        }
    }

    public static Task executeString(String line, TaskManager taskManager) throws TaskGetterException, TaskAddException {
        String[] records = line.split(",");
        Record newData = new Record();
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
        if (TaskType.SUB_TASK.equals(type)) {
            newData.epicID = getLastEpic(taskManager);
        }
        initTask(line, newData);
        Task newTask = createNewTask(newData, type);
        taskManager.add(newTask);

        return newTask;
    }

    private static String getLastEpic(TaskManager taskManager) {
        List<Task> epic = taskManager.getAllEpics();
        return (epic != null && !epic.isEmpty()) ? epic.stream().sorted(Comparator.reverseOrder()).findFirst().get().getTaskID() : "";
    }

    private static void initTask(String line, Record newdata) {
        String[] records = line.split(",");
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
                case "status":
                    newdata.status = data[1].trim().toUpperCase();
                    break;
                case "epicId": {
                    String type = records[1].trim();
                    if ("SUB_TASK".equals(type.toUpperCase().trim())) {
                        newdata.epicID = data[1].trim();
                    }
                    break;
                }
                default:
            }
        }
    }

    private static Task createNewTask(Record newData, TaskType type) throws TaskGetterException, TaskAddException {
        if (type == null) throw new TaskAddException("не указан тип задачи.");
        Task task = type.create();
        if (task != null) {
            task.init(newData.id, newData.name, newData.description, newData.epicID);
            task.builder().status(TaskStatus.NEW);
            if (!(task instanceof Epic) && !newData.status.isBlank()) {
                TaskStatus status = TaskStatus.valueOf(newData.status);
                if (status != null) task.builder().status(status);
            }
        }
        return task;
    }

    private static class Record {
        String id = "";
        String name = "";
        String description = "";
        String epicID = "";
        String status = "";

    }
}
