package ru.yandex.practicum.kanban.managers.schadule;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.COUNT_SLOTS;

public class ScheduleValidator {
    private final Map<LocalDate, DaySlots> schedule;
    private final Set<LocalDate> usedDays = new HashSet<>();

    public ScheduleValidator() {
        schedule = new HashMap<>();
    }

    public boolean takeTimeForTask(Task task) throws TaskException {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final LocalDate currentDate = LocalDate.now();
        if (date.isEqual(ChronoLocalDate.from(Helper.MAX_DATE))) {
            return false;
        }
        if (date.isAfter(currentDate.plusYears(1))) throw new TaskException("Планировать можно только на год вперед.");
        final DaySlots daySlots = schedule.getOrDefault(date, new DaySlots(date));

        LocalTime beginTime = dateTime.toLocalTime();
        if (!daySlots.containsKey(beginTime)) beginTime = daySlots.getTimeNearestSlot(beginTime);

        if (Boolean.TRUE.equals(daySlots.get(beginTime)))
            throw new TaskException("Время в расписании занято.");

        final int count = getCount(task, beginTime);

        if (daySlots.isEnoughTime(beginTime, count)) {
            daySlots.takeTimeSlots(beginTime, count);
            usedDays.add(date);
        } else
            throw new TaskException("Недостаточно свободного временив в расписании.");
        schedule.put(date, daySlots);

        if (ScheduleUtil.PRINT_REPORT) {
            ScheduleUtil.print(daySlots, false);
            Helper.printSeparator();
        }
        return true;
    }


    public void freeTime(final Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final Optional<DaySlots> day = Optional.ofNullable(schedule.get(date));

        if (day.isEmpty()) return;

        LocalTime beginTime = dateTime.toLocalTime();
        if (!day.get().containsKey(beginTime)) beginTime = day.get().getTimeNearestSlot(beginTime);
        final int count = getCount(task, beginTime);

        day.get().freeTimeSlots(beginTime, count);
        schedule.put(date, day.get());
        if (day.get().getCountFreeTimeSlotsInDay() == COUNT_SLOTS && !usedDays.isEmpty()) usedDays.remove(day);
        if (ScheduleUtil.PRINT_REPORT) ScheduleUtil.print(day.get(), false);
    }

    private int getCount(Task task, LocalTime beginTime) {
        final LocalTime endTime = task.getEndTime().toLocalTime();
        int duration = task.getDuration();
        int count = duration / ScheduleUtil.ONE_SLOT_TIME_IN_SCHEDULER + 1;
        return beginTime.plusMinutes((long) ScheduleUtil.ONE_SLOT_TIME_IN_SCHEDULER * count)
                .isAfter(endTime) ? count : count + 1;
    }

    public List<DaySlots> getBusyDays() {
        return schedule.entrySet().stream()
                .filter(f -> usedDays.contains(f.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void printDay(Task task, boolean isPrintAll) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final DaySlots daySlots = schedule.getOrDefault(date, new DaySlots(date));
        ScheduleUtil.print(daySlots, isPrintAll);
    }
}
