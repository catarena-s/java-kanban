package ru.yandex.practicum.kanban.model.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

class SimpleTaskTest extends TaskTest<SimpleTask> {
    @BeforeEach
    void setUp(TestInfo info) {
        task = new SimpleTask();
        if (info.getTags().contains("Init")) {
            task.builder()
                    .taskId("0001")
                    .name("Task1")
                    .description("Description")
                    .status(TaskStatus.IN_PROGRESS)
                    .startTime("01-01-2015 15:00")
                    .duration(15);
        }
    }

    @Test
    void testInit() {
        super.init("TASK, 0001, NEW, SimpleTask, desription, 0, 01-01-2222 00:00");
    }

    @Test
    @Tag(value = "Init")
    void testGetType() {
        super.getType(TaskType.TASK);
    }

    @Test
    @Tag(value = "Init")
    void testGetDuration() {
        super.getDuration(15);
    }

    @Test
    @Tag(value = "Init")
    void testUpdateStatus() {
        task.builder().status(TaskStatus.DONE);
    }

    @Test
    @Tag(value = "Init")
    void getStartTime() {
        super.getStartTime("01-01-2015 15:00");
    }
    @Test
    @Tag(value = "Init")
    void testToString() {
        super.testToString(task.toString());
    }

    @Test
    @Tag(value = "Init")
    void toCompactString() {
        super.toCompactString(task.toCompactString());
    }

    @Test
    @Tag(value = "Init")
    void toCompactString2() {
        super.toCompactString2(task.toCompactString2());
    }

    @Test
    @Tag(value = "Init")
    void toActualStringFoTest() {
        super.toActualStringFoTest(task.toActualStringFoTest());
    }

    @Test
    @Tag(value = "Init")
    void getStatus() {
        super.getStatus(TaskStatus.IN_PROGRESS);
    }

}