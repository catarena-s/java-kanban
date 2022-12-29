package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;

class EpicTest extends TaskTest {


    @Override
    @BeforeEach
    void setUp(TestInfo info) {
        task = new Epic("", "", 6, "12-05-2022 15:02:02", "15-05-2022 02:02:03");
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
            ((Epic) task).addSubtask(subTask);
        }
    }

    @Override
    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0001 , Epic name , Epic desription , 12-05-2022 15:02:02 , 6",
            "''   , ''        , ''              , 12-05-2022 15:02:02 , 6",
            "''   , ''        , Epicdescription , 12-05-2022 15:02:02 , 6",
            "0002 , ''        , ''              , 12-05-2022 15:02:02 , 6"})
    @DisplayName("Инициализация")
    void init(String id, String name, String description, String starTimeExpected, int duration) {
        super.init(id, name, description, starTimeExpected, duration);
    }

    @Override
    @ParameterizedTest
    @ValueSource(strings = "EPIC")
    @Tag(value = "Init")
    @DisplayName("Получить тип задачи")
    void getType(String expected) {
        super.getType(expected);
    }

    @Override
    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    @ParameterizedTest
    @ValueSource(strings = "15-05-2022 02:02:03")
    void getEndTime(String expected) {
        super.getEndTime(expected);
    }

    @Override
    @ParameterizedTest
    @ValueSource(strings = "12-05-2022 15:02:02")
    void getStartTime(String expected) {
        super.getStartTime(expected);
    }

    @Override
    @ParameterizedTest
    @ValueSource(strings = "NEW")
    @Tag(value = "Init")
    @DisplayName("Получить статус")
    void getStatus(String expected) {
        super.getStatus(expected);
    }

    @Override
    @Tag(value = "Init")
    @ParameterizedTest
    @ValueSource(ints = 6)
    void getDuration(int expected) {
        super.getDuration(expected);
    }
}