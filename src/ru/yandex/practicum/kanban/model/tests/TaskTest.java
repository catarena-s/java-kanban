package ru.yandex.practicum.kanban.model.tests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest<T extends Task> {
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    T task;

    void testToString(String string) {
        System.out.println(string);
    }

    void toCompactString(String string) {
        System.out.println(string);
    }

    void toCompactString2(String string) {
        System.out.println(string);
    }

    void toActualStringFoTest(String string) {
        System.out.println(string);
    }

    void getEndTime(String expectation) {
        String endTime = task.getEndTime();
        assertEquals(expectation, endTime);


    }

    void init(String expectation) {
        task.init("0001", task.getClass().getSimpleName(), "desription");
        if (task instanceof SubTask) {
            Epic epic = new Epic();
            epic.builder().taskId("0001");
            ((SubTask) task).builder().epic(epic.getTaskID());
            epic.addSubtask((SubTask) task);
        }
        assertEquals(expectation, task.toActualStringFoTest());
        Helper.printMessage(task.toActualStringFoTest());
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