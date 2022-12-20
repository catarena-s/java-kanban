package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {

    private final List<Task> subTasks;
    private LocalDateTime endTime;

    public Epic() {
        super();
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
    }

    @Override
    public String toCompactString() {
        return String.format("%-8s, %s", TaskType.EPIC, super.toCompactString());
    }

    @Override
    public String toActualStringFoTest() {
        return String.format("%s, %s", TaskType.EPIC, super.toActualStringFoTest());
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        List<String> listSubTaskId = subTasks.stream().map(s -> s.taskID).collect(Collectors.toList());
        return "Epic{" +
                super.toString() +
                ", endTime=" + (endTime == null ? "'-', " : endTime) +
                "subTasks=[" + String.join(", ", listSubTaskId) +
                "]" +
                '}';
    }
}
