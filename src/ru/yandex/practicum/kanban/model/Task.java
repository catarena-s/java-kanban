package ru.yandex.practicum.kanban.model;

public class Task implements Comparable<Task> {
    protected String taskID = "";
    protected String name = "";
    protected String description = "";
    protected TaskStatus taskStatus;
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%s, %-8s, %-12s, %-15s, %-25s,";

    public Task() {
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
        String resFormat = DEFAULT_FORMAT_OUT_DATA + "%n";

        return String.format(resFormat, taskID, TaskType.TASK, taskStatus, name, description);
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

        public Builder status(TaskStatus status) {
            task.setStatus(status);
            return this;
        }

        public Task build() {
            return task;
        }
    }

}
