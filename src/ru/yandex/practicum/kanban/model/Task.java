package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сделала Task абстрактым, что бы у эпика закрыть возможность извне устанавливать статус , startTime и duration
 */
public abstract class Task implements Comparable<Task> {
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%s, %-12s, %-15s, %-25s, %s, %s";
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    protected String taskID = "";
    protected String name = "";
    protected String description = "";
    protected int duration = 0;
    protected LocalDateTime startTime;
    public Task() {
        initDates();
    }
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        initDates();
    }
    private void initDates() {
        startTime = LocalDateTime.of(2222,1,1,0,0);//LocalDateTime.parse("01-01-2222 00:00", formatter);
    }

    private TaskStatus taskStatus = TaskStatus.NEW;

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
                ", startTime='" + startTime.format(formatter);
    }

    public String toCompactString() {
        String resFormat = DEFAULT_FORMAT_OUT_DATA;

        return String.format(resFormat, taskID, taskStatus, name, description, duration, startTimeToString());
    }

    public void init(String id, String name, String description) {
        builder().taskId(id)
                .name(name)
                .description(description);
    }

    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %s, %s";

        return String.format(resFormat, taskID, taskStatus, name, description, duration, startTimeToString());//, getType()
    }

    protected String startTimeToString() {
//        return startTime == null ? "" : startTime.format(formatter);
        return startTime == null ? "01-01-2222 00:00" : startTime.format(formatter);
    }

    public int getDuration() {
        return duration;
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    protected void setStartTime(LocalDateTime startTime) {
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

    protected void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    abstract public TaskType getType();

    public Builder builder() {
        return new Builder(this);
    }

    public class Builder {
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
