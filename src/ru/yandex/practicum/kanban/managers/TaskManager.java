package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;

import java.util.List;

public interface TaskManager extends Manager {

    void add(Task task) throws TaskException;
    Task clone(Task task) throws TaskException;

    void clear();

    void removeAllTasks() throws TaskGetterException;

    void removeAllEpics() throws TaskGetterException;

    void removeAllSubtasks() throws TaskGetterException;

    void removeTask(String taskID) throws TaskGetterException, TaskRemoveException;

    void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException;

    void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException;

    List<Task> getAllSubtaskFromEpic(Epic epic);

    List<Task> getAll();

    List<Task> getAllTasks();

    List<Task> getAllEpics();

    List<Task> getAllSubTasks();
    List<Task> getPrioritizedTasks();

    Task getById(String taskID) throws TaskGetterException;

    Task getTask(String taskID) throws TaskGetterException;

    Task getEpic(String taskID) throws TaskGetterException;

    Task getSubtask(String taskID) throws TaskGetterException;

    void updateTask(Task task) throws TaskException;

    List<Task> getHistory();
}
