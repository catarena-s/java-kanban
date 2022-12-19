package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                    .startTime("01-01-2015 15:00:02")
                    .duration(15);
            epic.addSubtask(task);
        }
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0006, Subtask name, Subtask desription",
            "'','' ,'' ",
            "'','' , Subtask description",
            "0002,'' ,'' "})
    @DisplayName("Инициализация")
    void init(String id, String name, String description) {
        super.init(id, name, description);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    void testGetEndTime() {
        super.getEndTime("01-01-2015 15:15:02");
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить duration")
    void testGetDuration() {
        super.getDuration(15);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить время начала")
    void testGetStartTime() {
        super.getStartTime("01-01-2015 15:00:02");
    }


    @Test
    @DisplayName("Получить тип задачи")
    void testGetType() {
        super.getType(TaskType.SUB_TASK);
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