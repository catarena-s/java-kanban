package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.kanban.utils.Helper.formatter;

class TaskTest implements TestLogger {
    Task task;

    @BeforeEach
    void setUp(TestInfo info) {
        task = new Task();
        if (info.getTags().contains("Init")) {
            task.builder()
                    .taskId("0001")
                    .name("Task1")
                    .description("Description")
                    .status(TaskStatus.IN_PROGRESS)
                    .startTime("01-01-2015 15:00:03")
                    .duration(15);
        }
    }


    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    @ParameterizedTest
    @ValueSource(strings = "01-01-2015 15:15:03")
    void getEndTime(String expected) {
        LocalDateTime endTime = task.getEndTime();
        assertEquals(expected, endTime.format(formatter));
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0006, Task name, Task desription,'',0",
            "'', '', '','',0",
            "'','' , Task description,'',0",
            "0002,'' ,'' ,'',0"})
    @DisplayName("Инициализация")
    void init(String id, String name, String description, String starTimeExpected, int duration) {
        task.init(id, name, description);
        assertEquals(id, task.getTaskID());
        assertEquals(name, task.getName());
        assertEquals(description, task.getDescription());
        LocalDateTime dateTime = task.getStartTime();
        assertEquals(starTimeExpected, dateTime == null ? "" : dateTime.format(formatter), "Даты не сходятся");//MAX_DATE.format(formatter)
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(duration, task.getDuration());
    }

    @Tag(value = "Init")
    @ParameterizedTest
    @ValueSource(ints = 15)
    void getDuration(int expected) {
        int duration = task.getDuration();
        assertEquals(expected, duration);
    }

    @ParameterizedTest
    @Tag(value = "Init")
    @ValueSource(strings = "01-01-2015 15:00:03")
    void getStartTime(String expected) {
        LocalDateTime dateTime = task.getStartTime();
        assertEquals(expected, dateTime.format(formatter));
    }


    @ParameterizedTest
    @ValueSource(strings = "NEW")
    void getStatus(String expected) {
        TaskStatus status = task.getStatus();
        assertEquals(TaskStatus.valueOf(expected), status);
    }

    @ParameterizedTest
    @ValueSource(strings = "TASK")
    void getType(String expected) {
        TaskType type = task.getType();
        assertEquals(TaskType.valueOf(expected), type);
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

    @Test
    protected void toStringTest() {
        Helper.printMessage(task.toString());
    }
}