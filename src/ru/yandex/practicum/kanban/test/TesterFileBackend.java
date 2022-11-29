package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class TesterFileBackend implements TestEdit {

    public static final String FILE_REMOVE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_remove.csv";
    public static final String FILE_MIX_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_mix_operations.csv";
    public static final String FILE_ADD_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_additional.csv";
    public static final String FILE_UPDATE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_update.csv";
    TaskManager taskManager;

    public TesterFileBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void initTaskManager() {
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
                            Helper.printMessage("Не добавлено ни одной подзадачи %n");
                        }
                        insertDate(records, 0, lastEpic);
                        break;
                    }
                    case TASK: {
                        insertDate(records, 0, null);
                        break;
                    }
                    default:
                        Helper.printMessage("некорректная строка: %s%n", line);
                }

            }
        } catch (IOException ex) {
            Helper.printMessage("Ошибка чтения из файла: %s", FILE_ADD_TEST_DATA);
        }
 /*       Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task);

        task = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task);

        task = new Task("Задача 3", "Описание задачи 3");
        taskManager.addTask(task);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача 1-1", "Описание подзадачи 1-1", epic.getTaskID());
        taskManager.addSubtask(subTask);

        subTask = new SubTask("Подзадача 1-2", "Описание подзадачи 1-2", epic.getTaskID());
        taskManager.addSubtask(subTask);

        subTask = new SubTask("Подзадача 1-3", "Описание подзадачи 1-2", epic.getTaskID());
        taskManager.clone(subTask);

        taskManager.addSubtask(subTask);
        taskManager.addSubtask(subTask);
        taskManager.clone(subTask);
        taskManager.clone(subTask);

        epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic);*/
    }

    @Override
    public void testUpdateTasks() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_UPDATE_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(",");
                if (!validate(records)) {
                    Helper.printMessage("Некорректная запись: %s%n", line);
                    continue;
                }

                String[] dataType = records[0].split("=");
                TaskType type = TaskType.valueOf(dataType[1].trim().toUpperCase());
                String[] dataId = records[1].split("=");
                String id = dataId[1].trim();

                switch (type) {
                    case TASK: {
                        updateData(records, taskManager.getTask(id), 2);
                        break;
                    }
                    case EPIC: {
                        updateData(records, taskManager.getEpic(id), 2);
                        break;
                    }
                    case SUB_TASK: {
                        updateData(records, taskManager.getSubtask(id), 2);
                        break;
                    }
                }

/*                for (int i=2 ;i < records.length;i++) {
                    String[] data = records[i].split("=");
                    switch (data[0].trim()){
                        case "name" : break;
                        case "status" : break;
                        case "description" : break;
                    }
                }*/

            }

        } catch (IOException ex) {
            Helper.printMessage("Ошибка чтения из файла" + FILE_UPDATE_TEST_DATA);
        }
       /* String currentID = "0001";
        Task task = taskManager.getTask(currentID);
        task.setName("New name 001");
        task.setStatus(TaskStatus.DONE);
//        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
       /* Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();*/
        ///------------------
    /*    currentID = "0004";
        Epic epic = (Epic) taskManager.getEpic(currentID);
        epic.setName("New nape 004");
//        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(epic);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();
//------Обновляем подзадачу
     /*  currentID = "0005";
        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask);
        subTask = (SubTask) taskManager.getSubtask(currentID);
/*
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);

        printHistory(taskManager);
        Helper.printEmptySting();*/
//------Обновляем подзадачу
//        currentID = "0006";
//        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
//        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);
//
//        subTask.setName("new subTaskName" + currentID);
//        subTask.setDescription("new subTask Description +++++++++++++++" + currentID);
//        subTask.setStatus(TaskStatus.DONE);
//        taskManager.updateTask(subTask);
//        subTask = (SubTask) taskManager.getSubtask(currentID);

//        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
//        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);

