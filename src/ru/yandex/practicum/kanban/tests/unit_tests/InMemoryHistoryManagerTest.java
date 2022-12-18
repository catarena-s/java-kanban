package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.HistoryManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest implements TestLogger{
    public static final String ERROR_MSG_HISTORY_IS_NOT_EMPTY = "История не пустая.";
    HistoryManager historyManager;
    TaskManager taskManager;
    Managers managers;
    @BeforeEach
    void setUp(TestInfo info) throws TaskException {
        managers = new Managers(1);
        taskManager = managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        if(info.getTags().contains("InitData")){
            Task task = new SimpleTask("Task 1", "Description 1");
            taskManager.add(task);
            task = new SimpleTask("Task 2", "Description 1");
            taskManager.add(task);
            task = new SimpleTask("Task 3", "Description 1");

            taskManager.add(task);
            Epic epic = new Epic("Epic 1", "Description epic 1");
            taskManager.add(epic);
            SubTask subTask = new SubTask("SubTask 1", "Description subtask 1",epic.getTaskID());
            taskManager.add(subTask);
        }

    }
    @Test
    @DisplayName("Добавление задачи в историю")
    void addTask() throws TaskException {
        Task task = new SimpleTask("Task 1", "Description 1");
        taskManager.add(task);

        final Epic epic = new Epic("Epic 1", "Description epic 1");
        taskManager.add(epic);
        final SubTask subTask = new SubTask("SubTask 1", "Description subtask 1",epic.getTaskID());
        taskManager.add(subTask);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(3, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);

    }
    @Test
    @Tag(value = "InitData")
    @DisplayName("Дабавление дублика та в историю")
    void addDuplicate() throws TaskException {
        final Task task = new SimpleTask("Task 1", "Description 1");
        taskManager.add(task);
        int sizeBefore = historyManager.getHistory().size();
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(sizeBefore, history.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);

    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление 1й задачи из истории")
    void removeFirst() {
        final List<Task> history = historyManager.getHistory();
        Task first = history.get(0);
        int sizeBefore = history.size();
        historyManager.remove(first.getTaskID());
        final List<Task> historyLoad = historyManager.getHistory();
        int sizeAfter = historyLoad.size();
        assertEquals(sizeBefore-1, sizeAfter, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление последней задачи из истории")
    void removeLast() {
        final List<Task> history = historyManager.getHistory();
        Task last = history.get(history.size()-1);
        int sizeBefore = history.size();
        historyManager.remove(last.getTaskID());
        final List<Task> historyLoad = historyManager.getHistory();
        int sizeAfter = historyLoad.size();
        assertEquals(sizeBefore-1, sizeAfter, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление задачи из середины")
    void removeMiddle() {
        final List<Task> history = historyManager.getHistory();
        Task midle = history.get(history.size() / 2);
        int sizeBefore = history.size();
        historyManager.remove(midle.getTaskID());
        final List<Task> historyLoad = historyManager.getHistory();
        int sizeAfter = historyLoad.size();
        assertEquals(sizeBefore-1, sizeAfter, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Очистить историю")
    void clear() {
        historyManager.clear();
        final List<Task> historyAfter = historyManager.getHistory();
        assertNotNull(historyAfter, ERROR_MSG_HISTORY_IS_NOT_EMPTY);
        assertEquals(0, historyAfter.size(), ERROR_MSG_HISTORY_IS_NOT_EMPTY);
    }
}