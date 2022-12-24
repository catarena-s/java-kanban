package ru.yandex.practicum.kanban.managers.schadule.service;

import ru.yandex.practicum.kanban.managers.schadule.DaySlots;
import ru.yandex.practicum.kanban.managers.schadule.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.ONE_SLOT_TIME_IN_SCHEDULER;

public class CheckSlotsService {
    private final Schedule schedule;

    public CheckSlotsService(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * достаточно ли свободных слотов для
     *
     * @param time  - время начала задачи
     * @param count - необходимое количество свободных слотов
     * @return true - если свободных слотов достаточно
     */
    public boolean isEnoughTime(final LocalDate date, final LocalTime beginTime, final int count) {
//        final LocalDate date = dateTime.toLocalDate();
        final DaySlots daySlots = schedule.get(date);
//        LocalTime time = dateTime.toLocalTime();


//        final LocalTime beginTime = daySlots.getSlots().containsKey(time) ? time : daySlots.getTimeNearestSlot(time);
        final LocalTime endTime = beginTime.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);
        return daySlots.getSlots().subMap(beginTime, endTime).entrySet().stream().
                allMatch(f -> Boolean.FALSE.equals(f.getValue()));
    }

    public boolean isEmptyDate(final LocalDate date) {
//        final LocalDate date = dateTime.toLocalDate();
        final DaySlots daySlots = schedule.get(date);

        return daySlots.getCountFreeTimeSlotsInDay() == DaySlots.COUNT_SLOTS;
    }

    public boolean isTimeBeginFree(final LocalDate date, final LocalTime beginTime) {
        final DaySlots daySlots = schedule.get(date);

        return daySlots.getSlots().get(beginTime);
    }
}