//        printHistory(taskManager);
//        Helper.printEmptySting();/**/
//------Удаляем подзадачу
 /*      currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        taskManager.removeSubtask(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();

//------Добавляем подзадачу
   /*     subTask = new SubTask("Подзадача 1-3", "Описание подзадачи 1-3", subTask.getEpicID());
        taskManager.addSubtask(subTask);
        printHistory(taskManager);
        Helper.printMessage(Helper.MSG_ADD_TASK, subTask.getTaskID(), subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();
//------Удаляем подзадачу
        currentID = "0006";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        taskManager.removeSubtask(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();*/
    }

    private Task insertDate(String[] records, int index, Epic lastEpic) {
        String name = "";
        String description = "";
        String epicID = lastEpic != null ? lastEpic.getTaskID() : "";
        String[] type = records[index].trim().split("=");

        for (int i = index + 1; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "name":
                    name = data[1].trim();
                    break;
                case "description":
                    description = data[1].trim();
                    break;
                case "epicId": {
                    if (records[index - 1].trim().equals("SUB_TASK")) {
                        epicID = data[1].trim();
                    }
                }
                default:
                    break;
            }
        }

        if (type[1].toUpperCase().trim().equals("SUB_TASK")) {
            SubTask subTask = new SubTask(name, description, epicID);
            taskManager.addSubtask(subTask);
            return subTask;
        } else if (type[1].toUpperCase().trim().equals("EPIC")) {
            Epic epic = new Epic(name, description);
            taskManager.addEpic(epic);
            return epic;
        } else if (type[1].toUpperCase().trim().equals("TASK")) {
            Task task = new Task(name, description);
            taskManager.addTask(task);
            return task;
        }
        return null;
    }

    private void updateData(String[] records, Task task, int index) {
        for (int i = index; i < records.length; i++) {
            String[] data = records[i].split("=");
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
        taskManager.updateTask(task);
    }

    private boolean validate(String[] records) {
        return records[0].toLowerCase().trim().startsWith("type=") && records[1].toLowerCase().trim().startsWith("id=");
    }

    private boolean validateRemoveLine(String[] records) {
        Set<String> firstRow = Set.of("task", "epic", "subtask", "allepic", "all", "alltask", "allsubtask");
        return firstRow.contains(records[0].trim().toLowerCase());
    }

    @Override
    public void testRemoveTasks() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_REMOVE_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(" ");
                if (!validateRemoveLine(records)) {
                    Helper.printMessage("Некорректная запись: %s%n", line);
                    continue;
                }
                remove(records, 0);
/*                switch (records[0].trim().toLowerCase()) {
                    case "task": {
                        for (int i = 1; i < records.length; i++) {
                            if (records[i].isBlank()) continue;
                            taskManager.removeTask(records[i].trim());
                        }
                        break;
                    }
                    case "epic": {
                        for (int i = 1; i < records.length; i++) {
                            if (records[i].isBlank()) continue;
                            taskManager.removeEpic(records[i].trim());
                        }
                        break;
                    }
                    case "subtask": {
                        for (int i = 1; i < records.length; i++) {
                            if (records[i].isBlank()) continue;
                            taskManager.removeSubtask(records[i].trim());
                        }
                        break;
                    }
                    case "allepic": {
                        taskManager.removeAllEpics();
                        break;
                    }
                    case "alltask": {
                        taskManager.removeAllTasks();
                        break;
                    }
                    case "allsubtask": {
                        taskManager.removeAllSubtasks();
                        break;
                    }
                    case "all": {
                        taskManager.clear();
                        break;
                    }
                }*/

            }
        } catch (IOException ex) {
            Helper.printMessage("Ошибка чтения из файла" + FILE_REMOVE_TEST_DATA);
        }
 /*       String currentID = "0003";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
//        printTaskByID(currentID, taskManager);
        taskManager.removeTask(currentID);
//        printAllTaskManagerList(taskManager);
//        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
//        printTaskByID(currentID, taskManager);
        taskManager.removeSubtask(currentID);
//        printAllTaskManagerList(taskManager);
//        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
//        printTaskByID(currentID, taskManager);
        taskManager.removeEpic(currentID);
//        printAllTaskManagerList(taskManager);
//        printHistory(taskManager);
        Helper.printEmptySting();

        //добавили эпиков
        Helper.printMessage("Добавили эпики\n");
        Epic epic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 4", "Описание эпика 4");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 5", "Описание эпика 5");
        taskManager.addEpic(epic);
//        printAllTaskManagerList(taskManager);
//        printHistory(taskManager);

        Helper.printEmptySting();
        Helper.printMessage(">>Удаляем все %s \n", TaskType.EPIC);
        taskManager.removeAllEpics();
//        printAllTaskManagerList(taskManager);
//        printHistory(taskManager);
        Helper.printEmptySting();
///----->>Удаляем все задач------------------------------------------
        Helper.printMessage(">>Удаляем все задачи\n");
        taskManager.clear();
        Helper.printMessage("Все задачи удалены!\n");
//        printHistory(taskManager);
        Helper.printEmptySting();
        Helper.printEmptySting();*/
    }

    @Override
    public void testMixOperation() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_MIX_TEST_DATA))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) continue;

                String[] records = line.split(",");
                if (!validateMixLine(records)) {
                    Helper.printMessage("Некорректная запись: %s%n", line);
                    continue;
                }
                switch (records[0].trim().toLowerCase()) {
                    case "add": {
                        insertDate(records, 1, null);
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
                    default:
                        Helper.printMessage("Некорректная запись: %s%n", line);
                }

            }
        } catch (IOException ex) {
            Helper.printMessage("Ошибка чтения из файла" + FILE_MIX_TEST_DATA);
        }
    }

    private void remove(String[] records, int index) {
        if (index == records.length - 1) {
            switch (records[index].trim().toLowerCase()) {
  /*          case "task": {
                for (int i = index + 1; i < records.length; i++) {
                    if (records[i].isBlank()) continue;
                    taskManager.removeTask(records[i].trim());
                }
                break;
            }
            case "epic": {
                for (int i = index + 1; i < records.length; i++) {
                    if (records[i].isBlank()) continue;
                    taskManager.removeEpic(records[i].trim());
                }
                break;
            }
            case "subtask": {
                for (int i = index + 1; i < records.length; i++) {
                    if (records[i].isBlank()) continue;
                    taskManager.removeSubtask(records[i].trim());
                }
                break;
            }*/
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
                case "subtask":
                    taskManager.removeSubtask(records[i].trim());
                    break;
                default:
                    break;
            }
        }


    }

    private void update(String[] records, int index) {
        String[] dataType = records[index].split("=");
        TaskType type = TaskType.valueOf(dataType[1].trim().toUpperCase());
        String[] dataId = records[index + 1].split("=");
        String id = dataId[1].trim();

        switch (type) {
            case TASK: {
                updateData(records, taskManager.getTask(id), index + 2);
                break;
            }
            case EPIC: {
                updateData(records, taskManager.getEpic(id), index + 2);
                break;
            }
            case SUB_TASK: {
                updateData(records, taskManager.getSubtask(id), index + 2);
                break;
            }
        }
    }

    private boolean validateMixLine(String[] records) {
        Set<String> firstRow = Set.of("add", "upd", "del");
        Set<String> removeRow = Set.of("task", "epic", "subtask", "allepic", "all", "alltask", "allsubtask");

        boolean isFirstCorrect = firstRow.contains(records[0].trim().toLowerCase());
        if (!isFirstCorrect) return false;

        switch (records[0].trim().toLowerCase()) {
            case "add": {
                return records[1].trim().toLowerCase().startsWith("type=") && records.length > 2;
            }
            case "upd": {
                return records[1].toLowerCase().trim().startsWith("type=") &&
                        records[2].toLowerCase().trim().startsWith("id=") &&
                        records.length > 3;
            }
            case "del": {
                return removeRow.contains(records[1].trim().toLowerCase());
            }
            default:
                return false;
        }
    }
}
