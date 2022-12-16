package ru.yandex.practicum.kanban.model.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

/**
 * getduration startTime посчитала избыточным - эти методы тестируются в таск -менеджере
 */
class EpicTest extends TaskTest<Epic> {

    @BeforeEach
    void setUp(TestInfo info) {
        task = new Epic();
        if (info.getTags().contains("Init")) {
            task.builder()
                    .taskId("0001")
                    .name("Epic1")
                    .description("Description");
            SubTask subTask = new SubTask();
            subTask.builder().taskId("0002")
                    .epic(task.getTaskID())
                    .status(TaskStatus.IN_PROGRESS)
                    .duration(10)
                    .startTime("11-02-2022 02:02");
            task.addSubtask(subTask);
        }
    }

    @Test
    void testInit() {
        super.init("EPIC, 0001, NEW, Epic, desription, 0, 01-01-2222 00:00");
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
    void testGetType() {
        super.getType(TaskType.EPIC);
    }

    @Test
    @Tag(value = "Init")
    void testGetEndTime() {
        super.getEndTime("11-02-2022 02:12");
//------test 2---------------------------------------------
        SubTask subTask = new SubTask();
        subTask.builder().taskId("0003")
                .epic(task.getTaskID())
                .status(TaskStatus.IN_PROGRESS)
                .duration(20)
                .startTime("12-02-2022 02:02");
        task.addSubtask(subTask);

        super.getEndTime("12-02-2022 02:22");
    }

    @Test
    @Tag(value = "Init")
    void testGetDuration() {
        super.getDuration(task.getDuration());
    }

    @Test
    @Tag(value = "Init")
    void testGetStartTime() {
        super.getStartTime("11-02-2022 02:02");
    }

    @Test
    @Tag(value = "Init")
    void testGetStatus() {
        super.getStatus(TaskStatus.IN_PROGRESS);
    }
}