package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<TaskType, Map<String, Task>> tasksByType;
    private final HistoryManager historyManager;
    private int lastID = 0;
    private final List<String> allId = new ArrayList<>();

    public InMemoryTaskManager() {
        tasksByType = new EnumMap<>(TaskType.class);
        Managers<HistoryManager> managers = new Managers<>(new InMemoryHistoryManager());
        historyManager = managers.getDefault();
    }

    protected Map<TaskType, Map<String, Task>> getTasksByType() {
        return tasksByType;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected void setLastID(int lastID) {
        this.lastID = lastID;
    }

    @Override
    public void addTask(Task task) throws TaskAddException {
        add(task, TaskType.TASK);
    }

    @Override
    public void addEpic(Epic task) throws TaskAddException {
        add(task, TaskType.EPIC);
    }

    @Override
    public void addSubtask(SubTask task) throws TaskGetterException, TaskAddException {
        if (task.getEpicID().isBlank()) {
            Helper.printMessage(">>Ошибка добавления subtask: Эпик отсутвует\n", task.getEpicID());
            return;
        }
        Epic epic = (Epic) getEpic(task.getEpicID());
        if (epic == null) {
            Helper.printMessage(">>Ошибка добавления subtask: Эпик с id=%s отсутвует\n", task.getEpicID());
            return;
        }
        if (add(task, TaskType.SUB_TASK)) {
            epic.addSubtask(task);
            updateEpicStatus(epic);
        }
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
            for (SubTask subTask : ((Epic) task).getSubTasks()) {
                SubTask newSubtask = new SubTask(subTask.getName(), subTask.getDescription(), newTask.getTaskID());
                add(newSubtask);
            }
        }
        return newTask;
    }

    private void add(Task task) throws TaskGetterException, TaskAddException {
        if (task instanceof SubTask) {
            addSubtask((SubTask) task);
        } else if (task instanceof Epic) {
            addEpic((Epic) task);
        } else {
            addTask(task);
        }
    }

    private boolean add(Task task, TaskType type) throws TaskAddException {
        Map<String, Task> tasks;

        if (task.getTaskID() != null && allId.contains(task.getTaskID())) {
            throw new TaskAddException("%s с id=%s уже существует.\n", type.getValue(), task.getTaskID());
        }
        if (tasksByType.containsKey(type)) {
            tasks = tasksByType.get(type);
        } else {
            tasks = new HashMap<>();
        }

        task.setTaskID(newTaskID());
        allId.add(task.getTaskID());
        tasks.put(task.getTaskID(), task);
        tasksByType.put(type, tasks);
        historyManager.add(task);
        return true;
    }

    private String newTaskID() {
        return String.format("%04d", ++lastID);
    }

    @Override
    public List<SubTask> getAllSubtaskByEpic(Epic epic) {
        List<SubTask> subTasks = epic.getSubTasks();
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
        throw new TaskGetterException("Задача с id=%s не найдена.\n", taskID);
    }

    @Override
    public Task getTask(String taskID) throws TaskGetterException {
        Task task = getByIdAndType(taskID, TaskType.TASK);
        if (task == null) throw new TaskGetterException("%s с id=%s не найдена.\n", TaskType.TASK.getValue(), taskID);
        return task;
    }

    @Override
    public Task getEpic(String taskID) throws TaskGetterException {
        Task task = getByIdAndType(taskID, TaskType.EPIC);
        if (task == null) throw new TaskGetterException("%s с id=%s не найден.\n", TaskType.TASK.getValue(), taskID);
        return task;
    }

    @Override
    public Task getSubtask(String taskID) throws TaskGetterException {
        Task task = getByIdAndType(taskID, TaskType.SUB_TASK);
        if (task == null) throw new TaskGetterException("%s с id=%s не найдена.\n", TaskType.TASK.getValue(), taskID);
        return task;
    }

    protected List<Task> getAllByType(TaskType taskType) {
        if (tasksByType.get(taskType) == null || tasksByType.get(taskType).isEmpty()) return new ArrayList<>();
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

        for (SubTask subTask : epic.getSubTasks()) {
            historyManager.remove(subTask.getTaskID());
            subtasks.remove(subTask.getTaskID());
        }
        epic.getSubTasks().clear();
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<SubTask> allSubTasks = new ArrayList<>(epic.getSubTasks());

        if (allSubTasks.isEmpty()) {
            Helper.printMessage(Helper.EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE);
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean isDone = true;
        boolean isNew = true;
        for (SubTask subTask : allSubTasks) {
            TaskStatus currentStatus = subTask.getStatus();

            isDone &= (currentStatus == TaskStatus.DONE);
            isNew &= (currentStatus == TaskStatus.NEW);
            boolean isInProgress = (!isDone && !isNew) || (currentStatus == TaskStatus.IN_PROGRESS);

            if (isInProgress) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
        }
        if (isNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private Task getByIdAndType(String taskID, TaskType type) {
        Map<String, Task> tasks = tasksByType.get(type);
        if (tasks != null && tasks.containsKey(taskID)) {
            Task task = tasks.get(taskID);
            historyManager.add(task);
            return task;
        }
        return null;
    }
}
