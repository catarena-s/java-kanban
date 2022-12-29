package ru.yandex.practicum.kanban.managers.schadule;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleUtil {
    public static final boolean PRINT_REPORT = false;

    private ScheduleUtil() {
    }

    public static void print(final Schedule schedule, final LocalDate date, boolean isPrintAll) {
        final DaySlots daySlots = schedule.get(date);
        print(daySlots, isPrintAll);
    }

    public static void print(final DaySlots daySlots, boolean isPrintAll) {
        Helper.printMessage("Day: %s", daySlots.getDate().format(DateTimeFormatter.ISO_DATE));
        getEntryStream(daySlots, isPrintAll)
                .ifPresent(getStreamConsumer(isPrintAll ? daySlots.getSlots().size() : daySlots.getCountBusyTimeSlotsInDay()));
    }

    public static void printDay(final ScheduleService validator, final Task task, boolean isPrintAll) {
        if (task.getStartTime() == null) return;
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final DaySlots daySlots = validator.getSchedule().get(date);
        ScheduleUtil.print(daySlots, isPrintAll);
    }

    private static Optional<Stream<Map.Entry<LocalTime, Boolean>>> getEntryStream(final DaySlots daySlots, boolean isPrintAll) {
        return isPrintAll ? Optional.ofNullable(daySlots.getSlots().entrySet().stream())
                : Optional.of(daySlots.getSlots().entrySet().stream()
                .filter(f -> Boolean.TRUE.equals(f.getValue())));
    }

    private static Consumer<Stream<Map.Entry<LocalTime, Boolean>>> getStreamConsumer(int size) {
        final AtomicInteger countPrint = new AtomicInteger();
        return t -> t.collect(Collectors.toList())
                .forEach(f -> {
                    countPrint.getAndIncrement();
                    final String timeSlot = f.getKey() + " - " + f.getValue();
                    if (Boolean.TRUE.equals(f.getValue()))
                        Helper.print("\033[1;36m" + timeSlot + "\033[0m" + "\t");
                    else Helper.print(timeSlot + "\t");
                    if (countPrint.get() % 4 == 0 || countPrint.get() == size) Helper.printEmptySting();
                });
    }
}
