package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleValidator {
    private static final int ONE_SLOT_SCHEDULER = 15;
    private static final boolean PRINT_REPORT = false;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final Map<LocalDate, DayOfWeek> schedule;
    private final Set<LocalDate> usedDays = new HashSet<>();

    public ScheduleValidator() {
        schedule = new TreeMap<>();
    }

    public boolean takeTimeForTask(Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        if (date.isEqual(LocalDate.of(2222, 1, 1))) return false;
        if (date.isAfter(date.plusYears(1))) return false;

        final LocalTime time = dateTime.toLocalTime();
        final int count = getCount(task.getDuration());
        final DayOfWeek dayOfWeek = schedule.getOrDefault(date, new DayOfWeek(date));

        if (Boolean.FALSE.equals(dayOfWeek.get(time.format(TIME_FORMATTER)))) {
            if (dayOfWeek.isEnoughTime(time, count)) {
                dayOfWeek.takeTime(time, count);
                usedDays.add(date);
            } else return false;
        }
        schedule.put(date, dayOfWeek);
        if (PRINT_REPORT) {
            print(dayOfWeek, false);
            Helper.printSeparator();
        }
        return true;
    }

    public void freeTime(final Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final int count = getCount(task.getDuration());
        final Optional<DayOfWeek> dayOfWeek = Optional.ofNullable(schedule.get(date));

        if (dayOfWeek.isPresent()) {
            dayOfWeek.get().free(dateTime.toLocalTime(), count);
            schedule.put(date, dayOfWeek.get());
            if (dayOfWeek.get().isFullFree()) usedDays.remove(dayOfWeek);
            if (PRINT_REPORT) print(dayOfWeek.get(), false);
        }
    }

    private int getCount(final int duration) {
        return duration / ONE_SLOT_SCHEDULER + 1;
    }

    public List<DayOfWeek> getBusyTime() {
        return schedule.entrySet().stream()
                .filter(f -> usedDays.contains(f.getKey()))
                .map(f -> f.getValue())
                .collect(Collectors.toList());
    }

    public static void print(DayOfWeek dayOfWeek, boolean isPrintAll) {
        final LocalTime timeBegin = LocalTime.of(0, 0);
        int countPrint = 0;

        Helper.printMessage("Day: %s", dayOfWeek.day.format(DateTimeFormatter.ISO_DATE));
        if (!dayOfWeek.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 4; j++) {
                    final LocalTime time = timeBegin.plusMinutes(ONE_SLOT_SCHEDULER * j + 60 * i);
                    final String currentTime = time.format(TIME_FORMATTER);
                    if (dayOfWeek.get(currentTime)) {
                        countPrint++;
                        System.out.print("\033[1;36m" + currentTime + " - " + true + "\033[0m" + "\t");
                        if (countPrint % 4 == 0 && !isPrintAll) Helper.printEmptySting();
                    } else {
                        if (isPrintAll) {
                            countPrint++;
                            System.out.print(currentTime + " - " + false + "\t");
                        }
                    }
                }
                if (isPrintAll) Helper.printEmptySting();
            }
        }
        Helper.printEmptySting();
    }

    public static class DayOfWeek extends LinkedHashMap<String, Boolean> {
        private static final int COUNT_SLOTS = 24 * 60 / ONE_SLOT_SCHEDULER;//96
        private final LocalDate day;
        private int countFreeSlots = COUNT_SLOTS;

        public DayOfWeek(final LocalDate day) {
            this.day = day;
            mark(COUNT_SLOTS, LocalTime.of(0, 0), false);
        }

        public boolean isFullFree() {
            return countFreeSlots == COUNT_SLOTS;
        }

        public int getCountFreeSlots() {
            return countFreeSlots;
        }

        public int getCountBusySlots() {
            return COUNT_SLOTS - countFreeSlots;
        }

        public LocalDate getDay() {
            return day;
        }

        public boolean isEnoughTime(final LocalTime time, final int count) {
            for (int i = 1; i < count; i++) {
                LocalTime nextTime = time.plusMinutes(ONE_SLOT_SCHEDULER * i);
                if (get(nextTime.format(TIME_FORMATTER))) {
                    return false;
                }
            }
            return true;
        }

        public void takeTime(final LocalTime timeBegin, final int count) {
            mark(count, timeBegin, true);
        }

        public void free(final LocalTime timeBegin, final int count) {
            mark(count, timeBegin, false);
        }

        private void mark(final int count, final LocalTime timeBegin, final boolean isMark) {
            for (int i = 0; i < count; i++) {
                LocalTime time = timeBegin.plusMinutes(ONE_SLOT_SCHEDULER * i);
                put(time.format(TIME_FORMATTER), isMark);
                countFreeSlots += (isMark ? -1 : 1);
            }
        }


    }
}
