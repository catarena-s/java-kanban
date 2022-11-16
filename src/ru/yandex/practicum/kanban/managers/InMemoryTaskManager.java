package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<TaskType, Map<String, Task>> tasksByType;
    private final HistoryManager historyManager;
    private int lastID = 0;

    public InMemoryTaskManager() {
        tasksByType = new EnumMap<>(TaskType.class);
        Managers<HistoryManager> managers = new Managers<>(new InMemoryHistoryManager());
        historyManager = managers.getDefault();
    }

    @Override
    public void addTask(Task task) {
        add(task, TaskType.TASK);
    }

    @Override
    public void addEpic(Epic task) {
        add(task, TaskType.EPIC);
    }

    @Override
    public void addSubtask(SubTask task) {
        add(task, TaskType.SUB_TASK);
        Epic epic = (Epic) getEpic(task.getEpicID());
        epic.addSubtask(task);
        updateEpicStatus(epic);
    }

    private void add(Task task, TaskType type) {
        Map<String, Task> tasks;
        task.setTaskID(newTaskID());

        if (tasksByType.containsKey(type)) {
            tasks = tasksByType.get(type);
        } else {
            tasks = new HashMap<>();
        }

        tasks.put(task.getTaskID(), task);
        tasksByType.put(type, tasks);
        historyManager.add(task);
    }

    private String newTaskID() {
        return String.format("%04d", ++lastID);
    }

    @Override
    public List<SubTask> getAllSubtaskByEpic(Epic epic) {
        return epic.getSubTasks();
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
    public Task getById(String taskID) {
        for (Map<String, Task> map : tasksByType.values()) {
            if (map.containsKey(taskID)) {
                Task task = map.get(taskID);
                historyManager.add(task);
                return task;
            }
        }
        return null;
    }

    @Override
    public Task getTask(String taskID) {
        return getByIdAndType(taskID, TaskType.TASK);
    }

    @Override
    public Task getEpic(String taskID) {
        return getByIdAndType(taskID, TaskType.EPIC);
    }

    @Override
    public Task getSubtask(String taskID) {
        return getByIdAndType(taskID, TaskType.SUB_TASK);
    }

    private List<Task> getAllByType(TaskType taskType) {
        if (tasksByType.get(taskType) == null || tasksByType.get(taskType).isEmpty()) return null;
        return new ArrayList<>(tasksByType.get(taskType).values());
    }

    @Override
    public void updateTask(Task task) {
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
    public void removeAllEpics() {
        removeAllTasks(TaskType.EPIC);
    }

    @Override
    public void removeAllTasks() {
        removeAllTasks(TaskType.TASK);
    }

    @Override
    public void removeAllSubtasks() {
        removeAllTasks(TaskType.SUB_TASK);
    }

    @Override
    public void removeTask(String taskID) {
        remove(taskID, TaskType.TASK);
    }

    @Override
    public void removeEpic(String taskID) {
        remove(taskID, TaskType.EPIC);
    }

    @Override
    public void removeSubtask(String taskID) {
        remove(taskID, TaskType.SUB_TASK);
    }

    private void removeAllTasks(TaskType taskType) {
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

    private void removeAnyById(String taskID) {
        for (Map.Entry<TaskType, Map<String, Task>> entry  : tasksByType.entrySet()) {
            Map<String, Task> tasks = entry.getValue();
            if (tasks.isEmpty()) continue;
            if (tasks.containsKey(taskID)) {
                remove(taskID, entry.getKey(), tasks);
                return;
            }
        }
    }

    private void remove(String taskID, TaskType taskType) {
        Map<String, Task> tasks = tasksByType.get(taskType);
        if (!tasks.containsKey(taskID)) return;

        remove(taskID, taskType, tasks);
    }

    private void remove(String taskID, TaskType taskType, Map<String, Task> tasks) {
        if (taskType == TaskType.EPIC) {
            Epic epic = (Epic) tasks.get(taskID);
            removeAllSubtasksForEpic(epic);
        }
        if (taskType == TaskType.SUB_TASK) {
            SubTask subTask = (SubTask) tasks.get(taskID);
            Epic epic = (Epic) getEpic(subTask.getEpicID());
            epic.getSubTasks().remove(subTask);
        }

        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    private void removeAllSubtasksForEpic(Epic epic) {
        List<SubTask> subtasks = epic.getSubTasks();
        for (SubTask sub : subtasks) {
            remove(sub.getTaskID(), TaskType.SUB_TASK);
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

            isDone = (currentStatus == TaskStatus.DONE);
            isNew = (currentStatus == TaskStatus.NEW);
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
        if (tasks.containsKey(taskID)) {
            Task task = tasks.get(taskID);
            historyManager.add(task);
            return task;
        }
        return null;
    }
}
