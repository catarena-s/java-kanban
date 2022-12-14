package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Task implements Comparable<Task> {
    protected String taskID = "";
    protected String name = "";
    protected String description = "";
    protected int duration = 0;
    protected LocalDateTime startTime;
    protected boolean sortByPriority = false;
    private TaskStatus taskStatus;
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%s, %-8s, %-12s, %-15s, %-25s";//,%-5s,%tH

    public Task() {
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration, ChronoUnit.MINUTES);
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    @Override
    public String toString() {
        return "Task{ " +
                "ID=" + taskID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                " }";
    }

    public String toCompactString() {
        String resFormat = DEFAULT_FORMAT_OUT_DATA;

        return String.format(resFormat, taskID, TaskType.TASK, taskStatus, name, description, duration, startTime);
    }
    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %s";

        return String.format(resFormat, taskID, TaskType.TASK, taskStatus, name, description, duration, startTime);
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

    public boolean isSortByPriority() {
        return sortByPriority;
    }

    protected void setSortByPriority(boolean sortByPriority) {
        this.sortByPriority = sortByPriority;
    }

    @Override
    public int compareTo(Task o) {
        if (sortByPriority)
            return startTime.compareTo(o.startTime);
        else
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

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Builder builder() {
        return new Builder(this);
    }

    public void init(String... args) {
        builder().taskId(args[0])
                .name(args[1])
                .description(args[2]);
    }

/*    public  class Builder extends Absatrct_Builder {

        public Builder(Task task) {
            super(task);
        }
        public Builder status(TaskStatus status) {
            task.setStatus(status);
            return this;
        }
    }*/
    public class Builder {
        protected Task task;
        private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

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

        public Builder duration(int duration) {
            if (duration < 0) throw new IllegalArgumentException("Значение <duration> должно быть больше 0");
            task.setDuration(duration);
            return this;
        }

        public Builder startTime(String startTime) {
            LocalDateTime time = LocalDateTime.parse(startTime, formatter);
            task.setStartTime(time);
            return this;
        }

        public Builder sortByPriority(boolean sortByPriority) {
            task.setSortByPriority(sortByPriority);
            return this;
        }

        public Task build() {
            return task;
        }

    }

}
