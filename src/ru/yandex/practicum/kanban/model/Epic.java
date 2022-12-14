package ru.yandex.practicum.kanban.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<Task> subTasks;
    private LocalDateTime endTime;

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void setDuration() {
        int durationSubtasks = 0;
        for (Task subTask : subTasks) {
            durationSubtasks += subTask.duration;
        }
        this.duration = durationSubtasks;
    }

    private void setStartTime() {
        if (!subTasks.isEmpty())
            startTime = subTasks.stream()
                                .sorted()
                                .findFirst()
                                .orElseThrow().getStartTime();
    }

    public Epic() {
        super();
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
    }

    public void check(){
        setDuration();
        setStartTime();
    }
    public List<Task> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        List<String> listSubTaskId = subTasks.stream().map(s -> s.taskID).collect(Collectors.toList());
        return "Epic{ " +
                "ID=" + getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasks=[" + String.join(", ", listSubTaskId) +
                "] }";
    }

    @Override
    public String toCompactString() {
        String resFormat = DEFAULT_FORMAT_OUT_DATA ;

        return String.format(resFormat, taskID, TaskType.EPIC, getStatus(), name, description);
    }
    @Override
    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %s";
        return String.format(resFormat, taskID, TaskType.EPIC, getStatus(), name, description, duration, startTime);
    }

}
