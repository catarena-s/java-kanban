package ru.yandex.practicum.kanban.managers.schadule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Класс-коллекция, хранящий слоты времени одного дня
 */
public class DaySlots {
    public static final int ONE_SLOT_TIME_IN_SCHEDULER = 15;
    /** количество слотов */
    public static final int COUNT_SLOTS = 24 * 60 / ONE_SLOT_TIME_IN_SCHEDULER;//96
    /** дата к которой относится текущий слот */
    private final LocalDate date;
    private final SortedMap<LocalTime, Boolean> slots = new TreeMap<>();


    public DaySlots(final LocalDate day) {
        this.date = day;
        for (int i = 0; i < COUNT_SLOTS; i++) {
            final LocalTime time = LocalTime.of(0, 0)
                    .plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * i);
            slots.put(time, false);
        }
    }

    public SortedMap<LocalTime, Boolean> getSlots() {
        return slots;
    }

    /**
     * достаточно ли свободных слотов для
     *
     * @param time  - время начала задачи
     * @param count - необходимое количество свободных слотов
     * @return true - если свободных слотов достаточно
     */
//    public boolean isEnoughTime(final LocalTime time, final int count) {
//        final LocalTime beginTime = slots.containsKey(time) ? time : getTimeNearestSlot(time);
//        final LocalTime endTime = beginTime.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);
//        return slots.subMap(beginTime, endTime).entrySet().stream().
//                allMatch(f -> Boolean.FALSE.equals(f.getValue()));
//    }

    /**
     * бронируем время
     *
     * @param time  - время начала
     * @param count - количество слотов, которые необходимо забронировать
     */
//    public void takeTimeSlots(final LocalTime time, final int count) {
//        final LocalTime timeBegin = slots.containsKey(time) ? time : getTimeNearestSlot(time);
//        mark(count, timeBegin, true);
//    }
//
//    /**
//     * освобождаем забронированное время
//     *
//     * @param time  - время начала
//     * @param count - количество слотов, которые необходимо освободить
//     */
//    public void freeTimeSlots(final LocalTime time, final int count) {
//        final LocalTime timeBegin = slots.containsKey(time) ? time : getTimeNearestSlot(time);
//        mark(count, timeBegin, false);
//    }

//    /**
//     * Находим ближайший слот
//     *
//     * @param time - время начала задачи
//     * @return - время ближайшего подходящего слота
//     */
//    public LocalTime getTimeNearestSlot(LocalTime time) {
//        final int hour = time.getHour();
//        final int min = time.getMinute();
//        //получаем ячейку к которой относится время 10:04 -> 10:00
//        return LocalTime.of(hour, min - min % ONE_SLOT_TIME_IN_SCHEDULER);
//    }

//    /**
//     * устанавливаем значение isMark заданное количество слотов начиная с timeBegin
//     *
//     * @param count     - количество слотов которые нужно пометить
//     * @param timeBegin - время начала
//     * @param isMark    - ture / false
//     */
//    private void mark(final int count, final LocalTime timeBegin, final boolean isMark) {
//        final LocalTime endTime = timeBegin.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count);
//        slots.subMap(timeBegin, endTime)
//                .entrySet()
//                .forEach(f -> f.setValue(isMark));
//
//    }

/*
    */
/**
     * Инициализируем коллекцию
     *//*

    private void init() {
        for (int i = 0; i < COUNT_SLOTS; i++) {
            final LocalTime time = LocalTime.of(0, 0)
                    .plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * i);
            slots.put(time, false);
        }
    }
*/


    /**
     * Получаем дату к которой относится коллекция
     *
     * @return
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Получаем количество свободных слотов
     *
     * @return
     */
    public int getCountFreeTimeSlotsInDay() {
        return (int) slots.values().stream().filter(f -> !f).count();
    }

    /**
     * Получаем количество занятых слотов
     *
     * @return
     */
    public int getCountBusyTimeSlotsInDay() {
        return (int) slots.values().stream().filter(f -> f).count();
    }

}