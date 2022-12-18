package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;

public class SubTask extends Task implements Updatable {
    private String epicID;

    public SubTask() {
        super();
        this.epicID = "";
    }

    public SubTask(String name, String description, String epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public String getEpicID() {
        return epicID;
    }

    public void setEpicID(String epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicID='" + epicID + '\'' +
                super.toString() +
                '}';
    }

    @Override
    public Builder builder() {
        return new Builder(this);
    }

    @Override
    public String toCompactString() {
        return String.format("%s, %s, %s", TaskType.SUB_TASK, super.toCompactString(), epicID);
    }

    @Override
    public String toActualStringFoTest() {
        return String.format("%s, %s, %s", TaskType.SUB_TASK, super.toActualStringFoTest(), epicID);
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

    public class Builder extends Task.Builder {
        public Builder(Task task) {
            super(task);
        }

        @Override
        public Task build() {
            return super.build();
        }
        @Override
        public Builder name(String name) {
            super.name(name);
            return this;
        }

        @Override
        public Builder taskId(String id) {
            super.taskId(id);
            return this;
        }

        @Override
        public Builder description(String description) {
            super.description(description);
            return this;
        }

        public Builder epic(String epicID) {
            ((SubTask) task).setEpicID(epicID);
            return this;
        }

        public Builder status(TaskStatus status) {
            task.setStatus(status);
            return this;
        }

        public Builder startTime(String startTime) {
            if (startTime.isBlank()) return this;
            LocalDateTime time = LocalDateTime.parse(startTime, Helper.formatter);
            task.setStartTime(time);
            return this;
        }

        public Builder duration(int duration) {
            if (duration < 0) throw new IllegalArgumentException("Значение <duration> должно быть больше положительным");
            task.setDuration(duration);
            return this;
        }
    }
}
