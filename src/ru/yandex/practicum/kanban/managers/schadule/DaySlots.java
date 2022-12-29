package ru.yandex.practicum.kanban.managers.schadule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NavigableMap;
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
    private final NavigableMap<LocalTime, Boolean> slots = new TreeMap<>();

    public DaySlots(final LocalDate day) {
        this.date = day;
        for (int i = 0; i < COUNT_SLOTS; i++) {
            final LocalTime time = LocalTime.of(0, 0)
                    .plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * i);
            slots.put(time, false);
        }
    }

    public NavigableMap<LocalTime, Boolean> getSlots() {
        return slots;
    }

    /**
     * Получаем дату к которой относится коллекция
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Получаем количество свободных слотов
     */
    public int getCountFreeTimeSlotsInDay() {
        return (int) slots.values().stream().filter(f -> !f).count();
    }

    /**
     * Получаем количество занятых слотов
     */
    public int getCountBusyTimeSlotsInDay() {
        return (int) slots.values().stream().filter(f -> f).count();
    }

}