package ru.yandex.practicum.kanban.managers.schadule;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.schadule.service.BookingSlotsService;
import ru.yandex.practicum.kanban.managers.schadule.service.CheckSlotsService;
import ru.yandex.practicum.kanban.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.ONE_SLOT_TIME_IN_SCHEDULER;

/**
 * Сервис для работы с расписанием расписания.
 */
public class ScheduleService {
    private final Schedule schedule;
    private final CheckSlotsService checkSlotsService;
    private final BookingSlotsService bookingSlotsService;

    public ScheduleService() {
        this.schedule = new Schedule();
        this.checkSlotsService = new CheckSlotsService(schedule);
        this.bookingSlotsService = new BookingSlotsService(schedule);
    }

    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Пробуем занять время под задачу
     *
     * @param task - задача
     * @return - true - если удалось выделить время
     */
    public void takeTimeForTask(Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        if (dateTime == null) return;
        final LocalDate date = dateTime.toLocalDate();

        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
        final int count = getCount(task, beginTime);

        bookingSlotsService.bookTimeSlots(date, beginTime, count);
        if (ScheduleUtil.PRINT_REPORT) ScheduleUtil.print(schedule, date, false);
    }

    /**
     * Проверяем время
     * @param task - задача
     * @return true - если есть свободное время для задачи
     * @throws TaskException
     */
    public boolean checkTime(Task task) throws TaskException {
        final LocalDateTime dateTime = task.getStartTime();
        if (dateTime == null) return false;
        final LocalDate date = dateTime.toLocalDate();
        final LocalDate currentDate = LocalDate.now();

        if (date.isAfter(currentDate.plusYears(1)))
            throw new TaskException("Планировать можно только на год вперед.");

        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());

        if (checkSlotsService.isFullDate(date) ||
                checkSlotsService.isTimeBeginFree(date, beginTime)) {
            throw new TaskException("Время в расписании занято.");
        }
        final int count = getCount(task, beginTime);

        if (checkSlotsService.isEnoughTime(date, beginTime, count)) return true;
        else
            throw new TaskException("Недостаточно свободного временив в расписании.");
    }

    /**
     * Освобождаем время из под задачи
     */
    public void freeTime(final Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        if (dateTime == null) return;
        final LocalDate date = dateTime.toLocalDate();

        if (checkSlotsService.isEmptyDate(date)) return;
        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
        final int count = getCount(task, beginTime);

        bookingSlotsService.freeTimeSlots(date, beginTime, count);
        if (ScheduleUtil.PRINT_REPORT) ScheduleUtil.print(schedule, date, false);
    }

    /**
     * Считаем сколько слотов необходимо выделить на задачу
     */
    private int getCount(Task task, LocalTime beginTime) {
        final LocalTime endTime = task.getEndTime().toLocalTime();
        int duration = task.getDuration();
        int count = duration / ONE_SLOT_TIME_IN_SCHEDULER + 1;
        return beginTime.plusMinutes((long) ONE_SLOT_TIME_IN_SCHEDULER * count)
                .isAfter(endTime) ? count : count + 1;
    }

    /**
     * получаем список дней занятых под задачи
     */
    public List<DaySlots> getBusyDays() {
        return schedule.getDays().entrySet().stream()
                .filter(f -> schedule.getUsedDays().contains(f.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Находим ближайший слот
     *
     * @param time - время начала задачи
     * @return - время ближайшего подходящего слота
     */
    private LocalTime getTimeNearestSlot(LocalTime time) {
        final int hour = time.getHour();
        final int min = time.getMinute();
        //получаем ячейку к которой относится время 10:04 -> 10:00
        return LocalTime.of(hour, min - min % ONE_SLOT_TIME_IN_SCHEDULER);
    }

    /**
     * очистка расписания
     */
    public void freeAllTime() {
        schedule.clear();
    }
}
