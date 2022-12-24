package ru.yandex.practicum.kanban.managers.schadule;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.schadule.service.BookingSlotsService;
import ru.yandex.practicum.kanban.managers.schadule.service.CheckSlotsService;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.kanban.managers.schadule.DaySlots.ONE_SLOT_TIME_IN_SCHEDULER;

/**
 * Валидатор расписания.
 */
public class ScheduleValidator {
    //    public Map<LocalDate, DaySlots> getSchedule() {
//        return schedule;
//    }
//
//    /** расписание */
//    private final Map<LocalDate, DaySlots> schedule;
//    /** список дней в которые есть занятое время */
//    private final Set<LocalDate> usedDays = new HashSet<>();
//
//    public ScheduleValidator() {
//        schedule = new HashMap<>();
//    }
    private final Schedule schedule;
    private final CheckSlotsService checkSlotsService;
    private final BookingSlotsService bookingSlotsService;

    public ScheduleValidator() {
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
     * @throws TaskException - оштбка если есть пересечение по времени
     */
    public boolean takeTimeForTask(Task task) throws TaskException {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
//        final LocalDate currentDate = LocalDate.now();
        if (date.isEqual(ChronoLocalDate.from(Helper.MAX_DATE))) {
            return false;
        }

//        if (date.isAfter(currentDate.plusYears(1)))
//            throw new TaskException("Планировать можно только на год вперед.");

//        final DaySlots daySlots = schedule.get(date); /*schedule.getDays()
//                .getOrDefault(date, new DaySlots(date));
//*/
        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
//        LocalTime beginTime = dateTime.toLocalTime();
//        if (!daySlots.getSlots().containsKey(beginTime))
////            beginTime = daySlots.getTimeNearestSlot(beginTime);
//            beginTime = daySlots.getTimeNearestSlot(beginTime);

//        if (Boolean.TRUE.equals(daySlots.getSlots().get(beginTime)))
//            throw new TaskException("Время в расписании занято.");

        final int count = getCount(task, beginTime);

//        if (checkSlotsService.isEnoughTime(date,beginTime, count)) {
            bookingSlotsService.bookTimeSlots(date, beginTime, count);
//        if (daySlots.isEnoughTime(beginTime, count)) {
//            daySlots.takeTimeSlots(beginTime, count);
//            schedule.addUsedDays(date);
//        } else
//            throw new TaskException("Недостаточно свободного временив в расписании.");
//        schedule.put(date, daySlots);

//        if (ScheduleUtil.PRINT_REPORT) {
//            ScheduleUtil.print(daySlots, false);
//            Helper.printSeparator();
//        }
        return true;
    }
    public void takeTime(Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
//        final LocalDate currentDate = LocalDate.now();

        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
        final int count = getCount(task, beginTime);
        bookingSlotsService.bookTimeSlots(date, beginTime, count);
//        if (daySlots.isEnoughTime(beginTime, count)) {
//            daySlots.takeTimeSlots(beginTime, count);
//        schedule.addUsedDays(date);
//        if (ScheduleUtil.PRINT_REPORT) {
//            ScheduleUtil.print(daySlots, false);
//            Helper.printSeparator();
//        }


//        return true;
    }
    public boolean checkDay(Task task) throws TaskException {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final LocalDate currentDate = LocalDate.now();

        if (date.isEqual(ChronoLocalDate.from(Helper.MAX_DATE))) {
            return false;
        }

        if (date.isAfter(currentDate.plusYears(1)))
            throw new TaskException("Планировать можно только на год вперед.");

//        final DaySlots daySlots = schedule.get(date);
//        LocalTime beginTime = dateTime.toLocalTime();

        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
//        if (!daySlots.getSlots().containsKey(beginTime))
//            beginTime = daySlots.getTimeNearestSlot(beginTime);

        if(checkSlotsService.isTimeBeginFree(date,beginTime)){
            throw new TaskException("Время в расписании занято.");
        }
//        if (Boolean.TRUE.equals(daySlots.getSlots().get(beginTime)))
//            throw new TaskException("Время в расписании занято.");
        final int count = getCount(task, beginTime);

        if (checkSlotsService.isEnoughTime(date,beginTime, count)) return true;
        else
            throw new TaskException("Недостаточно свободного временив в расписании.");
    }

    /**
     * Освобождаем время из под задачи
     */
    public void freeTime(final Task task) {
        final LocalDateTime dateTime = task.getStartTime();
        final LocalDate date = dateTime.toLocalDate();
        final Optional<DaySlots> day = Optional.ofNullable(schedule.get(date));

        // if (day.isEmpty()) return;
        if (checkSlotsService.isEmptyDate(date)) return;
        LocalTime beginTime = getTimeNearestSlot(dateTime.toLocalTime());
//        LocalTime beginTime = dateTime.toLocalTime();
//        if (!day.get().getSlots().containsKey(beginTime)) beginTime = day.get().getTimeNearestSlot(beginTime);
        final int count = getCount(task, beginTime);

//        day.get().freeTimeSlots(beginTime, count);
        bookingSlotsService.freeTimeSlots(date, beginTime, count);

//        schedule.put(date, day.get());
//        if (day.get().getCountFreeTimeSlotsInDay() == COUNT_SLOTS && !schedule.getUsedDays().isEmpty())
//            schedule.getUsedDays().remove(day.get().getDate());
        if (ScheduleUtil.PRINT_REPORT) ScheduleUtil.print(day.get(), false);
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
//    public void printDay(Task task, boolean isPrintAll) {
//        final LocalDateTime dateTime = task.getStartTime();
//        final LocalDate date = dateTime.toLocalDate();
//        final DaySlots daySlots = schedule.getOrDefault(date, new DaySlots(date));
//        ScheduleUtil.print(daySlots, isPrintAll);
//    }
}
