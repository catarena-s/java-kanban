package ru.yandex.practicum.kanban.model.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

class SubTaskTest extends TaskTest<SubTask> {

    @BeforeEach
    void setUp(TestInfo info) {
        task = new SubTask();
        if (info.getTags().contains("Init")) {
            Epic epic = new Epic();
            epic.builder()
                    .taskId("0001")
                    .name("Epic1")
                    .description("Description");
            task.builder()
                    .taskId("0005")
                    .name("SubTask1")
                    .epic(epic.getTaskID())
                    .description("Description")
                    .status(TaskStatus.IN_PROGRESS)
                    .startTime("01-01-2015 15:00")
                    .duration(15);
            epic.addSubtask(task);
        }
    }

    @Test
    void testInit() {
        super.init("SUB_TASK, 0001, NEW, SubTask, desription, 0, 01-01-2222 00:00, 0001");
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
    void testGetEndTime() {
        super.getEndTime("01-01-2015 15:15");
    }

    @Test
    @Tag(value = "Init")
    void testGetDuration() {
        super.getDuration(15);
    }

    @Test
    @Tag(value = "Init")
    void testGetStartTime() {
        super.getStartTime("01-01-2015 15:00");
    }

    @Test
    @Tag(value = "Init")
    void testGetStatus() {
        super.getStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void testGetType() {
        super.getType(TaskType.SUB_TASK);
    }

}