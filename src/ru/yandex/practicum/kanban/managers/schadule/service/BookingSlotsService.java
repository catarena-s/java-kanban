package ru.yandex.practicum.kanban.managers.schadule.service;

import ru.yandex.practicum.kanban.managers.schadule.DaySlots;
import ru.yandex.practicum.kanban.managers.schadule.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.COUNT_SLOTS;
import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.ONE_SLOT_TIME_IN_SCHEDULER;

public class BookingSlotsService {
    private final Schedule schedule;

    public BookingSlotsService(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * бронируем время
     *
     * @param date  - дата
     * @param time  - время начала
     * @param count - количество слотов, которые необходимо забронировать
     */
    public void bookTimeSlots(final LocalDate date, final LocalTime time, final int count) {
        final DaySlots daySlots = schedule.get(date);//getDaySlots(dateTime);
//        LocalTime time = dateTime.toLocalTime();

        mark(count, time, true, daySlots);
        schedule.put(date, daySlots);
        schedule.addToUsedDays(date);
    }

    /**
     * освобождаем забронированное время
     *
     * @param date  - дата
     * @param time  - время начала
     * @param count - количество слотов, которые необходимо освободить
     */
    public void freeTimeSlots(final LocalDate date, final LocalTime time, final int count) {
        final DaySlots daySlots = schedule.get(date);// getDaySlots(date);
//        LocalTime time = dateTime.toLocalTime();

        mark(count, time, false, daySlots);
        schedule.put(date, daySlots);

        if (daySlots.getCountFreeTimeSlotsInDay() == COUNT_SLOTS && !schedule.getUsedDays().isEmpty())
            schedule.removeFromUsedDays(daySlots.getDate());
    }

//    private DaySlots getDaySlots(LocalDate date) {
////        final LocalDate date = dateTime.toLocalDate();
//        final DaySlots daySlots = schedule.get(date);
//        return daySlots;
//    }

    private void mark(final int count, final LocalTime timeBegin, final boolean isMark, DaySlots daySlots) {
//        final LocalTime timeBegin = daySlots.getSlots().containsKey(time) ? time : daySlots.getTimeNearestSlot(time);
        final LocalTime endTime = timeBegin.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);

        daySlots.getSlots().subMap(timeBegin, endTime)
                .entrySet()
                .forEach(f -> f.setValue(isMark));

    }
}
