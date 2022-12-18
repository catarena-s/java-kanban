package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.managers.schadule.Day;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleValidator;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.utils.Helper.formatter;

class ScheduleValidatorTest implements TestLogger {
    final ScheduleValidator validator = new ScheduleValidator();
    private TaskManager taskManager;

    @BeforeEach
    void setUp(TestInfo info) throws IOException, TaskException {
        final Managers managers = new Managers(1);
        taskManager = managers.getDefault();

        if (info.getTags().contains("InitData")) {
            final List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
            for (String line : testLines) {
                if (line.isBlank()) continue;
                final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                final Task task = TestAddCommand.parseLine(testLine, taskManager);
                validator.takeTimeForTask(task);
                taskManager.add(task);
            }
        }
    }

    @Test
    @DisplayName("Проверяем бронирование времени для задач без пересечений по времени")
    void takeTimeForTaskWithOutOverlappingTime() throws TaskException, IOException {
        final List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
        for (String line : testLines) {
            if (line.isBlank()) continue;
            final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
            final String expected = TestHelper.getExpectation(line).trim();
            final Task task = TestAddCommand.parseLine(testLine, taskManager);
            Helper.printMessage(task.toActualStringFoTest());

            Helper.printMessage("Before:");
            ScheduleUtil.printDay(validator, task, false);

            boolean result = validator.takeTimeForTask(task);

            Helper.printMessage("After:");
            ScheduleUtil.printDay(validator, task, false);

            assertEquals(Boolean.valueOf(expected), result);
            Helper.printDotsSeparator();
        }
    }


    //    @Test
    @Tag(value = "InitData")
    @DisplayName("Проверяем добавление задач с пересекающимся временем или некорректной датой")
    @ParameterizedTest(name = "Задача id={0}: startTime={1} duration={2} -> {3}")
    @CsvSource(value = {
            "0001, 12-12-2022 09:10, 150, Ошибка: Недостаточно свободного временив в расписании.",
            "0002, 15-01-2024 14:22, 200, Ошибка: Планировать можно только на год вперед.",
            "0003, 12-12-2022 10:12, 110, Ошибка: Время в расписании занято.",})
    void takeTimeForTaskWithOverlappingTime(String taskId, String newStartTime, int duration, String expectationId) throws TaskGetterException {
        SimpleTask task = (SimpleTask) taskManager.getTask(taskId);
        task.builder().startTime(newStartTime).duration(duration);
        assertException(expectationId, task);
    }

    @DisplayName("Освобождаем время: ")
    @ParameterizedTest(name = "Задача id={0}: свободные слоты до={1} свободные слоты после={2}")
    @Tag(value = "InitData")
    @CsvSource({"0001, 94, 96", "0005, 76, 85"})
    void freeTime(String taskId, int freeTimeBefore, int freeTimeAfter) throws TaskGetterException {
        Helper.printDotsSeparator();
        Task task = taskManager.getById(taskId);
        Helper.printMessage("Before:");
        ScheduleUtil.printDay(validator, task, false);//если передать true - напечатается всё время,false - только занятое
        LocalDate taskDate = task.getStartTime().toLocalDate();
        Optional<List<Day>> days = Optional.ofNullable(validator.getBusyDays());
        Day day = days.orElse(new ArrayList<>()).stream()
                .filter(f -> f.getDate().equals(taskDate))
                .findFirst()
                .orElse(new Day(taskDate));

        assertEquals(freeTimeBefore, day.getCountFreeTimeSlotsInDay());
        Helper.printMessage("Освобождаем время задачи id=%s startTime='%s' duration=%d",
                task.getTaskID(), task.getStartTime().format(formatter), task.getDuration());
        validator.freeTime(task);

        ScheduleUtil.printDay(validator, task, false);

        assertEquals(freeTimeAfter, day.getCountFreeTimeSlotsInDay());
        Helper.printDotsSeparator();
    }

    private void assertException(String indexExpection, Task task) {
        TaskException ex = Assertions.assertThrows(
                TaskException.class,
                () -> validator.takeTimeForTask(task));
        assertEquals(indexExpection, ex.getDetailMessage().trim());
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получаем дни на которые распределялись задачи.")
    void getBusyDays() {
        Optional<List<Day>> days = Optional.ofNullable(validator.getBusyDays());
        days.ifPresent(days1 -> days1.forEach(d -> ScheduleUtil.print(d, false)));
        int size = days.orElse(new ArrayList<>()).size();
        assertEquals(4, size);
        Helper.printDotsSeparator();
    }
}