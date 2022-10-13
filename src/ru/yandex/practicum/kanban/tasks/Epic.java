package ru.yandex.practicum.kanban.tasks;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
     private final Map<Integer, SubTask> subTasks;

    public void addSubtask(int taskID, SubTask subtask) {
        subTasks.put(taskID, subtask);
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Epic(Integer taskID, String name, String description) {
        super(taskID, name, description);
        subTasks = new HashMap<>();
    }
    @Override
    public String toString() {
        return "Epic{ " +
                "ID=" + getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasks=" + subTasks.keySet() +
                " }";
    }
}
