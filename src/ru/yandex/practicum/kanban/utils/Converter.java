package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Converter {
    private Converter() {
    }

    /**
     * Создаем задачу из строки
     */
    public static Task fromString(final String value) throws TaskException {
        return createNewTask(parseString(value));
    }


    /**
     * Формируем стороку по истории
     */
    public static String historyToString(final HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getTaskID).collect(Collectors.joining(" "));
    }

    /**
     * Список истории по строке
     */
    public static List<String> historyFromString(final String value) {
        return Arrays.asList(value.split(" "));
    }

    /**
     * Формируем строку из списка задач
     */
    public static String taskListToString(final List<Task> taskList) {
        final String result = taskList.stream().sorted()
                .map(Task::toCompactString)
                .collect(Collectors.joining(System.lineSeparator()));

        return result.isBlank() ? "" : result + System.lineSeparator();
    }

    /**
     * создаем задачу
     *
     * @param newData - данные полученные из строкик
     * @return задача
     * @throws TaskException
     */
    public static Task createNewTask(final Record newData) throws TaskException {
        final TaskType type = newData.type;
        final Optional<Task> task = Optional.ofNullable(type.create());
        task.ifPresent(t -> {
            t.init(newData.id, newData.name, newData.description);
            if (t instanceof SubTask) {
                ((SubTask) t).builder().epic(newData.epicID);
            }
            if (t instanceof Updatable) {
                if (!newData.duration.isBlank())
                    ((Updatable) t).updateDuration(Integer.parseInt(newData.duration));
                if (!newData.status.isBlank())
                    ((Updatable) t).updateStatus(TaskStatus.valueOf(newData.status));
                if (!newData.startTime.isBlank())
                    ((Updatable) t).updateStartTime(newData.startTime);
            }
        });
        return task.orElseThrow(() -> new TaskException("Ошибка создания задачи из строки"));
    }

    private static Record parseString(final String value) {
        final Record newData = new Record();
        final String[] data = value.split(",");

        newData.id = data[1].trim();
        newData.type = TaskType.valueOf(data[0].trim().toUpperCase());
        newData.status = data[2].trim().toUpperCase();
        newData.name = data[3].trim();
        newData.description = data[4].trim();
        newData.duration = data[5].trim();
        newData.startTime = data[6].trim();
        newData.epicID = (data.length == 8) ? data[7].trim() : "";

        return newData;
    }

    /**
     * вспомогательный класс для создания задач из строки
     */
    public static class Record {
        public TaskType type;
        public String id = "";
        public String name = "";
        public String description = "";
        public String epicID = "";
        public String status = "";
        public String duration = "0";
        public String startTime = "01-01-2222 00:00";
    }
}
