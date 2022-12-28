package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {

    private LocalDateTime endTime;
//    private TaskType taskType = TaskType.EPIC;
    private final List<Task> subTasks;

    public Epic() {
        super();
        subTasks = new ArrayList<>();
        setTaskType(TaskType.EPIC);
    }

    public Epic(String name, String description, int duration, String startTime, LocalDateTime endTime, List<Task> subTasks) {
        super(name, description, duration, startTime);
        this.endTime = endTime;
        this.subTasks = subTasks;
        setTaskType(TaskType.EPIC);
    }

    public Epic(String name, String description, int duration, String startTime, String endTime) {
        super(name, description, duration, startTime);
        setTaskType(TaskType.EPIC);
        if (!endTime.isBlank())
            this.endTime = LocalDateTime.parse(endTime, Helper.formatter);
        else
            this.endTime = null;
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, String endTime) {
        super(name, description);
        if (!endTime.isBlank())
            this.endTime = LocalDateTime.parse(endTime, Helper.formatter);
        else
            this.endTime = null;
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

//    @Override
//    public String toCompactString() {
////        return String.format("%-8s, %s", TaskType.EPIC, super.toCompactString());
//    }

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
                ", endTime='" + (endTime == null ? "" : endTime.format(Helper.formatter)) +"',"+
                "subTasks=[" + String.join(", ", listSubTaskId)+ "]" +
                '}';//String.join(", ", listSubTaskId)
    }

//    @Override
//    public String toString() {
//        return "Epic{" +
//                "endTime=" + endTime +
//                ", subTasks=" + subTasks +
//                ", taskID='" + taskID + '\'' +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", duration=" + duration +
//                ", startTime=" + startTime +
//                '}';
//    }
}
