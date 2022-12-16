package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;

public class SimpleTask extends Task implements Updateable{
    public SimpleTask() {
        super();
    }

    public SimpleTask(String name, String description) {
        super(name, description);
    }

    @Override
    public Builder builder() {
        return new Builder(this);
    }

    @Override
    public String toCompactString() {
        return String.format("%s, %s", TaskType.TASK, super.toCompactString());
    }

    @Override
    public String toCompactString2() {
        return String.format("%s, %s", TaskType.TASK, super.toCompactString2());
    }

    @Override
    public String toActualStringFoTest() {
        return String.format("%s, %s", TaskType.TASK, super.toActualStringFoTest());
    }

    public TaskType getType() {
        return TaskType.TASK;
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

    @Override
    public String toString() {
        return "Task{" + super.toString() + "}";
    }
    public class Builder extends Task.Builder {
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

        @Override
        public Task build() {
            return super.build();
        }

        public Builder(Task task) {
            super(task);
        }

        public Builder status(TaskStatus status) {
            task.setStatus(status);
            return this;
        }

        public Builder startTime(String startTime) {
            LocalDateTime time = LocalDateTime.parse(startTime, formatter);
            task.setStartTime(time);
            return this;
        }

        public Builder duration(int duration) {
            if (duration < 0) throw new IllegalArgumentException("Значение <duration> должно быть больше 0");
            task.setDuration(duration);
            return this;
        }

    }


}