package ru.yandex.practicum.kanban.model;

public class Task implements Comparable<Task> {
    protected String taskID = "";
    protected String name="";
    protected String description="";
    protected TaskStatus taskStatus;
    protected static final String DEFAULT_FORMAT_OUT_DATA = "%s, %-8s, %-12s, %-15s, %-25s,";

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }
    public void init(String... args){
        taskID = args[0];
        name = args[1];
        description = args[2];
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

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public int compareTo(Task o) {
        return String.CASE_INSENSITIVE_ORDER.compare(taskID, o.getTaskID());
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

}
