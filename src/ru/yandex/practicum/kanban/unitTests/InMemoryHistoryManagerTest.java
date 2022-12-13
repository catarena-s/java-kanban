package ru.yandex.practicum.kanban.unitTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    public static final String ERROR_MSG_HISTORY_IS_NOT_EMPTY = "История не пустая.";
    static HistoryManager historyManager;
    static TaskManager taskManager;
    @BeforeAll
    static void init(){
        Managers managers = new Managers(1);
        taskManager = managers.getDefault();
        historyManager = Managers.getDefaultHistory();

    }

    @Test
    void add() throws TaskGetterException, TaskAddException {
        final Task task = new Task("Task 1", "Description 1");
//        taskManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(1, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    void getHistory() {
    }

    @Test
    void remove() {
        //        addTaskToTaskmanager();

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(0, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    void clear() {
        //        addTaskToTaskmanager();
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(0, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }
}