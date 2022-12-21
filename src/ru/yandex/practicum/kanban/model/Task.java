package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;

public abstract class Task implements Comparable<Task> {
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%s, %-12s, %-15s, %-25s, %3s, %19s";
    protected String taskID = "";
    protected String name = "";
    protected String description = "";
    protected int duration = 0;
    protected LocalDateTime startTime;

    private TaskStatus taskStatus = TaskStatus.NEW;

    protected Task() {
        initDates();
    }
    protected Task(String name, String description) {
        this.name = name;
        this.description = description;
        initDates();
    }

    protected Task(String name, String description, int duration, String startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime, Helper.formatter);
    }

    private void initDates() {
        startTime = Helper.MAX_DATE;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "ID='" + taskID + '\'' +
                ", status=" + taskStatus +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime='" + startTime.format(Helper.formatter);
    }

    public String toCompactString() {
        return String.format(DEFAULT_FORMAT_OUT_DATA, taskID, taskStatus, name, description, duration, timeToString(startTime));
    }

    public void init(String id, String name, String description) {
        builder().taskId(id)
                .name(name)
                .description(description);
    }

    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %3s, %s ,%s";

        return String.format(resFormat, taskID, taskStatus, name, description, duration,
                timeToString(startTime), timeToString(getEndTime()));
    }

    protected String timeToString(LocalDateTime dateTime) {
        return (dateTime == null)||(dateTime.equals(Helper.MAX_DATE)) ? "" : startTime.format(Helper.formatter);
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

    protected void setTaskID(String taskID) {
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

    public abstract TaskType getType();

    public Builder builder() {
        return new Builder(this);
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

        public Task build() {
            return task;
        }


    }

}
