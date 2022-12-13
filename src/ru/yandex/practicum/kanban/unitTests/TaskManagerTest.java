package ru.yandex.practicum.kanban.unitTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.test.TestManager;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.test.Tester;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {

    public static final String ERROR_MSG_TASK_NOT_FOUND = "Задача не найдена.";
    public static final String ERROR_MSG_TASK_NOT_EQUALS = "Задачи не совпадают.";
    static TaskManager taskManager;

    static void init(int config) {
        Managers managers = new Managers(config);
        taskManager = managers.getDefault();
    }

    @Test
    void addTask() throws TaskGetterException, TaskAddException {
        final Task task = new Task("Task 1", "Description 1");
        taskManager.add(task);
        final Task savedTask = taskManager.getTask(task.getTaskID());
        assertNotNull(savedTask, ERROR_MSG_TASK_NOT_FOUND);
        assertEquals(task, savedTask, ERROR_MSG_TASK_NOT_EQUALS);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), ERROR_MSG_TASK_NOT_EQUALS);
    }

    @Test
    void addEpic() throws TaskGetterException, TaskAddException {
        Task task = new Epic("Epic 1", "Description epic 1");
        taskManager.add(task);
        Task receivedTask = taskManager.getEpic(task.getTaskID());
        assertEquals(task, receivedTask);
    }

    @Test
    void testClone() {
    }

    @Test
    void getAllSubtaskByEpic() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void getById() {
    }



    @Test
    void updateEpic() throws IOException, TaskGetterException {

    }



    @Test
    void removeTask() {
    }

    @Test
    void removeEpic() {
    }
}