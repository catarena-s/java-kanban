package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubTaskTest extends TaskTest {

    @BeforeEach
    void setUp(TestInfo info) {
        task = new SubTask();
        if (info.getTags().contains("Init")) {
            Epic epic = new Epic();
            epic.builder()
                    .taskId("0001")
                    .name("Epic1")
                    .description("Description");
            ((SubTask) task).builder()
                    .taskId("0005")
                    .name("SubTask1")
                    .epic(epic.getTaskID())
                    .description("Description")
                    .status(TaskStatus.IN_PROGRESS)
                    .startTime("01-01-2015 15:00:02")
                    .duration(15);
            epic.addSubtask((SubTask) task);
        }
    }

    @Test
    @Tag(value = "Init")
    void StringTest() {
        super.toStringTest();
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0006, Subtask name, Subtask desription,'',0",
            "'','' ,'' ,'',0",
            "'','' , Subtask description,'',0",
            "0002,'' ,'','' ,0"})
    @DisplayName("Инициализация")
    void init(String id, String name, String description, String starTimeExpected, int duration) {
        super.init(id, name, description, starTimeExpected, duration);
    }

    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    @ParameterizedTest
    @ValueSource(strings = "01-01-2015 15:15:02")
    void getEndTime(String expected) {
        super.getEndTime(expected);
    }

    @Tag(value = "Init")
    @ParameterizedTest
    @ValueSource(strings = "01-01-2015 15:00:02")
    void getStartTime(String expected) {
        super.getStartTime(expected);
    }

    @ParameterizedTest
    @Tag(value = "Init")
    @DisplayName("Получить duration")
    @ValueSource(ints = 15)
    void getDuration(int expected) {
        super.getDuration(expected);
    }



    @Override
    @ParameterizedTest
    @ValueSource(strings = "SUB_TASK")
    @DisplayName("Получить тип задачи")
    void getType(String expected) {
        super.getType(expected);
    }

    @Override
    @ParameterizedTest
    @ValueSource(strings = "IN_PROGRESS")
    @Tag(value = "Init")
    @DisplayName("Получить статус")
    void getStatus(String expected) {
        super.getStatus(expected);
    }

    @Override
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