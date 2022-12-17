package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    public static final String ERROR_MSG_HISTORY_IS_NOT_EMPTY = "История не пустая.";
    static HistoryManager historyManager;
    static TaskManager taskManager;

    @BeforeEach
    void init() {
//        Managers managers = new Managers(1);
//        taskManager = managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    /*- [ ]  a. Пустая история задач.
    - [ ]  b. Дублирование.
    - [ ]  с. Удаление из истории: начало, середина, конец.*/
    @Test
    void setUp() {
        final Task task = new SimpleTask("Task 1", "Description 1");
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(1, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);

        //
    }

    @Test
    void addDublicate() {
        final Task task = new SimpleTask("Task 1", "Description 1");
        historyManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(1, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);

    }

    @Test
    void removeFirst() {
        final List<Task> history = historyManager.getHistory();
        removeFromHistory(history, 0);
        final List<Task> historyLoad = historyManager.getHistory();
        assertEquals(historyLoad.size(), history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    void removeLast() {
        final List<Task> history = historyManager.getHistory();
        removeFromHistory(history, history.size() - 1);
        final List<Task> historyLoad = historyManager.getHistory();
        assertEquals(historyLoad.size(), history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    void removeMiddle() {
        final List<Task> history = historyManager.getHistory();
        removeFromHistory(history, history.size() / 2);
        final List<Task> historyLoad = historyManager.getHistory();
        assertEquals(historyLoad.size(), history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    private void removeFromHistory(List<Task> history, int index) {
        Task middle = history.get(index);
        history.remove(middle);
    }

    @Test
    void clear() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(0, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }
}