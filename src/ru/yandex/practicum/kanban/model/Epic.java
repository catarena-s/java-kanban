package ru.yandex.practicum.kanban.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<SubTask> subTasks;
    private Instant endTime;

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    private void setDuration() {
        int durationSubtasks = 0;
        for (SubTask subTask : subTasks) {
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
    public List<SubTask> getSubTasks() {
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
        String resFormat = DEFAULT_FORMAT_OUT_DATA + "%n";

        return String.format(resFormat, taskID, TaskType.EPIC, taskStatus, name, description);
    }



}
