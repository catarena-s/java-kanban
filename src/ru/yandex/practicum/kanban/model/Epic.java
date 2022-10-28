package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<String> subTasksID;

    public Epic(String name, String description) {
        super(name, description);
        subTasksID = new ArrayList<>();
    }

    public void addSubtask(SubTask subtask) {
        subTasksID.add(subtask.taskID);
    }

    public List<String> getSubTasksID() {
        return subTasksID;
    }

    @Override
    public String toString() {
        return "Epic{ " +
                "ID=" + getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasks=" + subTasksID +
                " }";
    }
}
