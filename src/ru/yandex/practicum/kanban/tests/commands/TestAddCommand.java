package ru.yandex.practicum.kanban.tests.commands;


import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Converter;
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
        } catch (Exception e) {
            Helper.printMessage(Colors.RED, e.getMessage());
        }
    }

    public static Task executeString(String line, TaskManager taskManager) throws TaskException {
        Task newTask = parseLine(line, taskManager);
        taskManager.add(newTask);

        return newTask;
    }

    public static Task parseLine(String line, TaskManager taskManager) throws TaskException {
        Converter.Record newData = initTask(line, taskManager);
        return Converter.createNewTask(newData);
    }

    private static String getLastEpic(TaskManager taskManager) {
        Optional<List<? extends Task>> epic = Optional.ofNullable(taskManager.getAllEpics());
        return (epic.isPresent() && !epic.get().isEmpty()) ?
                epic.get().stream()
                        .sorted(Comparator.reverseOrder())
                        .findFirst().get()
                        .getTaskID() : "";

    }

    private static Converter.Record initTask(String line, TaskManager taskManager) {
        Converter.Record newData = new Converter.Record();
        String[] records = line.split(",");
        newData.type = TaskType.valueOf(records[1].toUpperCase().trim());
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
        return newData;
    }
}
