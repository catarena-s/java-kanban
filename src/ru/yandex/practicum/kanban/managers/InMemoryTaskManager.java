package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.util.*;

import static ru.yandex.practicum.kanban.utils.Helper.printMessage;

public class InMemoryTaskManager implements TaskManager {
    private final Map<TaskType, Map<String, Task>> tasksByType;
    private final HistoryManager historyManager;
    private int lastID = 0;

    public InMemoryTaskManager() {
        tasksByType = new EnumMap<>(TaskType.class);
        historyManager = Managers.getDefaultHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private String newTaskID() {
        return String.format("%04d", ++lastID);
    }

    @Override
    public void add(Task task, TaskType type) {
        Map<String, Task> tasks;

        task.setTaskID(newTaskID());

        if (type == TaskType.SUB_TASK) {
            Epic epic = (Epic) getEpic(((SubTask) task).getEpicID());
            epic.addSubtask((SubTask) task);
        }

        if (tasksByType.containsKey(type)) {
            tasks = tasksByType.get(type);
        } else {
            tasks = new HashMap<>();
        }

        tasks.put(task.getTaskID(), task);
        tasksByType.put(type, tasks);
    }

    @Override
    public void removeAllTasks() {
        tasksByType.clear();
        lastID = 0;
    }

    @Override
    public void removeAllTasks(TaskType taskType) {
        if (tasksByType.containsKey(taskType)) {
            if (taskType == TaskType.SUB_TASK) {
                Map<String, Task> subTasks = tasksByType.get(taskType);
                for (Task subTask : subTasks.values()) {
                    Epic epic = (Epic) getEpic(((SubTask) subTask).getEpicID());
                    epic.getSubTasksID().remove(subTask.getTaskID());
                }
            }
            if (taskType == TaskType.EPIC) {
                Map<String, Task> epics = tasksByType.get(taskType);
                for (Task epic : epics.values()) {
                    ((Epic) epic).getSubTasksID().clear();
                }
            }
            tasksByType.remove(taskType);
        }
    }

    @Override
    public void removeTaskByID(String taskID) {
        for (Map.Entry<TaskType, Map<String, Task>> entry : tasksByType.entrySet()) {
            Map<String, Task> tasks = entry.getValue();
            if (tasks.containsKey(taskID)) {
                if (entry.getKey() == TaskType.SUB_TASK) {
                    SubTask subTask = (SubTask) tasks.get(taskID);
                    Epic epic = (Epic) getEpic(subTask.getEpicID());
                    epic.getSubTasksID().remove(taskID);
                }
                tasks.remove(taskID);
            }
        }
    }

    @Override
    public void removeTaskByID(String taskID, TaskType taskType) {
        Map<String, Task> tasks = tasksByType.get(taskType);
        if (tasks.containsKey(taskID)) {
            if (taskType == TaskType.SUB_TASK) {
                SubTask subTask = (SubTask) tasks.get(taskID);
                Epic epic = (Epic) getEpic(subTask.getEpicID());
                epic.getSubTasksID().remove(taskID);
            }
            tasks.remove(taskID);
        }
    }

    @Override
    public List<Task> getAllSubtaskByEpic(Epic epic) {
        List<Task> subTasks = new ArrayList<>();
        for (String subTaskId : epic.getSubTasksID()) {
            subTasks.add(getSubtask(subTaskId));
        }
        return subTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();

        if (tasksByType.get(TaskType.TASK) != null) {
            allTasks.addAll(tasksByType.get(TaskType.TASK).values());
        }
        if (tasksByType.get(TaskType.EPIC) != null) {
            for (Task epic : tasksByType.get(TaskType.EPIC).values()) {
                allTasks.add(epic);
                for (String subTaskId : ((Epic) epic).getSubTasksID()) {
                    allTasks.add(getSubtask(subTaskId));
                }
            }
        }
        return allTasks;
    }

    @Override
    public List<Task> getAllTasks(TaskType taskType) {
        return new ArrayList<>(tasksByType.get(taskType).values());
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
        return getByIdAndType(taskID,TaskType.TASK);
    }

    @Override
    public Task getEpic(String taskID) {
        return getByIdAndType(taskID,TaskType.EPIC);
    }

    @Override
    public Task getSubtask(String taskID) {
        return getByIdAndType(taskID,TaskType.SUB_TASK);
    }

    private Task getByIdAndType(String taskID,TaskType type) {
        Map<String, Task> tasks = tasksByType.get(type);
        if (tasks.containsKey(taskID)) {
            Task task = tasks.get(taskID);
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public void updateTask(Task task, TaskType taskType) {
        Map<String, Task> taskByType = tasksByType.get(taskType);
        taskByType.put(task.getTaskID(), task);
        if (taskType == TaskType.EPIC) {
            updateEpicStatus((Epic) task);
        }
        if (taskType == TaskType.SUB_TASK) {
            Epic epic = (Epic) getEpic(((SubTask) task).getEpicID());
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        int countDone = 0;
        int countNew = 0;
        ArrayList<String> allSubTasks = new ArrayList<>(epic.getSubTasksID());
        if (allSubTasks.isEmpty()) {
            printMessage(Helper.EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE);
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        for (String subTaskId : allSubTasks) {
            if (getSubtask(subTaskId).getStatus() == TaskStatus.DONE) {
                countDone++;
            }
            if (getSubtask(subTaskId).getStatus() == TaskStatus.NEW) {
                countNew++;
            }
        }
        if (countNew == epic.getSubTasksID().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone == epic.getSubTasksID().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
