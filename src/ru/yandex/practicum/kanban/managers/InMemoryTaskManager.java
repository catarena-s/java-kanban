package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<TaskType, Map<String, Task>> tasksByType;
    protected final HistoryManager historyManager;
    private int lastID = 0;
    private final List<String> allId = new ArrayList<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasksByType = new EnumMap<>(TaskType.class);
        this.historyManager = historyManager;
    }

    @Override
    public void add(Task task) throws TaskGetterException, TaskAddException {
        if (task instanceof SubTask) {
            addSubtask((SubTask) task);
        } else if (task instanceof Epic) {
            addEpic((Epic) task);
        } else {
            addTask(task);
        }
    }

    private void addTask(Task task) throws TaskAddException, TaskGetterException {
        add(task, TaskType.TASK);
    }

    private void addEpic(Epic task) throws TaskAddException, TaskGetterException {
        add(task, TaskType.EPIC);
    }

    private void addSubtask(SubTask task) throws TaskGetterException, TaskAddException {
        if (task.getEpicID().isBlank()) {
            throw new TaskAddException("Нельзя добавить subtask - не указан id эпика.");
        }
        Epic epic = (Epic) getEpic(task.getEpicID());
        if (epic == null) {
            throw new TaskAddException("Нельзя добавить subtask - Эпик с id=%s отсутвует", task.getEpicID());
        }
        add(task, TaskType.SUB_TASK);
    }

    private void add(Task task, TaskType type) throws TaskAddException, TaskGetterException {
        if (!task.getTaskID().isBlank() && allId.contains(task.getTaskID())) {
            throw new TaskAddException("%s с id=%s уже существует.", type.getValue(), task.getTaskID());
        }

        addTaskToTaskManager(task);
        if (TaskType.SUB_TASK.equals(task.getType())) {
            subtaskToEpic((SubTask) task);
        }
        historyManager.add(task);
    }

    /**
     * добавляем задачу в таск-менеожер
     */
    protected void addTaskToTaskManager(Task task) {
        TaskType taskType = task.getType();
        Map<String, Task> tasks = tasksByType.getOrDefault(taskType, new HashMap<>());
        if (task.getTaskID().isBlank()) {
            task.builder().taskId(newTaskID());
        } else {
            int current = Integer.parseInt(task.getTaskID());
            if (lastID < current) lastID = current;
        }
        tasks.put(task.getTaskID(), task);
        tasksByType.put(taskType, tasks);
        allId.add(task.getTaskID());
    }

    protected void subtaskToEpic(SubTask task) throws TaskGetterException {
        Epic epic = (Epic) getEpic(task.getEpicID());
        epic.addSubtask(task);
        updateEpicStatus(epic);
    }

    /**
     * Генерация нового Id
     */
    private String newTaskID() {
        return String.format("%04d", ++lastID);
    }

    @Override
    public Task clone(Task task) throws TaskGetterException, TaskAddException {
        Task newTask;
        if (task instanceof SubTask) {
            newTask = new SubTask(task.getName(), task.getDescription(), ((SubTask) task).getEpicID());
        } else if (task instanceof Epic) {
            newTask = new Epic(task.getName(), task.getDescription());
        } else {
            newTask = new Task(task.getName(), task.getDescription());
        }
        add(newTask);
        if (task instanceof Epic) {
            for (Task subTask : ((Epic) task).getSubTasks()) {
                SubTask newSubtask = new SubTask(subTask.getName(), subTask.getDescription(), newTask.getTaskID());
                add(newSubtask);
            }
        }
        return newTask;
    }

    @Override
    public List<Task> getAllSubtaskByEpic(Epic epic) {
        List<Task> subTasks = epic.getSubTasks();
        subTasks.forEach(historyManager::add);
        return subTasks;
    }

    @Override
    public List<Task> getAll() {
        List<Task> allTasks = new ArrayList<>();
        if (tasksByType.get(TaskType.TASK) != null) {
            allTasks.addAll(tasksByType.get(TaskType.TASK).values());
        }
        if (tasksByType.get(TaskType.EPIC) != null) {
            for (Task epic : tasksByType.get(TaskType.EPIC).values()) {
                allTasks.add(epic);
                allTasks.addAll(((Epic) epic).getSubTasks());
            }
        }
        return allTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        return getAllByType(TaskType.TASK);
    }

    @Override
    public List<Task> getAllEpics() {
        return getAllByType(TaskType.EPIC);
    }

    @Override
    public List<Task> getAllSubTasks() {
        return getAllByType(TaskType.SUB_TASK);
    }

    @Override
    public Task getById(String taskID) throws TaskGetterException {
        for (Map.Entry<TaskType, Map<String, Task>> map : tasksByType.entrySet()) {
            if (map.getValue().containsKey(taskID)) {
                Task task = map.getValue().get(taskID);
                historyManager.add(task);
                return task;
            }
        }
        throw new TaskGetterException("Задача с id=%s не найдена.", taskID);
    }

    @Override
    public Task getTask(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.TASK);
    }

    @Override
    public Task getEpic(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.EPIC);
    }

    @Override
    public Task getSubtask(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.SUB_TASK);
    }

    protected List<Task> getAllByType(TaskType taskType) {
        if (tasksByType.get(taskType) == null ||
                tasksByType.get(taskType).isEmpty()) return new ArrayList<>();
        return new ArrayList<>(tasksByType.get(taskType).values());
    }

    @Override
    public void updateTask(Task task) throws TaskGetterException {
        if (task == null) return;
        Map<String, Task> taskByType;
        if (task instanceof SubTask) {
            taskByType = tasksByType.get(TaskType.SUB_TASK);
            Epic epic = (Epic) getEpic(((SubTask) task).getEpicID());
            updateEpicStatus(epic);
            epic.check();
        } else if (task instanceof Epic) {
            taskByType = tasksByType.get(TaskType.EPIC);
            updateEpicStatus((Epic) task);
        } else {
            taskByType = tasksByType.get(TaskType.TASK);
        }
        taskByType.put(task.getTaskID(), task);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void clear() {
        tasksByType.clear();
        historyManager.clear();
        lastID = 0;
    }

    @Override
    public void removeAllEpics() throws TaskGetterException {
        removeAllTasks(TaskType.EPIC);
    }

    @Override
    public void removeAllTasks() throws TaskGetterException {
        removeAllTasks(TaskType.TASK);
    }

    @Override
    public void removeAllSubtasks() throws TaskGetterException {
        removeAllTasks(TaskType.SUB_TASK);
    }

    @Override
    public void removeTask(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.TASK);
    }

    @Override
    public void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.EPIC);
    }

    @Override
    public void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.SUB_TASK);
    }

    private void removeAllTasks(TaskType taskType) throws TaskGetterException {
        if (!tasksByType.containsKey(taskType)) return;

        if (taskType == TaskType.SUB_TASK) {
            Map<String, Task> subTasks = tasksByType.get(taskType);
            for (Task subTask : subTasks.values()) {
                Epic epic = (Epic) getEpic(((SubTask) subTask).getEpicID());
                epic.getSubTasks().remove(subTask);
                historyManager.remove(subTask.getTaskID());
            }
        }
        if (taskType == TaskType.EPIC) {
            Map<String, Task> epics = tasksByType.get(taskType);
            if (epics.isEmpty()) return;
            for (Task epic : epics.values()) {
                removeAllSubtasksForEpic((Epic) epic);
                historyManager.remove(epic.getTaskID());

            }
        }
        tasksByType.remove(taskType);
    }

    private void removeAnyById(String taskID) throws TaskGetterException {
        for (Map.Entry<TaskType, Map<String, Task>> entry : tasksByType.entrySet()) {
            Map<String, Task> tasks = entry.getValue();
            if (tasks.isEmpty()) continue;
            if (tasks.containsKey(taskID)) {
                remove(taskID, entry.getKey(), tasks);
                return;
            }
        }
    }

    private void remove(String taskID, TaskType taskType) throws TaskRemoveException, TaskGetterException {
        Map<String, Task> tasks = tasksByType.get(taskType);
        if (tasks == null || !tasks.containsKey(taskID))
            throw new TaskRemoveException("%s c id=%s не найден\n", taskType.getValue(), taskID);

        remove(taskID, taskType, tasks);
    }

    private void remove(String taskID, TaskType taskType, Map<String, Task> tasks) throws TaskGetterException {
        if (taskType == TaskType.EPIC) {
            Epic epic = (Epic) tasks.get(taskID);
            removeAllSubtasksForEpic(epic);
        }
        if (taskType == TaskType.SUB_TASK) {
            SubTask subTask = (SubTask) tasks.get(taskID);
            Epic epic = (Epic) getEpic(subTask.getEpicID());
            epic.getSubTasks().remove(subTask);
            updateEpicStatus(epic);
        }

        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    private void removeAllSubtasksForEpic(Epic epic) {
        Map<String, Task> subtasks = tasksByType.get(TaskType.SUB_TASK);

        for (Task subTask : epic.getSubTasks()) {
            historyManager.remove(subTask.getTaskID());
            subtasks.remove(subTask.getTaskID());
        }
        epic.getSubTasks().clear();
    }

    /**
     * Обновление статуса эпика
     */
    private void updateEpicStatus(Epic epic) {
        ArrayList<Task> allSubTasks = new ArrayList<>(epic.getSubTasks());

        if (allSubTasks.isEmpty()) {
            epic.builder().status(TaskStatus.NEW);
//            Helper.printMessage(Helper.EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE);
            return;
        }

        boolean isDone = true;
        boolean isNew = true;
        for (Task subTask : allSubTasks) {
            TaskStatus currentStatus = subTask.getStatus();

            isDone &= (currentStatus == TaskStatus.DONE);
            isNew &= (currentStatus == TaskStatus.NEW);
            boolean isInProgress = (!isDone && !isNew) || (currentStatus == TaskStatus.IN_PROGRESS);

            if (isInProgress) {
                epic.builder().status(TaskStatus.IN_PROGRESS);
                return;
            }
        }
        if (isNew) {
            epic.builder().status(TaskStatus.NEW);
        } else if (isDone) {
            epic.builder().status(TaskStatus.DONE);
        } else {
            epic.builder().status(TaskStatus.IN_PROGRESS);
        }
    }

    private Task getByIdAndType(String taskID, TaskType type) throws TaskGetterException {
        Map<String, Task> tasks = tasksByType.get(type);
        if (tasks != null && tasks.containsKey(taskID)) {
            Task task = tasks.get(taskID);
            historyManager.add(task);
            return task;
        }
        if(tasks == null || tasks.isEmpty()) throw new TaskGetterException("%s - отсутствуют ",type.getValue());
        throw new TaskGetterException("%s c id =%s не найдена ", type.getValue(), taskID);
    }
}
