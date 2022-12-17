package ru.yandex.practicum.kanban.managers.schadule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.TreeMap;

import static ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil.ONE_SLOT_TIME_IN_SCHEDULER;

public class Day extends TreeMap<LocalTime, Boolean> {
    private static final int COUNT_SLOTS = 24 * 60 / ONE_SLOT_TIME_IN_SCHEDULER;//96
    private final LocalDate date;
    private int countFreeSlots = COUNT_SLOTS;

    public Day(final LocalDate day) {
        this.date = day;
        mark(COUNT_SLOTS, LocalTime.of(0, 0), false);
    }

    public boolean isAllTimeSlotsFree() {
        return countFreeSlots == COUNT_SLOTS;
    }

    public boolean isEnoughTime(final LocalTime time, final int count) {
        final LocalTime beginTime = containsKey(time) ? time : getTimeNearestSlot(time);
        final LocalTime endTime = beginTime.plusMinutes(ONE_SLOT_TIME_IN_SCHEDULER * count);
        return subMap(beginTime, endTime).entrySet().stream().
                allMatch(f -> Boolean.FALSE.equals(f.getValue()));
//        for (int i = 1; i < count; i++) {
//            LocalTime nextTime = beginTime.plusMinutes(ScheduleUtil.ONE_SLOT_SCHEDULER * i);
//
//            if (get(nextTime)) {
//                return false;
//            }
//        }
//        return true;
    }

    public void takeTimeSlots(final LocalTime timeBegin, final int count) {
        mark(count, timeBegin, true);
    }

    public void freeTimeSlots(final LocalTime timeBegin, final int count) {
        mark(count, timeBegin, false);
    }

//    public boolean checkNearestTimeSlot(LocalTime time) {
//        if (containsKey(time)) return get(time);
//        LocalTime timeSlot = getTimeNearestSlot(time);
//        return get(timeSlot);
//    }

    public LocalTime getTimeNearestSlot(LocalTime time) {
        final int hour = time.getHour();
        final int min = time.getMinute();
        //получаем ячейку к которой относится время 10:04 -> 10:00
        return LocalTime.of(hour, min - min % ONE_SLOT_TIME_IN_SCHEDULER);
    }

    private void mark(final int count, final LocalTime timeBegin, final boolean isMark) {
        final LocalTime endTime = timeBegin.plusMinutes(ONE_SLOT_TIME_IN_SCHEDULER * count);
        Optional.of(subMap(timeBegin, endTime))
                .orElse(init(count,timeBegin,isMark)).values()
                .stream().map(f -> f = isMark);
        countFreeSlots += (isMark ? -1 : 1) * count;
    }

    private TreeMap<LocalTime, Boolean> init(int count, LocalTime timeBegin, boolean isMark) {
        for (int i = 0; i < count; i++) {
            final LocalTime time = timeBegin.plusMinutes(ONE_SLOT_TIME_IN_SCHEDULER * i);
            put(time, isMark);
            countFreeSlots = 0;
        }
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCountFreeTimeSlotsInDay() {
        return countFreeSlots;
    }

    public int getCountBusyTimeSlotsInDay() {
        return COUNT_SLOTS - countFreeSlots;
    }

}
