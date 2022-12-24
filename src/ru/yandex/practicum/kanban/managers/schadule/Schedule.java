package ru.yandex.practicum.kanban.managers.schadule;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Schedule {
    /** расписание */
    private final Map<LocalDate, DaySlots> days;

    /** список дней в которые есть занятое время */
    private final Set<LocalDate> usedDays = new HashSet<>();

    public Schedule() {
        this.days = new HashMap<>();
    }

    public Set<LocalDate> getUsedDays() {
        return usedDays;
    }

    public Map<LocalDate, DaySlots> getDays() {
        return days;
    }


    public void put(final LocalDate date, final DaySlots daySlots) {
        days.put(date, daySlots);
    }

    public DaySlots get(final LocalDate date) {
        return days.getOrDefault(date, new DaySlots(date));
    }

    public void addToUsedDays(final LocalDate day) {
        usedDays.add(day);
    }
    public void removeFromUsedDays(final LocalDate date){
        usedDays.remove(date);
    }
}
