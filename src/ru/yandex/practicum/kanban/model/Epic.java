package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        List<String> listSubTaskId = new ArrayList<>();
        listSubTaskId.addAll(subTasks.stream().map(s -> s.taskID).collect(Collectors.toList()));
        return "Epic{ " +
                "ID=" + getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasks=[" + String.join(", ", listSubTaskId) +
                "] }";
    }

    @Override
    public String toStringShort() {
        List<String> listSubTaskId = new ArrayList<>();
        listSubTaskId.addAll(subTasks.stream().map(s -> s.taskID).collect(Collectors.toList()));
        return "Epic{ " +
                "ID=" + taskID +
                ", subTasks=[" + String.join(", ", listSubTaskId) +
                "] }";
    }
}
