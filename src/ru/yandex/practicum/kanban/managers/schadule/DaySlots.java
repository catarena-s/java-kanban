package ru.yandex.practicum.kanban.managers.schadule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.TreeMap;

import static ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil.ONE_SLOT_TIME_IN_SCHEDULER;

public class DaySlots extends TreeMap<LocalTime, Boolean> {
    public static final int COUNT_SLOTS = 24 * 60 / ONE_SLOT_TIME_IN_SCHEDULER;//96
    private final LocalDate date;

    public DaySlots(final LocalDate day) {
        this.date = day;
        mark(COUNT_SLOTS, LocalTime.of(0, 0), false);
    }

    public boolean isEnoughTime(final LocalTime time, final int count) {
        final LocalTime beginTime = containsKey(time) ? time : getTimeNearestSlot(time);
        final LocalTime endTime = beginTime.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);
        return subMap(beginTime, endTime).entrySet().stream().
                allMatch(f -> Boolean.FALSE.equals(f.getValue()));
    }

    public void takeTimeSlots(final LocalTime time, final int count) {
        final LocalTime timeBegin = containsKey(time) ? time : getTimeNearestSlot(time);
        mark(count, timeBegin, true);
    }

    public void freeTimeSlots(final LocalTime time, final int count) {
        final LocalTime timeBegin = containsKey(time) ? time : getTimeNearestSlot(time);
        mark(count, timeBegin, false);
    }

    public LocalTime getTimeNearestSlot(LocalTime time) {
        final int hour = time.getHour();
        final int min = time.getMinute();
        //получаем ячейку к которой относится время 10:04 -> 10:00
        return LocalTime.of(hour, min - min % ONE_SLOT_TIME_IN_SCHEDULER);
    }

    private void mark(final int count, final LocalTime timeBegin, final boolean isMark) {
        final LocalTime endTime = timeBegin.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);
        Optional.of(subMap(timeBegin, endTime))
                .orElse(init(count, timeBegin, isMark))
                .values().stream()
                .map(f -> f = isMark);
    }

    private TreeMap<LocalTime, Boolean> init(int count, LocalTime timeBegin, boolean isMark) {
        for (int i = 0; i < count; i++) {
            final LocalTime time = timeBegin.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * i);
            put(time, isMark);
        }
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCountFreeTimeSlotsInDay() {
        return (int) values().stream().filter(f -> !f).count();
    }

    public int getCountBusyTimeSlotsInDay() {
        return (int) values().stream().filter(f -> f).count();
    }

}
