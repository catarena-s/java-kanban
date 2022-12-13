package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    private Converter() {
    }

    /**
     * Создаем задачу из строки
     */
    public static Task fromString(String value) {
        String epcId = "";
        String[] data = value.split(",");
        TaskType type = TaskType.valueOf(data[1].trim().toUpperCase());
        String id = data[0].trim();
        TaskStatus status = TaskStatus.valueOf(data[2].trim().toUpperCase());
        String name = data[3].trim();
        String description = data[4].trim();
        if (data.length == 6) epcId = data[5].trim();
        Task task = type.create();
        if (task != null) {
            task.init(id, name, description, epcId);
            task.builder().status(status);
        }
        return task;
    }

    /**
     * Формируем стороку по истории
     */
    public static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getTaskID).collect(Collectors.joining(" "));
    }

    /**
     * Список истории по строке
     */
    public static List<String> historyFromString(String value) {
        return Arrays.asList(value.split(" "));
    }

    /**
     * Формируем строку из списка задач
     */
    public static String taskListToString(List<Task> taskList) {
        return taskList.stream().sorted()
                .map(Task::toCompactString)
                .collect(Collectors.joining());
    }

}
