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
        printHistory(taskManager);

        Helper.printMessage(Helper.MSG_SEPARATOR);
        testGetTasksByID(taskManager);

        Helper.printMessage(Helper.MSG_SEPARATOR);
        testUpdateTasks(taskManager);

        Helper.printMessage(Helper.MSG_SEPARATOR);
        testRemoveTasks(taskManager);
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
        String currentID = "0002";
        Task task = taskManager.getTask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, task);

        task.setName("newName" + currentID);
        task.setDescription("new Description " + currentID);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task, TaskType.TASK);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, taskManager.getTask(currentID));
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0007";
        Epic updatedEpic = (Epic) taskManager.getEpic(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, updatedEpic);

        updatedEpic.setName("newEpic" + currentID);
        updatedEpic.setDescription("new Description " + currentID);
        /*
         Вопрос: Стоит ли переоперделить setStatus у эпика - запретить смену статуса, если у него нет подзадач?
         Или оставить эту проверку внутири таск-менеджера?
         */
        updatedEpic.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(updatedEpic, TaskType.EPIC);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, taskManager.getEpic(currentID));
        printHistory(taskManager);
        Helper.printEmptySting();

        currentID = "0005";
        SubTask subTask = (SubTask) taskManager.getSubtask(currentID);
        Helper.printMessage(Helper.MSG_UPDATE_TASK_BY_ID, currentID, subTask);

        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask, TaskType.SUB_TASK);
        subTask = (SubTask) taskManager.getSubtask(currentID);

        Helper.printMessage(Helper.MSG_TEMPLATE_TASK_PRINT, subTask);

        Epic epic = (Epic) taskManager.getEpic(subTask.getEpicID());
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
        taskManager.add(task, TaskType.TASK);
        Helper.printMessage(Helper.MSG_ADD_TASK, task.getTaskID(), task.getName());
        printHistory(taskManager);

        task = new Task("Задача 2", "Описание задачи 2");
        taskManager.add(task, TaskType.TASK);
        Helper.printMessage(Helper.MSG_ADD_TASK, task.getTaskID(), task.getName());
        printHistory(taskManager);
        task = new Task("Задача 3", "Описание задачи 3");
        taskManager.add(task, TaskType.TASK);
        Helper.printMessage(Helper.MSG_ADD_TASK, task.getTaskID(), task.getName());
        printHistory(taskManager);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.add(epic, TaskType.EPIC);
        Helper.printMessage(Helper.MSG_ADD_TASK, epic.getTaskID(), epic.getName());
        printHistory(taskManager);
        SubTask subTask = new SubTask("Подзадача 1-1", "Описание подзадачи 1-1", epic.getTaskID());
        taskManager.add(subTask, TaskType.SUB_TASK);
        Helper.printMessage(Helper.MSG_ADD_TASK, subTask.getTaskID(), subTask.getName());
        printHistory(taskManager);

        subTask = new SubTask("Подзадача 1-2", "Описание подзадачи 1-2", epic.getTaskID());
        taskManager.add(subTask, TaskType.SUB_TASK);
        Helper.printMessage(Helper.MSG_ADD_TASK, subTask.getTaskID(), subTask.getName());
        printHistory(taskManager);

        epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.add(epic, TaskType.EPIC);
        Helper.printMessage(Helper.MSG_ADD_TASK, epic.getTaskID(), epic.getName());
        printHistory(taskManager);

        Helper.printMessage(">>Заполнили TaskManager тестовыми данными\n");
    }
}
