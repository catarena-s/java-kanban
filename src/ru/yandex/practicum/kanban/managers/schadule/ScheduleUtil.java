package ru.yandex.practicum.kanban.managers.schadule;

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
    public static final boolean IS_PRINT_ALL = false;

    public static void print(final Day day) {
        Helper.printMessage("Day: %s", day.getDate().format(DateTimeFormatter.ISO_DATE));
        getEntryStream(day).ifPresent(getStreamConsumer());
//            for (int i = 0; i < 24; i++) {
//                for (int j = 0; j < 4; j++) {
//                    final LocalTime time = timeBegin.plusMinutes(ONE_SLOT_TIME_IN_SCHEDULER * j + 60 * i);
//                    final String currentTime = time.format(TIME_FORMATTER);
//                    if (day.get(time)) {
//                        System.out.print("\033[1;36m" + currentTime + " - " + true + "\033[0m" + "\t");
//                        if (i==3 && !isPrintAll) Helper.printEmptySting();
//                    }
//                }
//            }
//        } else {
//            getEntryStream(day,f -> Boolean.TRUE.equals(f.getValue()))
//                    .ifPresent(getStreamConsumer(countPrint, IS_PRINT_ALL ));
//        }
//        if (!day.isEmpty()) {
//            for (int i = 0; i < 24; i++) {
//                for (int j = 0; j < 4; j++) {
//                    final LocalTime time = timeBegin.plusMinutes(ONE_SLOT_TIME_IN_SCHEDULER * j + 60 * i);
//                    final String currentTime = time.format(TIME_FORMATTER);
//                    if (day.get(time)) {
//                        countPrint++;
//                        System.out.print("\033[1;36m" + currentTime + " - " + true + "\033[0m" + "\t");
//                        if (countPrint % 4 == 0 && !isPrintAll) Helper.printEmptySting();
//                    } else {
//                        if (isPrintAll) {
//                            countPrint++;
//                            System.out.print(currentTime + " - " + false + "\t");
//                        }
//                    }
//                }
//                if (isPrintAll) Helper.printEmptySting();
//            }
//        }
        Helper.printEmptySting();
    }

    private static Optional<Stream<Map.Entry<LocalTime, Boolean>>> getEntryStream(final Day day) {
        return IS_PRINT_ALL ? Optional.ofNullable(day.entrySet().stream())
                : Optional.of(day.entrySet().stream()
                .filter(f -> Boolean.TRUE.equals(f.getValue())));
    }

    private static Consumer<Stream<Map.Entry<LocalTime, Boolean>>> getStreamConsumer() {
        final AtomicInteger countPrint = new AtomicInteger();
        return t -> t.collect(Collectors.toList())
                .forEach(f -> {
                    countPrint.getAndIncrement();
                    final String timeSlot = f.getKey() + " - " + f.getValue();
                    if (Boolean.TRUE.equals(f.getValue()))
                        System.out.print("\033[1;36m" + timeSlot + "\033[0m" + "\t");
                    else System.out.print(timeSlot + "\t");
                    if (countPrint.get() % 4 == 0) Helper.printEmptySting();
                });
    }

}
