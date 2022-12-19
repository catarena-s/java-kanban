package ru.yandex.practicum.kanban.tests.unit_tests.model;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.utils.Helper.MAX_DATE;
import static ru.yandex.practicum.kanban.utils.Helper.formatter;

class TaskTest<T extends Task> implements TestLogger {
    T task;

    void getEndTime(String expectation) {
        LocalDateTime endTime = task.getEndTime();
        assertEquals(expectation, endTime.format(formatter));
    }

    void init(String id, String name, String description) {
        task.init(id, name, description);
        assertEquals(id, task.getTaskID());
        assertEquals(name, task.getName());
        assertEquals(description, task.getDescription());
        assertEquals(MAX_DATE.format(formatter), task.getStartTime().format(formatter),"Даты не сходятся");
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(0, task.getDuration());
    }

    void getDuration(int expectation) {
        int duration = task.getDuration();
        assertEquals(expectation, duration);
    }

    void getStartTime(String expectation) {
        LocalDateTime dateTime = task.getStartTime();
        assertEquals(expectation, dateTime.format(formatter));
    }


    void getStatus(TaskStatus expectation) {
        TaskStatus status = task.getStatus();
        assertEquals(expectation, status);
    }

    void getType(TaskType expectation) {
        TaskType type = task.getType();
        assertEquals(expectation, type);
    }

}