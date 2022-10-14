package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.util.TaskStatus;
import ru.yandex.practicum.kanban.util.TaskType;

import java.util.*;

public class TaskManager {
    private final Map<TaskType, Map<Integer, Task>> tasksByType;
    private int lastID;

    public int getLastID() {
        return lastID;
    }

    public TaskManager() {
        tasksByType = new EnumMap<>(TaskType.class);
        lastID = 0;
    }

    public void addTask(Task task, TaskType type) {
        Map<Integer, Task> tasks;
        task.setTaskID(++lastID);
        if (type == TaskType.SUB_TASK) {
            Epic epic = (Epic) getTaskById(((SubTask) task).getEpicID());
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

    public void removeAllTasks() {
        tasksByType.clear();
        lastID = 0;
    }

    public void removeAllTasks(TaskType taskType) {
        if (tasksByType.containsKey(taskType)) {
            if (taskType == TaskType.SUB_TASK) {
                Map<Integer, Task> subTasks = tasksByType.get(taskType);
                for (Task subTask : subTasks.values()) {
                    Epic epic = (Epic) getTaskById(((SubTask) subTask).getEpicID());
                    epic.getSubTasks().remove(subTask.getTaskID());
                }
            }
            if (taskType == TaskType.EPIC) {
                Map<Integer, Task> epics = tasksByType.get(taskType);
                for (Task epic : epics.values()) {
                    ((Epic) epic).getSubTasks().clear();
                }
            }
            tasksByType.remove(taskType);
        }
    }

    public void removeTaskByID(int taskID) {
        for (Map.Entry<TaskType, Map<Integer, Task>> entry : tasksByType.entrySet()) {
            Map<Integer, Task> tasks = entry.getValue();
            if (tasks.containsKey(taskID)) {
                if (entry.getKey() == TaskType.SUB_TASK) {
                    SubTask subTask = (SubTask) tasks.get(taskID);
                    Epic epic = (Epic) getTaskById(subTask.getEpicID());
                    epic.getSubTasks().remove(taskID);
                }
                tasks.remove(taskID);
            }
        }
    }

    public void removeTaskByID(int taskID, TaskType taskType) {
        Map<Integer, Task> tasks = tasksByType.get(taskType);
        if (tasks.containsKey(taskID)) {
            if (taskType == TaskType.SUB_TASK) {
                SubTask subTask = (SubTask) tasks.get(taskID);
                Epic epic = (Epic) getTaskById(subTask.getEpicID());
                epic.getSubTasks().remove(taskID);
            }
            tasks.remove(taskID);
        }
    }

    public List<Task> getAllSubtaskByEpic(Epic epic) {
        return new ArrayList<>(epic.getSubTasks().values());
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();

        if (tasksByType.get(TaskType.TASK) != null) {
            allTasks.addAll(tasksByType.get(TaskType.TASK).values());
        }
        if (tasksByType.get(TaskType.EPIC) != null) {
            for (Task epic : tasksByType.get(TaskType.EPIC).values()) {
                allTasks.add(epic);
                Map<Integer, SubTask> epicSubTasks = ((Epic) epic).getSubTasks();
                allTasks.addAll(epicSubTasks.values());
            }
        }
        return allTasks;
    }

    public List<Task> getAllTasks(TaskType taskType) {
        return new ArrayList<>(tasksByType.get(taskType).values());
    }

    public Task getTaskById(int taskID) {
        for (Map<Integer, Task> map : tasksByType.values()) {
            if (map.containsKey(taskID)) {
                return map.get(taskID);
            }
        }
        return null;
    }

    public void updateTask(Task task, TaskType taskType) {
        Map<Integer, Task> taskByType = tasksByType.get(taskType);
        taskByType.put(task.getTaskID(), task);
        if (taskType == TaskType.EPIC) {
            updateEpicStatus((Epic) task);
        }
        if (taskType == TaskType.SUB_TASK) {
            Epic epic = (Epic) getTaskById(((SubTask) task).getEpicID());
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        int countDone = 0;
        int countNew = 0;
        ArrayList<SubTask> allSubTasks = new ArrayList<>(epic.getSubTasks().values());

        for (SubTask subTask : allSubTasks) {
            if (subTask.getStatus() == TaskStatus.DONE) {
                countDone++;
            }
            if (subTask.getStatus() == TaskStatus.NEW) {
                countNew++;
            }
        }
        if (epic.getSubTasks().isEmpty() || countNew == epic.getSubTasks().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone == epic.getSubTasks().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
