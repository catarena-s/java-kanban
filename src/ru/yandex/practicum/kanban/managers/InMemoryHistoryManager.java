package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final CustomLinkedList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new CustomLinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
    public void remove(String id) {
        history.removeTask(id);
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }
}
