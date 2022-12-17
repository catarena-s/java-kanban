package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskPrinter {
    private TaskPrinter() {
    }
    public static void printHistory(TaskManager taskManager) {
        Helper.printMessage("History: ");
        List<Task> historyManager = taskManager.getHistory();
        List<String> history = new ArrayList<>();
        if (historyManager.isEmpty()) return;
        for (Task task : historyManager) {
            history.add(task.getTaskID());
        }
        Helper.printMessage("(%d) '%s'",historyManager.size(), String.join("' -> '", history));
    }

    public static void printAllTaskManagerList(TaskManager taskManager) {
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
    public static void printAllTaskManagerListLong(TaskManager taskManager) {
        List<Task> allTasks = taskManager.getAllTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasksLong(allTasks);
        }

        allTasks = taskManager.getAllEpics();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasksLong(allTasks);
        }
        allTasks = taskManager.getAllSubTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasksLong(allTasks);
        }
    }
    public static void printSortedTasks(List<Task> allTasks) {
        allTasks.stream().sorted().forEach(t -> Helper.printMessage(t.toCompactString()));
        Helper.printEmptySting();
    }
    public static void printList(List<Task> allTasks) {
        allTasks.stream().forEach(t -> Helper.printMessage(t.toCompactString()));
        Helper.printEmptySting();
    }
    public static void printSortedTasksLong(List<Task> allTasks) {
        allTasks.stream().sorted().forEach(t -> Helper.printMessage(t.toString()));
        Helper.printEmptySting();
    }

    public static void printEpicInfo(Epic epic) {
        Helper.printMessage(epic.toCompactString());
        List<Task> allTasks = epic.getSubTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            printSortedTasks(allTasks);
        }
    }
}
