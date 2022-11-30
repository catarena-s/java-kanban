package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.test.Test;

import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Managers<TaskManager> managers = new Managers<>(new InMemoryTaskManager());
        TaskManager taskManager = managers.getDefault();

        Test tester = managers.getTester();
        if (tester == null) return;

        UserMenu.setTester(tester);
        int answer;
        Scanner scanner = new Scanner(System.in);
        do {
            UserMenu.printMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0)
                UserMenu.run(answer, taskManager);

        } while (answer > 0);
    }

    private static void testGetTasksByID(TaskManager taskManager) throws TaskGetterException {
        String currentID = "0001";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "задачу ", currentID);
        Printer.printTaskByID(currentID, taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "подзадачу ", currentID);
        Printer.printTaskByID(currentID, taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0015";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "задачу ", currentID);
        Printer.printTaskByID(currentID, taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_GET_TASK_BY_ID, "подзадачи эпика", currentID);
        Epic epic = (Epic) taskManager.getEpic(currentID);
        Printer.printHistory(taskManager);
        List<SubTask> allSubtaskByEpic = taskManager.getAllSubtaskByEpic(epic);
        if (!allSubtaskByEpic.isEmpty()) {
            allSubtaskByEpic.forEach(t -> Helper.printMessage("%s\n", t));
        }
        Printer.printHistory(taskManager);
        Helper.printEmptySting();
    }

    private static void testUpdateTasks(TaskManager taskManager) throws TaskGetterException {
        String currentID = "0001";
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
        currentID = "0006";
        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description +++++++++++++++" + currentID);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask);
        subTask = (SubTask) taskManager.getSubtask(currentID);

//        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);
//        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, epic);

        Printer.printHistory(taskManager);
        Helper.printEmptySting();/**/
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

    private static void testRemoveTasks(TaskManager taskManager) throws TaskGetterException {
        String currentID = "0003";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        Printer.printTaskByID(currentID, taskManager);
        taskManager.removeTask(currentID);
        Printer.printAllTaskManagerList(taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        Printer.printTaskByID(currentID, taskManager);
        taskManager.removeSubtask(currentID);
        Printer.printAllTaskManagerList(taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0004";
        Helper.printMessage(Helper.MSG_DELETE_BY_ID, currentID);
        Printer.printTaskByID(currentID, taskManager);
        taskManager.removeEpic(currentID);
        Printer.printAllTaskManagerList(taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();

        //добавили эпиков
        Helper.printMessage("Добавили эпики\n");
        Epic epic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 4", "Описание эпика 4");
        taskManager.addEpic(epic);
        epic = new Epic("Эпик 5", "Описание эпика 5");
        taskManager.addEpic(epic);
        Printer.printAllTaskManagerList(taskManager);
        Printer.printHistory(taskManager);

        Helper.printEmptySting();
        Helper.printMessage(">>Удаляем все %s \n", TaskType.EPIC);
        taskManager.removeAllEpics();
        Printer.printAllTaskManagerList(taskManager);
        Printer.printHistory(taskManager);
        Helper.printEmptySting();
///----->>Удаляем все задач------------------------------------------
        Helper.printMessage(">>Удаляем все задачи\n");
        taskManager.clear();
        Helper.printMessage("Все задачи удалены!\n");
        Printer.printHistory(taskManager);
        Helper.printEmptySting();
        Helper.printEmptySting();
    }

    private static void initTaskManager(TaskManager taskManager) throws TaskGetterException {
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
        taskManager.clone(subTask);

        taskManager.addSubtask(subTask);
        taskManager.addSubtask(subTask);
        taskManager.clone(subTask);
        taskManager.clone(subTask);

        epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic);
    }
}
