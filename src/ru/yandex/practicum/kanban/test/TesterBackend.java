package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TesterBackend implements TestEdit {
    public static final String FILE_ADD_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_additional.csv";
    public static final String FILE_UPDATE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_update.csv";
    public static final String FILE_REMOVE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_remove.csv";
    public static final String FILE_MIX_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_mix_operations.csv";
    public static final String FILE_GET_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_get_task.csv";
    public static final String WRONG_RECORD = "Некорректная запись: %s%n";
    protected TaskManager taskManager;

    public TesterBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void initTaskManager() {
        testOperations("add");
/*
        Epic lastEpic = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_ADD_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(",");
                String[] typeSting = records[0].split("=");
                TaskType type = TaskType.valueOf(typeSting[1].trim().toUpperCase());

                switch (type) {
                    case EPIC: {
                        lastEpic = (Epic) insertDate(records, 0, null);
                        break;
                    }
                    case SUB_TASK: {
                        if (lastEpic == null) {
                            Helper.printMessage("Не добавлено ни одного эпика %n");
                        }
                        insertDate(records, 0, lastEpic);
                        break;
                    }
                    case TASK: {
                        insertDate(records, 0, null);
                        break;
                    }
                    default:
                        Helper.printMessage(WRONG_RECORD, line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(ERROR_FILE_READING, FILE_ADD_TEST_DATA);
        }
*/
    }

    @Override
    public void testGetOperations() {
        testOperations("get");
       /* try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_GET_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (!line.isBlank()) {

                    String[] records = line.split(" ");
                    if (!TestValidator.validateGetLine(records, 0)) {
                        Helper.printMessage(WRONG_RECORD, line);
                        continue;
                    }

                    getTask(records, 0);
                }
            }

        } catch (IOException ex) {
            Helper.printMessage(ERROR_FILE_READING, FILE_MIX_TEST_DATA);
        }*/
    }

    @Override
    public void testRemoveTasks() {
        testOperations("del");
      /*  try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_REMOVE_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(" ");
                if (!TestValidator.validateRemoveLine(records, 0)) {
                    Helper.printMessage(WRONG_RECORD, line);
                    continue;
                }
                remove(records, 0);
            }
        } catch (IOException ex) {
            Helper.printMessage(ERROR_FILE_READING, FILE_REMOVE_TEST_DATA);
        }*/
    }

    @Override
    public void testMixOperations() {
        testOperations("mix");
    }

    @Override
    public void testUpdateTasks() {
        testOperations("upd");
/*        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_UPDATE_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(",");
                if (!TestValidator.validateUpdateLine(records)) {
                    Helper.printMessage(WRONG_RECORD, line);
                    continue;
                }

                update(records, 0);
            }
        } catch (IOException ex) {
            Helper.printMessage(ERROR_FILE_READING, FILE_UPDATE_TEST_DATA);
        }*/
    }

    private void testOperations(String operation) {
        String file = getFile(operation);
        Epic lastEpic = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (!line.isBlank()) {

                    if (!TestValidator.validateLine(line)) {
                        Helper.printMessage(WRONG_RECORD, line);
                        continue;
                    }

                    lastEpic = (Epic) runTestOperation(line, lastEpic);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, FILE_MIX_TEST_DATA);
        }
    }

    private String getFile(String operation) {
        switch (operation) {
            case "add":
                return FILE_ADD_TEST_DATA;
            case "del":
                return FILE_REMOVE_TEST_DATA;
            case "upd":
                return FILE_UPDATE_TEST_DATA;
            case "get":
                return FILE_GET_DATA;
            case "mix":
                return FILE_MIX_TEST_DATA;
            default:
                return "";
        }
    }

    private Task runTestOperation(String line, Task epic) {
        String[] records = line.split(",");
        OperationType operationType = OperationType.getByName(records[0].trim().toLowerCase());

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
                Helper.printMessage(WRONG_RECORD, line);
        }
        /*switch (records[0].trim().toLowerCase()) {
            case "add": {
                epic = insertDate(records, 1, (Epic) epic);
                break;
            }
            case "upd": {
                update(records, 1);
                break;
            }
            case "del": {
                remove(records, 1);
                break;
            }
            case "get": {
                getTask(records, 1);
                break;
            }
            default:
                Helper.printMessage(WRONG_RECORD, line);
        }*/
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
