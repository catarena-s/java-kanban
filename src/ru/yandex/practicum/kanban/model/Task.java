package ru.yandex.practicum.kanban.model;

import com.google.gson.annotations.SerializedName;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;

public class Task implements Comparable<Task>, Updatable {
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%-8s, %s, %-12s, %-15s, %-25s, %2s, %s";
    protected String taskID = "";
    protected String name = "";
    protected String description = "";
    protected int duration = 0;
    protected LocalDateTime startTime;
    private TaskType taskType = TaskType.TASK;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    private TaskStatus taskStatus = TaskStatus.NEW;

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int duration, String startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        if (!startTime.isBlank())
            this.startTime = LocalDateTime.parse(startTime, Helper.formatter);
        else this.startTime = null;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(duration) : null;
    }

    @Override
    public String toString() {
        return "ID='" + taskID + '\'' +
                ", status=" + taskStatus +
                ", name='" + name + '\'' +
                ", taskType='" + getType().toString() + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime='" + timeToString(startTime) + "'" +
                ", endTime='" + timeToString(getEndTime()) + "'";
    }

    public String toCompactString() {
        String resFormat = DEFAULT_FORMAT_OUT_DATA;

        return String.format(resFormat, getType(), taskID, taskStatus, name, description, duration, timeToString(startTime));
    }

    public void init(String id, String name, String description) {
        builder().taskId(id)
                .name(name)
                .description(description);
    }

    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %02d, %s ,%s";

        return String.format(resFormat, taskID, taskStatus, name, description, duration,
                timeToString(startTime), timeToString(getEndTime()));
    }

    protected String timeToString(LocalDateTime dateTime) {
        return dateTime == null ? "" : startTime.format(Helper.formatter);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Task o) {
        return String.CASE_INSENSITIVE_ORDER.compare(taskID, o.getTaskID());
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Builder builder() {
        return new Builder(this);
    }

    @Override
    public void updateStatus(TaskStatus status) {
        builder().status(status);
    }

    @Override
    public void updateStartTime(String startTime) {
        builder().startTime(startTime);
    }

    @Override
    public void updateDuration(int duration) {
        builder().duration(duration);
    }

    public static class Builder {
        protected Task task;

        public Builder(Task task) {
            this.task = task;
        }

        public Builder name(String name) {
            task.setName(name);
            return this;
        }

        public Builder taskId(String id) {
            task.setTaskID(id);
            return this;
        }

        public Builder description(String description) {
            task.setDescription(description);
            return this;
        }

        public Builder status(TaskStatus status) {
            task.setStatus(status);
            return this;
        }

        public Task build() {
            return task;
        }


        public Builder startTime(String startTime) {
            if (startTime.isBlank()) return this;
            LocalDateTime time = LocalDateTime.parse(startTime, Helper.formatter);
            task.setStartTime(time);
            return this;
        }

        public Builder duration(int duration) {
            if (duration < 0)
                throw new IllegalArgumentException("Значение <duration> должно быть больше положительным");
            task.setDuration(duration);
            return this;
        }
    }

}
