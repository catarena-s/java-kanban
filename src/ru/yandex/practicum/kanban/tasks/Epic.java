package ru.yandex.practicum.kanban.tasks;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final Map<String , SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new HashMap<>();
    }

    public void addSubtask(SubTask subtask) {
        subTasks.put(subtask.getTaskID(), subtask);
    }

    public Map<String, SubTask> getSubTasks() {
        return subTasks;
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
