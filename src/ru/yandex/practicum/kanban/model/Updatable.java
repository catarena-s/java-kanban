package ru.yandex.practicum.kanban.model;

/**
 * интерфейс, что бы пометить классы(таск и сабтаск), что они могут менять статус,
 * дату начала и duration
 */
public interface Updatable {
    void updateStatus(TaskStatus status);
    void updateStartTime(String startTime);
    void updateDuration(int duration);
}
