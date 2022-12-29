package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.managers.schadule.DaySlots;
import ru.yandex.practicum.kanban.managers.schadule.Schedule;
import ru.yandex.practicum.kanban.managers.schadule.service.BookingSlotsService;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DaySlotsTest implements TestLogger {
    private final Schedule schedule = new Schedule();

    @BeforeEach
    void setUp(TestInfo info) {
        final LocalDate localDate = LocalDate.now();
        BookingSlotsService bookingSlotsService = new BookingSlotsService(schedule);
        if (info.getTags().contains("init")) {
            final Random rnd = new Random(0);
            rnd.ints()
                    .limit(10)
                    .forEach(f -> {
                        final int hour = Math.abs(f % 24);
                        final int[] min = {0, 15, 30, 45};
                        final Random minInd = new Random(0);
                        final LocalTime time = LocalTime.of(hour, min[minInd.nextInt(4)]);
                        bookingSlotsService.bookTimeSlots(localDate, time, 1);
                    });
            bookingSlotsService.bookTimeSlots(localDate, LocalTime.of(10, 0), 3);
            bookingSlotsService.bookTimeSlots(localDate, LocalTime.of(15, 0), 6);
        }
    }


    @Test
    @Tag("init")
    @DisplayName("Получаем количество свободных слотов времени")
    void getCountFreeTimeSlotsInDay() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(72, testDaySlots.getCountFreeTimeSlotsInDay());
    }

    @Test
    @Tag("init")
    @DisplayName("Получаем количество занятых слотов времени")
    void getCountBusyTimeSlotsInDay() {
        final DaySlots testDaySlots = getTestDay();
        assertEquals(24, testDaySlots.getCountBusyTimeSlotsInDay());
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
        return schedule.get(localDate);
    }
}