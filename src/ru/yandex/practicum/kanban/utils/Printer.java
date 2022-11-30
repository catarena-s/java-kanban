package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Printer {
    private Printer(){}
    public static void printHistory(TaskManager taskManager) {
        Helper.printMessage("History: ");
        List<Task> historyManager = taskManager.getHistory();
        List<String> history = new ArrayList<>();
        Helper.printMessage("(%d)", historyManager.size());
        if (historyManager.isEmpty()) return;
        for (Task task : historyManager) {
            history.add(task.getTaskID());
        }
        Helper.printMessage(" '%s'%n", String.join("' -> '", history));
    }

    static void printTaskByID(String currentID, TaskManager taskManager) {
        Task task = taskManager.getById(currentID);
        if (task == null) {
            Helper.printMessage(Helper.MSG_TASK_WITH_ID_NOT_EXIST, currentID);
        } else {
            Helper.printMessage("%s%n", task);
        }
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

    public static void printSortedTasks(List<Task> allTasks) {
        allTasks.sort((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTaskID(), t2.getTaskID()));
        allTasks.forEach(t -> Helper.printMessage(t.toCompactString()));
        Helper.printEmptySting();
    }
}
