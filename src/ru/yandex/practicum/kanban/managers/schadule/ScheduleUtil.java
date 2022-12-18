package ru.yandex.practicum.kanban.managers.schadule;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleUtil {
    public static final int ONE_SLOT_TIME_IN_SCHEDULER = 15;
    public static final boolean PRINT_REPORT = false;

    private ScheduleUtil() {
    }

    public static void print(final Day day, boolean isPrintAll) {
        Helper.printMessage("Day: %s", day.getDate().format(DateTimeFormatter.ISO_DATE));
        getEntryStream(day, isPrintAll)
                .ifPresent(getStreamConsumer(isPrintAll ? day.size() : day.getCountBusyTimeSlotsInDay()));
    }

    private static Optional<Stream<Map.Entry<LocalTime, Boolean>>> getEntryStream(final Day day, boolean isPrintAll) {
        return isPrintAll ? Optional.ofNullable(day.entrySet().stream())
                            : Optional.of(day.entrySet().stream()
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

    public static void printDay(ScheduleValidator validator, Task task, boolean isPrintAll) {
        validator.printDay(task, isPrintAll);
    }
}
