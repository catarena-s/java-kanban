package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.tests.TestHelper.formatter;

class TaskTest<T extends Task> implements TestLogger {
    T task;
    void getEndTime(String expectation) {
        LocalDateTime endTime = task.getEndTime();
        assertEquals(expectation, endTime.format(TestHelper.formatter));
    }

    void testInit(String id, String name, String description) {
        task.init(id, name, description);
        assertEquals(id, task.getTaskID());
        assertEquals(name, task.getName());
        assertEquals(description, task.getDescription());
        assertEquals("01-01-2222 00:00", task.getStartTime().format(formatter));
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(0, task.getDuration());
    }
//    void init(String expectation) {
//        task.init("0001", task.getClass().getSimpleName(), "desription");
//        if (task instanceof SubTask) {
//            Epic epic = new Epic();
//            epic.builder().taskId("0001");
//            ((SubTask) task).builder().epic(epic.getTaskID());
//            epic.addSubtask((SubTask) task);
//        }
//        assertEquals(expectation, task.toActualStringFoTest());
//        Helper.printMessage(task.toActualStringFoTest());

//    }

    void getDuration(int expectation) {
        int duration = task.getDuration();
        assertEquals(expectation, duration);
    }

    void getStartTime(String expectation) {
        LocalDateTime dateTime = task.getStartTime();
        assertEquals(expectation, dateTime.format(TestHelper.formatter));
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