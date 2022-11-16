package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface TaskManager extends Manager {
    void addTask(Task task);

    void addEpic(Epic task);

    void addSubtask(SubTask task);

    void clear();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeTask(String taskID);

    void removeEpic(String taskID);

    void removeSubtask(String taskID);

    List<SubTask> getAllSubtaskByEpic(Epic epic);

    List<Task> getAll();

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubTasks();

    Task getById(String taskID);

    Task getTask(String taskID);

    Task getEpic(String taskID);

    Task getSubtask(String taskID);

    void updateTask(Task task);

    List<Task> getHistory();
}
