package ru.yandex.practicum.kanban.tests.utils.commands;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.tests.utils.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        Task newTask = parseLine(line, taskManager);
        taskManager.add(newTask);

        return newTask;
    }

    public static Task parseLine(String line, TaskManager taskManager) throws TaskAddException {
        String[] records = line.split(",");
        Record newData = new Record();
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
        if (TaskType.SUB_TASK.equals(type)) {
            newData.epicID = getLastEpic(taskManager);
        }
        initTask(line, newData);
        Task newTask = createNewTask(newData, type);
        return newTask;
    }

    private static String getLastEpic(TaskManager taskManager) {
        Optional<List<Task>> epic = Optional.ofNullable(taskManager.getAllEpics());
        return (epic.isPresent() && !epic.get().isEmpty()) ?
                epic.get().stream()
                        .sorted(Comparator.reverseOrder())
                        .findFirst().get()
                        .getTaskID() : "";

    }

    private static void initTask(String line, Record newData) {
        String[] records = line.split(",");
        for (int i = 2; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "id":
                    newData.id = data[1].trim();
                    break;
                case "name":
                    newData.name = data[1].trim();
                    break;
                case "description":
                    newData.description = data[1].trim();
                    break;
                case "status":
                    newData.status = data[1].trim().toUpperCase();
                    break;
                case "duration": {
                    newData.duration = data[1].trim();
                    break;
                }
                case "start_time": {
                    newData.startTime = data[1].trim();
                    break;
                }
                case "epicId": {
                    String type = records[1].trim();
                    if ("SUB_TASK".equals(type.toUpperCase().trim())) {
                        newData.epicID = data[1].trim();
                    }
                    break;
                }
                default:
            }
        }
    }

    private static Task createNewTask(Record newData, TaskType type) throws TaskAddException {
        if (type == null) throw new TaskAddException("не указан тип задачи.");
        Task task = type.create();
        if (task != null) {
            task.init(newData.id, newData.name, newData.description);
            if (task instanceof Updateable) {
                if (!newData.duration.isBlank())
                    ((Updateable) task).updateDuration(Integer.parseInt(newData.duration));
                if (!newData.status.isBlank())
                    ((Updateable) task).updateStatus(TaskStatus.valueOf(newData.status));
                if (!newData.startTime.isBlank())
                    ((Updateable) task).updateStartTime(newData.startTime);
            }
            if (task instanceof SubTask) {
                ((SubTask) task).builder().epic(newData.epicID);
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
        String duration = "0";
        String startTime = "01-01-2222 00:00";

    }
}
