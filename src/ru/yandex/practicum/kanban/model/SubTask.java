package ru.yandex.practicum.kanban.model;

import java.time.Instant;

public class SubTask extends Task {
    private String epicID;

    public SubTask() {
        super();
        this.epicID = "0";
    }

    public SubTask(String name, String description, String epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }
    @Override
    public void init(String... args) {
        super.init(args);
        builder().epic(args[3]);
    }

    public String getEpicID() {
        return epicID;
    }

    public void setEpicID(String epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "SubTask{ " +
                "ID=" + getTaskID() +
                ", epicID=" + epicID +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                " }";
    }

    @Override
    public Builder builder() {
        return new Builder(this);
    }

    @Override
    public String toCompactString() {
        String resFormat = DEFAULT_FORMAT_OUT_DATA + ",%6s";

        return String.format(resFormat, taskID, TaskType.SUB_TASK, getStatus(), name, description, epicID);
    }
    @Override
    public String toActualStringFoTest() {
        String resFormat = "%s, %s, %s, %s, %s, %s";
        return String.format(resFormat, taskID, TaskType.SUB_TASK, getStatus(), name, description, epicID);
    }

    public class Builder extends Task.Builder {
        public Builder(Task task) {
            super(task);
        }

        @Override
        public Task build() {
            return super.build();
        }
        public Builder epic(String epicID) {
            ((SubTask) task).setEpicID(epicID);
            return this;
        }
    }
}
