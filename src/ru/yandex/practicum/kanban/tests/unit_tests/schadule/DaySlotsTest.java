package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.managers.schadule.DaySlots;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DaySlotsTest implements TestLogger {
    private final Map<LocalDate, DaySlots> schedule = new HashMap<>();

    @BeforeEach
    void setUp(TestInfo info) {
        final LocalDate localDate = LocalDate.now();
        final DaySlots daySlots = schedule.getOrDefault(localDate, new DaySlots(localDate));
        if (info.getTags().contains("init")) {
            final Random rnd = new Random(0);
            rnd.ints()
                    .limit(10)
                    .forEach(f -> {
                        final int hour = Math.abs(f % 24);
                        final int[] min = {0, 15, 30, 45};
                        final Random minInd = new Random(0);
                        final LocalTime time = LocalTime.of(hour, min[minInd.nextInt(4)]);
                        daySlots.takeTimeSlots(time, 1);
                    });

            daySlots.takeTimeSlots(LocalTime.of(10, 0), 3);
            daySlots.takeTimeSlots(LocalTime.of(15, 0), 6);
        }
        schedule.put(localDate, daySlots);
    }

    @Tag("init")
    @ParameterizedTest(name ="Проверяем: начало задачи {0} нужно {1} слота по 15 мин -> {2}")
    @CsvSource(value = {"10:00, 2, false", "15:00, 2, false", "14:30, 3, false", "09:30, 1, true"})
    @DisplayName("Достаточно ли свободных слотов времени(один слот 15 мин) для заданного времени.")
    void isEnoughTime(String time, String count, String expectation) {
        final DaySlots testDay = getTestDay();
        assertEquals(Boolean.valueOf(expectation), testDay.isEnoughTime(LocalTime.parse(time), Integer.parseInt(count)));
    }

    @Test
    @DisplayName("Бронируем слоты под задачу.")
    void takeTimeSlots() {
        final DaySlots testDaySlots = getTestDay();
        Helper.printMessage("Before:");
        ScheduleUtil.print(testDaySlots,false);

        testDaySlots.takeTimeSlots(LocalTime.of(10,11),2);
        testDaySlots.takeTimeSlots(LocalTime.of(12,22),5);

        Helper.printMessage("After:");
        ScheduleUtil.print(testDaySlots,false);

        assertEquals(89, testDaySlots.getCountFreeTimeSlotsInDay());
        assertEquals(7, testDaySlots.getCountBusyTimeSlotsInDay());
        Helper.printDotsSeparator();
    }

    @Test
    @Tag("init")
    @DisplayName("Освобождаем слоты: если задача удалена, или были обновлены startTime и duration")
    void freeTimeSlots() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(80, testDaySlots.getCountFreeTimeSlotsInDay());
        assertEquals(16, testDaySlots.getCountBusyTimeSlotsInDay());
        Helper.printMessage("Before:");
        ScheduleUtil.print(testDaySlots,false);

        testDaySlots.freeTimeSlots(LocalTime.of(10,11),2);
        testDaySlots.freeTimeSlots(LocalTime.of(15,22),5);
        Helper.printMessage("After:");
        ScheduleUtil.print(testDaySlots,false);
        assertEquals(87, testDaySlots.getCountFreeTimeSlotsInDay());
        assertEquals(9, testDaySlots.getCountBusyTimeSlotsInDay());
        Helper.printDotsSeparator();
    }

    @Tag("init")
    @ParameterizedTest(name ="Находим ближайший слот для времени {0} -> {1}")
    @CsvSource(value = {"10:00, 10:00", "15:05, 15:00", "14:14, 14:00", "20:30, 20:30"})
    @DisplayName("Ищем к какому слоту времени относится заданное время: '10:05' и '10:14'-> '10:00' ")
    void getTimeNearestSlot(String time, String expectation) {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(LocalTime.parse(expectation), testDaySlots.getTimeNearestSlot(LocalTime.parse(time)));
    }

    @Test
    @DisplayName("Получаем дату к которой относится текущая временная сетка.")
    void getDate() {
        LocalDate localDate = LocalDate.now();
        DaySlots testDaySlots = schedule.getOrDefault(LocalDate.now(), new DaySlots(localDate));
        assertEquals(localDate, testDaySlots.getDate());
    }

    @Test
    @Tag("init")
    @DisplayName("Получаем количество свободных слотов времени")
    void getCountFreeTimeSlotsInDay() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(80, testDaySlots.getCountFreeTimeSlotsInDay());
    }

    @Test
    @Tag("init")
    @DisplayName("Получаем количество занятых слотов времени")
    void getCountBusyTimeSlotsInDay() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(16, testDaySlots.getCountBusyTimeSlotsInDay());
    }

    @Test
    @DisplayName("Получаем количество занятых слотов времени - пустое расписание")
    void getCountBusyTimeSlotsInDayEmpty() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(0, testDaySlots.getCountBusyTimeSlotsInDay());
    }

    @Test
    @DisplayName("Получаем количество свободных слотов времени - пустое расписание")
    void getCountFreeTimeSlotsInDayEmpty() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(96, testDaySlots.getCountFreeTimeSlotsInDay());
    }

    private DaySlots getTestDay() {
        final LocalDate localDate = LocalDate.now();
        return schedule.getOrDefault(localDate, new DaySlots(localDate));
    }
}