package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface TaskManager extends Manager {
/*
У меня сомнения в целесообразности наличия 3-х отдельных методов для добавления задач.
По-моему, это увеличивает шансы на ошибку пользователя.
Например, в addTask передать эпик, или в addEpic - передать сабтаск.

Мне кажется, было бы лучше иметь один метод add(Task task), а уже внутри, по переданному объекту определять,
кого добавляем и вызывать соответствующий метод(addTask, addEpic, addSubtask - которые будут скрыты от пользователя).
Но, из-за того, что в ТЗ каждый раз указывают на необходимость иметь отдельные методы, они ещё есть.

Насколько важно пользователю видеть эти отдельные методы? Или всё же один универсальный метод не гуд?
*/
    void addTask(Task task) throws TaskAddException;

    void addEpic(Epic task) throws TaskAddException;

    void addSubtask(SubTask task) throws TaskGetterException, TaskAddException;
    Task clone(Task task) throws TaskGetterException, TaskAddException;

    void clear();

    void removeAllTasks() throws TaskGetterException;

    void removeAllEpics() throws TaskGetterException;

    void removeAllSubtasks() throws TaskGetterException;

    void removeTask(String taskID) throws TaskGetterException, TaskRemoveException;

    void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException;

    void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException;

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
