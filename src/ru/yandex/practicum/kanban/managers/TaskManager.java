package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface TaskManager extends Manager {
    void addTask(Task task);

    void addEpic(Epic task);

    void addSubtask(SubTask task) throws TaskGetterException;
    void clone(Task task) throws TaskGetterException;

    void clear();

    void removeAllTasks() throws TaskGetterException;

    void removeAllEpics() throws TaskGetterException;

    void removeAllSubtasks() throws TaskGetterException;

    void removeTask(String taskID) throws TaskGetterException;

    void removeEpic(String taskID) throws TaskGetterException;

    void removeSubtask(String taskID) throws TaskGetterException;

    List<SubTask> getAllSubtaskByEpic(Epic epic);

    List<Task> getAll();

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubTasks();

    Task getById(String taskID) throws TaskGetterException;

    Task getTask(String taskID) throws TaskGetterException;

    Task getEpic(String taskID) throws TaskGetterException;

    Task getSubtask(String taskID) throws TaskGetterException;

    void updateTask(Task task) throws TaskGetterException;

    List<Task> getHistory();
}
