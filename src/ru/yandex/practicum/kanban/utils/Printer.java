package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Printer {
    private Printer() {
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

    public static void printSortedTasks(List<Task> allTasks) {
        allTasks.stream().sorted().forEach(t -> Helper.printMessage(t.toCompactString()));
        Helper.printEmptySting();
    }
}
