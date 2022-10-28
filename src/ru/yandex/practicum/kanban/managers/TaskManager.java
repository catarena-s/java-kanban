package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;

import java.util.List;

public interface TaskManager {
    void add(Task task, TaskType type);

    void removeAllTasks();

    void removeAllTasks(TaskType taskType);

    void removeTaskByID(String taskID);

    void removeTaskByID(String taskID, TaskType taskType);

    List<Task> getAllSubtaskByEpic(Epic epic);

    List<Task> getAllTasks();

    List<Task> getAllTasks(TaskType taskType);

    Task getById(String taskID);

    Task getTask(String taskID);

    Task getEpic(String taskID);

    Task getSubtask(String taskID);

    void updateTask(Task task, TaskType taskType);

    HistoryManager getHistoryManager();
}
