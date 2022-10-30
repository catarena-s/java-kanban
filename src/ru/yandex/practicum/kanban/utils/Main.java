package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Managers<TaskManager> managers = new Managers<>(new InMemoryTaskManager());
        TaskManager taskManager = managers.getDefault();

        initTaskManager(taskManager);
        printTaskByID("0004",taskManager);
        printTaskByID("0005",taskManager);
        printTaskByID("0006",taskManager);

        Helper.printMessage(Helper.MSG_SEPARATOR);
        testUpdateTasks(taskManager);

    }

    public static void printHistory(TaskManager taskManager) {
        Helper.printMessage("History: ");
        HistoryManager historyManager = taskManager.getHistoryManager();
        List<String> history = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            history.add(task.getTaskID());
        }
        Helper.printMessage("(%d) '%s'\n", history.size(), String.join("' -> '", history));
    }

    private static void testGetTasksByID(TaskManager taskManager) {
        String currentID;
        List<Task> allTasks;

        currentID = "0001";
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0003";
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0015";
        printTaskByID(currentID, taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "подзадачи эпика", currentID);
        Epic epic = (Epic) taskManager.getEpic(currentID);
        allTasks = taskManager.getAllSubtaskByEpic(epic);
        if (!allTasks.isEmpty()) {
            allTasks.forEach(t -> Helper.printMessage("%s\n", t));
        }
        printHistory(taskManager);
        Helper.printEmptySting();
    }

    private static void testUpdateTasks(TaskManager taskManager) {
        String currentID = "0004";
        Epic epic = (Epic) taskManager.getEpic(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        Helper.printEmptySting();
//------Обзовляем подзадачу
        currentID = "0005";
        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask, TaskType.SUB_TASK);
        subTask = (SubTask) taskManager.getSubtask(currentID);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        Helper.printEmptySting();
//------Обзовляем подзадачу
        currentID = "0006";
        subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask, TaskType.SUB_TASK);
        subTask = (SubTask) taskManager.getSubtask(currentID);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        Helper.printEmptySting();
//------Удаляем подзадачу
        currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        taskManager.removeTaskByID(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();

//------Добавляем подзадачу
        subTask = new SubTask("Подзадача 1-3", "Описание подзадачи 1-3",subTask.getEpicID() );
        taskManager.addSubtask(subTask);
        Helper.printMessage(Helper.MSG_ADD_TASK,subTask.getTaskID(),subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();
//------Удаляем подзадачу
        currentID = "0006";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        taskManager.removeTaskByID(currentID);
        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);
        printHistory(taskManager);
        Helper.printEmptySting();
    }

    private static void testRemoveTasks(TaskManager taskManager) {
        String currentID = "0004";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        taskManager.removeTaskByID(currentID);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0008";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        taskManager.removeTaskByID(currentID, TaskType.SUB_TASK);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        Helper.printMessage(">>Удаляем все %s \n", TaskType.EPIC);
        taskManager.removeAllTasks(TaskType.EPIC);
        printAllTaskManagerList(taskManager);
        printHistory(taskManager);
        Helper.printEmptySting();

        Helper.printMessage(">>Удаляем все задачи\n");
        taskManager.removeAllTasks();
        Helper.printMessage("Все задачи удалены!\n");
    }

    private static void printTaskByID(String currentID, TaskManager taskManager) {
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "задачу", currentID);
        Task task = taskManager.getById(currentID);
        if (task == null) {
            Helper.printMessage(Helper.MSG_TASK_WITH_ID_NOT_EXIST, currentID);
        } else {
            Helper.printMessage("%s\n", task);
        }
    }

    private static void printAllTaskManagerList(TaskManager taskManager) {
        List<Task> allTasks = taskManager.getAllTasks();
        allTasks.forEach(t -> Helper.printMessage("%s\n", t));
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

        epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic);
    }
}
