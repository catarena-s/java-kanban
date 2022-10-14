package ru.yandex.practicum.kanban.tasks;

import ru.yandex.practicum.kanban.util.TaskStatus;

public class Task {
    private Integer taskID = 0;
    private String name;
    private String description;
    private TaskStatus taskStatus;

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

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
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
