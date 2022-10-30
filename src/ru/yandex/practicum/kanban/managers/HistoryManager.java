package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager extends Manager {
    List<Task> getHistory();

    void add(Task task);
}
