package ru.yandex.practicum.kanban.model;

public interface Updateable {
    void updateStatus(TaskStatus status);
    void updateStartTime(String startTime);
    void updateDuration(int duration);
}
