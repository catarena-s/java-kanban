package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.managers.schadule.Day;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DayTest implements TestLogger {
    private final Map<LocalDate, Day> schedule = new HashMap<>();

    @BeforeEach
    void setUp(TestInfo info) {
        final LocalDate localDate = LocalDate.now();
        final Day day = schedule.getOrDefault(localDate, new Day(localDate));
        if (info.getTags().contains("init")) {
            final Random rnd = new Random(0);
            rnd.ints()
                    .limit(10)
                    .forEach(f -> {
                        final int hour = Math.abs(f % 24);
                        final int[] min = {0, 15, 30, 45};
                        final Random minInd = new Random(0);
                        final LocalTime time = LocalTime.of(hour, min[minInd.nextInt(4)]);
                        day.takeTimeSlots(time, 1);
                    });

            day.takeTimeSlots(LocalTime.of(10, 0), 3);
            day.takeTimeSlots(LocalTime.of(15, 0), 6);
        }
        schedule.put(localDate, day);
    }

    @Tag("init")
    @ParameterizedTest(name ="Проверяем: начало задачи {0} нужно {1} слота по 15 мин -> {2}")
    @CsvSource(value = {"10:00, 2, false", "15:00, 2, false", "14:30, 3, false", "09:30, 1, true"})
    @DisplayName("Достаточно ли свободных слотов времени(один слот 15 мин) для заданного времени.")
    void isEnoughTime(String time, String count, String expectation) {
        final Day testDay = getTestDay();
        assertEquals(Boolean.valueOf(expectation), testDay.isEnoughTime(LocalTime.parse(time), Integer.parseInt(count)));
    }

    @Test
    @DisplayName("Бронируем слоты под задачу.")
    void takeTimeSlots() {
        final Day testDay = getTestDay();
        Helper.printMessage("Before:");
        ScheduleUtil.print(testDay,false);

        testDay.takeTimeSlots(LocalTime.of(10,11),2);
        testDay.takeTimeSlots(LocalTime.of(12,22),5);

        Helper.printMessage("After:");
        ScheduleUtil.print(testDay,false);

        assertEquals(89, testDay.getCountFreeTimeSlotsInDay());
        assertEquals(7, testDay.getCountBusyTimeSlotsInDay());
        Helper.printDotsSeparator();
    }

    @Test
    @Tag("init")
    @DisplayName("Освобождаем слоты: если задача удалена, или были обновлены startTime и duration")
    void freeTimeSlots() {
        final Day testDay = getTestDay();
        assertEquals(80, testDay.getCountFreeTimeSlotsInDay());
        assertEquals(16, testDay.getCountBusyTimeSlotsInDay());
        Helper.printMessage("Before:");
        ScheduleUtil.print(testDay,false);

        testDay.freeTimeSlots(LocalTime.of(10,11),2);
        testDay.freeTimeSlots(LocalTime.of(15,22),5);
        Helper.printMessage("After:");
        ScheduleUtil.print(testDay,false);
        assertEquals(87, testDay.getCountFreeTimeSlotsInDay());
        assertEquals(9, testDay.getCountBusyTimeSlotsInDay());
        Helper.printDotsSeparator();
    }

    @Tag("init")
    @ParameterizedTest(name ="Находим ближайший слот для времени {0} -> {1}")
    @CsvSource(value = {"10:00, 10:00", "15:05, 15:00", "14:14, 14:00", "20:30, 20:30"})
    @DisplayName("Ищем к какому слоту времени относится заданное время: '10:05' и '10:14'-> '10:00' ")
    void getTimeNearestSlot(String time, String expectation) {
        final Day testDay = getTestDay();
        assertEquals(LocalTime.parse(expectation), testDay.getTimeNearestSlot(LocalTime.parse(time)));
    }

    @Test
    @DisplayName("Получаем дату к которой относится текущая временная сетка.")
    void getDate() {
        LocalDate localDate = LocalDate.now();
        Day testDay = schedule.getOrDefault(LocalDate.now(), new Day(localDate));
        assertEquals(localDate, testDay.getDate());
    }

    @Test
    @Tag("init")
    @DisplayName("Получаем количество свободных слотов времени")
    void getCountFreeTimeSlotsInDay() {
        final Day testDay = getTestDay();
        assertEquals(80, testDay.getCountFreeTimeSlotsInDay());
    }

    @Test
    @Tag("init")
    @DisplayName("Получаем количество занятых слотов времени")
    void getCountBusyTimeSlotsInDay() {
        final Day testDay = getTestDay();
        assertEquals(16, testDay.getCountBusyTimeSlotsInDay());
    }

    @Test
    @DisplayName("Получаем количество занятых слотов времени - пустое расписание")
    void getCountBusyTimeSlotsInDayEmpty() {
        final Day testDay = getTestDay();
        assertEquals(0, testDay.getCountBusyTimeSlotsInDay());
    }

    @Test
    @DisplayName("Получаем количество свободных слотов времени - пустое расписание")
    void getCountFreeTimeSlotsInDayEmpty() {
        final Day testDay = getTestDay();
        assertEquals(96, testDay.getCountFreeTimeSlotsInDay());
    }

    private Day getTestDay() {
        final LocalDate localDate = LocalDate.now();
        return schedule.getOrDefault(localDate, new Day(localDate));
    }
}