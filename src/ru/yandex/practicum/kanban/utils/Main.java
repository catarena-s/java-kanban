package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Managers<TaskManager> managers = new Managers<>(new InMemoryTaskManager());
        TaskManager taskManager = managers.getDefault();

///-----добавляем задачи в менеджер
        initTaskManager(taskManager);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        Helper.printMessage("-- GET " + Helper.MSG_SEPARATOR);
        testGetTasksByID(taskManager);

        Helper.printMessage("REMOVE " + Helper.MSG_SEPARATOR);
        testRemoveTasks(taskManager);
    }

    public static void printHistory(TaskManager taskManager) {
        Helper.printMessage("History: ");
        List<Task> historyManager = taskManager.getHistory();
        List<String> history = new ArrayList<>();
        Helper.printMessage("(%d)", historyManager.size());
        if (historyManager.isEmpty()) return;
        for (Task task : historyManager) {
            history.add(task.getTaskID());
        }
        Helper.printMessage(" '%s'\n", String.join("' -> '", history));
    }

    private static void testGetTasksByID(TaskManager taskManager) {
        String currentID = "0001";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "задачу ", currentID);
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "подзадачу ", currentID);
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0015";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "задачу ", currentID);
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "подзадачи эпика", currentID);
        Epic epic = (Epic) taskManager.getEpic(currentID);
        printHistory(taskManager);
        List<SubTask> allSubtaskByEpic = taskManager.getAllSubtaskByEpic(epic);
        if (!allSubtaskByEpic.isEmpty()) {
            allSubtaskByEpic.forEach(t -> Helper.printMessage("%s\n", t));
        }
        printHistory(taskManager);
        Helper.printEmptySting();
    }

    private static void testUpdateTasks(TaskManager taskManager) {
        String currentID = "0004";
        Epic epic = (Epic) taskManager.getEpic(currentID);
        epic.setName("New nape 004");
        taskManager.updateTask(epic);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();
//------Обновляем подзадачу
        currentID = "0005";
        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask);
        subTask = (SubTask) taskManager.getSubtask(currentID);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);

        printHistory(taskManager);
        Helper.printEmptySting();
//------Обновляем подзадачу
        currentID = "0006";
        subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask);
        subTask = (SubTask) taskManager.getSubtask(currentID);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);

        printHistory(taskManager);
        Helper.printEmptySting();
//------Удаляем подзадачу
        currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        taskManager.removeSubtask(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();

//------Добавляем подзадачу
        subTask = new SubTask("Подзадача 1-3", "Описание подзадачи 1-3", subTask.getEpicID());
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
        Helper.printEmptySting();
    }

    private static void testRemoveTasks(TaskManager taskManager) {
        String currentID = "0003";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        printTaskByID(currentID, taskManager);
        taskManager.removeTask(currentID);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        printTaskByID(currentID, taskManager);
        taskManager.removeSubtask(currentID);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        printTaskByID(currentID, taskManager);
        taskManager.removeEpic(currentID);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        //добавили эпиков
        Helper.printMessage("Добавили эпики\n");
        Epic epic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 4", "Описание эпика 4");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 5", "Описание эпика 5");
        taskManager.addEpic(epic);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);

        Helper.printEmptySting();
        Helper.printMessage(">>Удаляем все %s \n", TaskType.EPIC);
        taskManager.removeAllEpics();
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();
///----->>Удаляем все задач------------------------------------------
        Helper.printMessage(">>Удаляем все задачи\n");
        taskManager.clear();
        Helper.printMessage("Все задачи удалены!\n");
        printHistory(taskManager);
        Helper.printEmptySting();
        Helper.printEmptySting();
    }

    private static void printTaskByID(String currentID, TaskManager taskManager) {
        Task task = taskManager.getById(currentID);
        if (task == null) {
            Helper.printMessage(Helper.MSG_TASK_WITH_ID_NOT_EXIST, currentID);
        } else {
            Helper.printMessage("%s\n", task);
        }
    }

    private static void printAllTaskManagerList(TaskManager taskManager) {
        List<Task> allTasks = taskManager.getAllTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasks(allTasks);
        }

        allTasks = taskManager.getAllEpics();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasks(allTasks);
        }
        allTasks = taskManager.getAllSubTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasks(allTasks);
        }
    }

    private static void printSortedTasks(List<Task> allTasks) {
        Collections.sort(allTasks, (t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTaskID(), t2.getTaskID()));
        allTasks.forEach(t -> Helper.printMessage("%s ", t.toStringShort()));
        Helper.printEmptySting();
    }

    private static void initTaskManager(TaskManager taskManager) {
        Task task = new Task("Задача 1", "Описание задачи 1");
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
        taskManager.addSubtask(subTask);

        epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic);
    }
}
