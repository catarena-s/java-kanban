package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    void testGetEndTime() {
        super.getEndTime("01-01-2015 15:15");
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0006, Task name, Task desription",
            "'', '', ''",
            "'','' , Task description",
            "0002,'' ,'' "})
    @DisplayName("Инициализация")
    void testInit(String id, String name, String description) {

        super.testInit(id, name, description);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить тип задачи")
    void testGetType() {
        super.getType(TaskType.TASK);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить duration")
    void testGetDuration() {
        super.getDuration(15);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Обновить статус")
    void testUpdateStatus() {
        task.builder().status(TaskStatus.DONE);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить время начала")
    void getStartTime() {
        super.getStartTime("01-01-2015 15:00");
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить статус")
    void testGetStatus() {
        super.getStatus(TaskStatus.IN_PROGRESS);
    }

    @Tag(value = "InitData")
    @ParameterizedTest(name = "Устанавливаем duration={0}")
    @CsvSource({"0 , true", "150 , true", "-120 , false"})
    @DisplayName("Проверяем обновление duration")
    void testSetDuration(int value, boolean isCorrect) {
        if (!isCorrect) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> task.updateDuration(-120));
            assertEquals("Значение <duration> должно быть больше положительным", ex.getMessage());
        } else {
            task.updateDuration(value);
            assertEquals(value, task.getDuration());
        }
    }

}