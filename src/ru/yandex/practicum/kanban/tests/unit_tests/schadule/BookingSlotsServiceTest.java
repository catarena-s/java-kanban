package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.managers.schadule.DaySlots;
import ru.yandex.practicum.kanban.managers.schadule.Schedule;
import ru.yandex.practicum.kanban.managers.schadule.service.BookingSlotsService;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingSlotsServiceTest implements TestLogger {
    private final Schedule schedule = new Schedule();
    BookingSlotsService bookingSlotsService = new BookingSlotsService(schedule);

    @BeforeEach
    void setUp() {
        LocalDate localDate = LocalDate.of(2022, 12, 12);
        LocalTime localTime = LocalTime.of(15, 0);
        bookingSlotsService.bookTimeSlots(localDate, localTime, 10);

        localDate = LocalDate.of(2022, 12, 13);
        localTime = LocalTime.of(10, 45);
        bookingSlotsService.bookTimeSlots(localDate, localTime, 9);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022 12:30:15, 3",
            "13-12-2022 14:30:15, 6",
    })
    void bookTimeSlots(String dateTime, int count) {
        int before = schedule.getDays().values().stream()
                .mapToInt(DaySlots::getCountBusyTimeSlotsInDay)
                .sum();
        Helper.printMessage("%d", before);

        LocalDateTime current = LocalDateTime.parse(dateTime, Helper.formatter);
        LocalDate localDate = current.toLocalDate();
        LocalTime localTime = current.toLocalTime();
        bookingSlotsService.bookTimeSlots(localDate, localTime, count);


        int after = schedule.getDays().values().stream()
                .mapToInt(DaySlots::getCountBusyTimeSlotsInDay)
                .sum();
        Helper.printMessage("%d", after);
        assertEquals(before + count, after);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022 15:30:15, 3",
            "13-12-2022 10:30:15, 6",
    })
    void freeTimeSlots(String dateTime, int count) {
        int before = schedule.getDays().values().stream()
                .mapToInt(DaySlots::getCountBusyTimeSlotsInDay)
                .sum();
        Helper.printMessage("%d", before);
        LocalDateTime current = LocalDateTime.parse(dateTime, Helper.formatter);
        LocalDate localDate = current.toLocalDate();
        LocalTime localTime = current.toLocalTime();
        bookingSlotsService.freeTimeSlots(localDate, localTime, count);
        int after = schedule.getDays().values().stream()
                .mapToInt(DaySlots::getCountBusyTimeSlotsInDay)
                .sum();
        Helper.printMessage("%d", after);
        assertEquals(before - count, after);
    }
}