package ru.yandex.practicum.kanban.tests.unit_tests.schadule;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.managers.schadule.Schedule;
import ru.yandex.practicum.kanban.managers.schadule.service.BookingSlotsService;
import ru.yandex.practicum.kanban.managers.schadule.service.CheckSlotsService;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckSlotsServiceTest implements TestLogger {
    private static final Schedule schedule = new Schedule();
    private final CheckSlotsService checkSlotsService = new CheckSlotsService(schedule);

    @BeforeAll
    static void setUp() {
        BookingSlotsService bookingSlotsService = new BookingSlotsService(schedule);
        bookingSlotsService.bookTimeSlots(LocalDate.of(2022, 12, 12), LocalTime.of(14, 0), 13);
        bookingSlotsService.bookTimeSlots(LocalDate.of(2022, 12, 13), LocalTime.of(10, 0), 9);
        bookingSlotsService.bookTimeSlots(LocalDate.of(2022, 12, 9), LocalTime.of(0, 0), 95);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022 10:30:15, 3,true",
            "12-12-2022 15:30:15, 3,false",
            "13-12-2022 10:30:15, 6,false",
            "13-12-2022 18:45:15, 6,true",
    })
    void isEnoughTime(String dateTime, int count, boolean expected) {
        final LocalDateTime current = LocalDateTime.parse(dateTime, Helper.formatter);
        final LocalDate localDate = current.toLocalDate();
        final LocalTime localTime = current.toLocalTime();
        final boolean result = checkSlotsService.isEnoughTime(localDate, localTime, count);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022,false",
            "10-12-2022,true",
            "13-12-2022,false",
            "01-12-2022,true",
    })
    void isEmptyDate(String dateTime, boolean expected) {
        final LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        final boolean result = checkSlotsService.isEmptyDate(localDate);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022,false",
            "09-12-2022,true",
            "13-12-2022,false",
    })
    void isFullDate(String dateTime, boolean expected) {
        final LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        final boolean result = checkSlotsService.isFullDate(localDate);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "12-12-2022 10:00:00,false",
            "12-12-2022 15:45:00,true",
            "13-12-2022 10:30:00,true",
            "13-12-2022 18:45:00,false",
    })
    void isTimeBeginFree(String dateTime, boolean expected) {
        final LocalDateTime current = LocalDateTime.parse(dateTime, Helper.formatter);
        final LocalDate localDate = current.toLocalDate();
        final LocalTime localTime = current.toLocalTime();
        final boolean result = checkSlotsService.isTimeBeginFree(localDate, localTime);
        assertEquals(expected, result);
    }
}