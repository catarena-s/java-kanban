package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

class EpicTest extends TaskTest<Epic> {

    @BeforeEach
    void setUp(TestInfo info) {        task = new Epic();
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
                    .startTime("11-02-2022 02:02:01");
            task.addSubtask(subTask);
        }
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0001, Epic name, Epic desription",
            "'','' ,'' ",
            "'','' , Epicdescription",
            "0002,'' ,'' "})
    @DisplayName("Инициализация")
    void init(String id, String name, String description) {
        super.init(id, name, description);
    }


    @Test
    @Tag(value = "Init")
    @DisplayName("Получить тип задачи")
    void testGetType() {
        super.getType(TaskType.EPIC);
    }
}