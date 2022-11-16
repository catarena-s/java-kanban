package ru.yandex.practicum.kanban.model;

public class Task {
    protected String taskID = "0";
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;

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

     public String toStringShort(){
         return "Task{ "+
                 "ID=" +taskID +
         " }";
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
}
