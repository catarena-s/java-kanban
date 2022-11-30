package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * Изначально, хотелось упростить себе тестирования.
 * Что-бы не в коде искать, где какие тестовые данные, а читать их из файла, ну и не перезапускать каждый раз программу
 * для новых тестовых...
 * и слегка увлеклась...
 *
 */
public class TesterBackend implements Test {
    protected TaskManager taskManager;

    public TesterBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void initTaskManager() {
        testOperations("add");
    }

    @Override
    public void testGetOperations() {
        testOperations("get");
    }

    @Override
    public void testRemoveTasks() {
        testOperations("del");
    }

    @Override
    public void testMixOperations() {
        testOperations("mix");
    }

    @Override
    public void testUpdateTasks() {
        testOperations("upd");
    }

    private void testOperations(String operation) {
        String file = Helper.getFile(operation);
        Epic lastEpic = null;
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (line.isBlank()) continue;
                if (!TestValidator.validateLine(line)) {
                    Helper.printMessage(Helper.WRONG_RECORD, line);
                    continue;
                }

                lastEpic = (Epic) runTestOperation(line, lastEpic);
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }

    private Task runTestOperation(String line, Task epic) {
        String[] records = line.split(",");
        OperationType operationType = OperationType.getByName(records[0].trim().toLowerCase());
        Helper.printMessage("Test: [ %s ]%n", line);
        switch (operationType) {
            case ADD:
                epic = insert(records, 1, (Epic) epic);
                break;
            case DEL:
                remove(records, 1);
                break;
            case UPDATE:
                update(records, 1);
                break;
            case GET:
                get(records, 1);
                break;
            default:
                Helper.printMessage(Helper.WRONG_RECORD, line);
        }
        return epic;
    }

    private void get(String[] records, int index) {
        for (int i = index + 1; i < records.length; i++) {
            if (records[i].isBlank()) continue;
            String objectType = records[index].trim().toLowerCase();
            try {
                List<Task> tasks = new ArrayList<>();
                switch (objectType) {
                    case "task":
                        tasks.add(taskManager.getTask(records[i].trim()));
                        break;
                    case "epic":
                        tasks.add(taskManager.getEpic(records[i].trim()));
                        break;
                    case "sub_task":
                        tasks.add(taskManager.getSubtask(records[i].trim()));
                        break;
                    case "all":
                        tasks.addAll(taskManager.getAll());
                        break;
                    case "allepic":
                        tasks.addAll(taskManager.getAllEpics());
                        break;
                    case "alltask":
                        tasks.addAll(taskManager.getAllTasks());
                        break;
                    case "allsubtask":
                        tasks.addAll(taskManager.getAllSubTasks());
                        break;
                    case "EpicSubtask":
                        tasks.addAll(taskManager.getAllSubtaskByEpic((Epic) taskManager.getEpic(records[i].trim())));
                        break;
                    default:
                        break;
                }
                if (tasks.isEmpty()) return;
                Printer.printSortedTasks(tasks);
            } catch (TaskGetterException e) {
                Helper.printMessage(e.getMessage());
            }
        }
    }

    private Task insert(String[] records, int index, Epic lastEpic) {
        Record newData = new Record();
        newData.epicID = lastEpic != null ? lastEpic.getTaskID() : "";
        String[] type = records[index].trim().split("=");

        initNewTask(records, index, newData);
        try {
            Task newTask = createNewTask(newData, type);
            if (newTask instanceof Epic) return newTask;
            else return lastEpic;
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
        return null;
    }

    private void initNewTask(String[] records, int index, Record newdata) {
        for (int i = index + 1; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "name":
                    newdata.name = data[1].trim();
                    break;
                case "description":
                    newdata.description = data[1].trim();
                    break;
                case "epicId": {
                    if (records[index - 1].trim().equals("SUB_TASK")) {
                        newdata.epicID = data[1].trim();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private Task createNewTask(Record newData, String[] type) throws TaskGetterException {
        if (type[1].toUpperCase().trim().equals("SUB_TASK")) {
            SubTask subTask = new SubTask(newData.name, newData.description, newData.epicID);
            taskManager.addSubtask(subTask);
            return subTask;
        } else if (type[1].toUpperCase().trim().equals("EPIC")) {
            Epic epic = new Epic(newData.name, newData.description);
            taskManager.addEpic(epic);
            return epic;
        } else if (type[1].toUpperCase().trim().equals("TASK")) {
            Task task = new Task(newData.name, newData.description);
            taskManager.addTask(task);
            return task;
        }
        return null;
    }

    private void update(String[] records, int index) {
        String[] dataType = records[index].split("=");
        TaskType type = TaskType.valueOf(dataType[1].trim().toUpperCase());
        String[] dataId = records[index + 1].split("=");
        String id = dataId[1].trim();
        try {
            Task task = getTask(type, id);
            if (task != null)
                updateData(records, task, index + 2);
        } catch (TaskGetterException ex) {
            Helper.printMessage(ex.getMessage());
        }
    }

    private void updateData(String[] records, Task task, int index) {
        for (int i = index; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
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

    private Task getTask(TaskType type, String id) throws TaskGetterException {
        Task task = null;
        switch (type) {
            case TASK: {
                task = taskManager.getTask(id);
                break;
            }
            case EPIC: {
                task = taskManager.getEpic(id);
                break;
            }
            case SUB_TASK: {
                task = taskManager.getSubtask(id);
                break;
            }
        }
        return task;
    }

    private void remove(String[] records, int index) {
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

    private class Record {
        String name = "";
        String description = "";
        String epicID = "";

        String status = "";

    }


}
