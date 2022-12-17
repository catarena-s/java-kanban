package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleValidator;
import ru.yandex.practicum.kanban.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<TaskType, Map<String, Task>> tasksByType;
    protected final TreeSet<Task> prioritized;
    protected final HistoryManager historyManager;
    private final ScheduleValidator schedule;
    private int lastID = 0;
    private final List<String> allId = new ArrayList<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;

        tasksByType = new EnumMap<>(TaskType.class);
        prioritized = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        schedule = new ScheduleValidator();
    }

    @Override
    public void add(Task task) throws TaskException {
        if (task instanceof SubTask) {
            addSubtask((SubTask) task);
        } else if (task instanceof Epic) {
            addEpic((Epic) task);
        } else {
            addTask(task);
        }
    }

    private void addTask(Task task) throws TaskException {
        add(task, TaskType.TASK);
    }

    private void addEpic(Epic task) throws TaskException {
        add(task, TaskType.EPIC);
    }

    private void addSubtask(SubTask task) throws TaskException {
        if (task.getEpicID().isBlank()) {
            throw new TaskAddException("Нельзя добавить subtask - не указан id эпика.");
        }
        Epic epic = (Epic) getEpic(task.getEpicID());
        if (epic == null) {
            throw new TaskAddException("Нельзя добавить subtask - Эпик с id=%s отсутвует", task.getEpicID());
        }
        add(task, TaskType.SUB_TASK);
    }

    private void add(Task task, TaskType type) throws TaskException {
        if (!task.getTaskID().isBlank() && allId.contains(task.getTaskID())) {
            throw new TaskAddException("%s с id=%s уже существует.", type.getValue(), task.getTaskID());
        }

        addTaskToTaskManager(task);
        if (TaskType.SUB_TASK.equals(task.getType())) {
            subtaskToEpic((SubTask) task);
        }
        historyManager.add(task);
    }

    /**
     * добавляем задачу в таск-менеожер
     */
    protected void addTaskToTaskManager(final Task task) throws TaskException {
        checkTimeInScheduler(task, false);

        final TaskType taskType = task.getType();
        final Map<String, Task> tasks = tasksByType.getOrDefault(taskType, new HashMap<>());
        if (task.getTaskID().isBlank()) {
            task.builder().taskId(newTaskID());
        } else {
            final int current = Integer.parseInt(task.getTaskID());
            if (lastID < current) lastID = current;
        }
        tasks.put(task.getTaskID(), task);
        tasksByType.put(taskType, tasks);
        allId.add(task.getTaskID());

        if (filterStartTimeOff(task)) return;

        changePrioritizedList(task);
    }

    private void changePrioritizedList(final Task task) {
        if (filterStartTimeOff(task) && prioritized.contains(task)) prioritized.remove(task);
        else
            prioritized.add(task);
    }

    private void checkTimeInScheduler(final Task task, final boolean isUpdate) throws TaskException {
        if (filterStartTimeOff(task)) return;
        if (TaskType.EPIC.equals(task.getType())) return;
        if (isUpdate) {
            final Task taskFromTM = getById(task.getTaskID());
            final LocalDateTime oldStartDate = taskFromTM.getStartTime();
            if (oldStartDate.equals(task.getStartTime())) return;
            schedule.freeTime(taskFromTM);
        }
        schedule.takeTimeForTask(task);
        changePrioritizedList(task);
    }

    protected void subtaskToEpic(final SubTask task) throws TaskGetterException {
        final Epic epic = (Epic) getEpic(task.getEpicID());
        epic.addSubtask(task);
        epic.refreshEpic();
    }

    /**
     * Генерация нового Id
     */
    private String newTaskID() {
        return String.format("%04d", ++lastID);
    }

    @Override
    public Task clone(Task task) throws TaskException {
        Task newTask;
        if (task instanceof SubTask) {
            newTask = new SubTask(task.getName(), task.getDescription(), ((SubTask) task).getEpicID());
        } else if (task instanceof Epic) {
            newTask = new Epic(task.getName(), task.getDescription());
        } else {
            newTask = new SimpleTask(task.getName(), task.getDescription());
        }
        add(newTask);
        if (task instanceof Epic) {
            for (Task subTask : ((Epic) task).getSubTasks()) {
                SubTask newSubtask = new SubTask(subTask.getName(), subTask.getDescription(), newTask.getTaskID());
                add(newSubtask);
            }
        }
        return newTask;
    }

    @Override
    public List<Task> getAllSubtaskFromEpic(Epic epic) {
        List<Task> subTasks = epic.getSubTasks();
        subTasks.forEach(historyManager::add);
        return subTasks;
    }

    @Override
    public List<Task> getAll() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getOptionalList(TaskType.TASK).orElse(new ArrayList<>()));
        allTasks.addAll(getOptionalList(TaskType.SUB_TASK).orElse(new ArrayList<>()));
        allTasks.addAll(getOptionalList(TaskType.EPIC).orElse(new ArrayList<>()));

        return allTasks;
    }

    private Optional<Collection<Task>> getOptionalList(TaskType type) {
        return Optional.of(Optional.ofNullable(tasksByType.get(type))
                .orElse(new HashMap<>()).values());
    }

    @Override
    public List<Task> getAllTasks() {
        return getAllByType(TaskType.TASK);
    }

    @Override
    public List<Task> getAllEpics() {
        return getAllByType(TaskType.EPIC);
    }

    @Override
    public List<Task> getAllSubTasks() {
        return getAllByType(TaskType.SUB_TASK);
    }

    @Override
    public List<Task> getPrioritizedTasks(TaskType type) {
        List<Task> list = new ArrayList<>();
        list.addAll(new ArrayList<>(prioritized));
        list.addAll(getOptionalList(type).orElse(new ArrayList<>())
                .stream()
                .filter(this::filterStartTimeOff)
                .collect(Collectors.toList()));

        return list;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> list = new ArrayList<>();
        list.addAll(prioritized.stream().collect(Collectors.toList()));
        list.addAll(this.getAll().stream()
                .filter(f -> filterStartTimeOff(f))
                .collect(Collectors.toList())
        );
        return prioritized.stream().collect(Collectors.toList());
    }

    /**
     * проверяем установлено ли значение в startTime
     *
     * @param task
     * @return
     */
    private boolean filterStartTimeOff(Task task) {
        return task.getStartTime()
                .equals(LocalDateTime.of(2222, 1, 1, 0, 0));
    }

    @Override
    public Task getById(String taskID) throws TaskGetterException {
        for (Map.Entry<TaskType, Map<String, Task>> map : tasksByType.entrySet()) {
            if (map.getValue().containsKey(taskID)) {
                Task task = map.getValue().get(taskID);
                historyManager.add(task);
                return task;
            }
        }
        throw new TaskGetterException("Задача с id=%s не найдена.", taskID);
    }

    @Override
    public Task getTask(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.TASK);
    }

    @Override
    public Task getEpic(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.EPIC);
    }

    @Override
    public Task getSubtask(String taskID) throws TaskGetterException {
        return getByIdAndType(taskID, TaskType.SUB_TASK);
    }

    protected List<Task> getAllByType(TaskType taskType) {
        return new ArrayList<>(getOptionalList(taskType).orElse(new ArrayList<>()));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task) throws TaskException {
        if (task == null) return;

        checkTimeInScheduler(task, true);

        Map<String, Task> taskByType = tasksByType.get(task.getType());

        if (task instanceof SubTask) {
            Epic epic = (Epic) getEpic(((SubTask) task).getEpicID());
            epic.refreshEpic();
        }
        taskByType.put(task.getTaskID(), task);
    }

    @Override
    public void clear() {
        tasksByType.clear();
        historyManager.clear();
        lastID = 0;
    }

    @Override
    public void removeAllEpics() throws TaskGetterException {
        removeAllTasks(TaskType.EPIC);
    }

    @Override
    public void removeAllTasks() throws TaskGetterException {
        removeAllTasks(TaskType.TASK);
    }

    @Override
    public void removeAllSubtasks() throws TaskGetterException {
        removeAllTasks(TaskType.SUB_TASK);
    }

    @Override
    public void removeTask(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.TASK);
    }

    @Override
    public void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.EPIC);
    }

    @Override
    public void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException {
        remove(taskID, TaskType.SUB_TASK);
    }

    private void removeAllTasks(TaskType taskType) throws TaskGetterException {
        if (!tasksByType.containsKey(taskType)) return;

        if (taskType == TaskType.SUB_TASK) {
            Map<String, Task> subTasks = tasksByType.get(taskType);
            for (Task subTask : subTasks.values()) {
                Epic epic = (Epic) getEpic(((SubTask) subTask).getEpicID());
                epic.getSubTasks().remove(subTask);
                historyManager.remove(subTask.getTaskID());
            }
        }
        if (taskType == TaskType.EPIC) {
            Map<String, Task> epics = tasksByType.get(taskType);
            if (epics.isEmpty()) return;
            for (Task epic : epics.values()) {
                removeAllSubtasksForEpic((Epic) epic);
                historyManager.remove(epic.getTaskID());

            }
        }
        tasksByType.remove(taskType);
    }

    private void removeAnyById(String taskID) throws TaskGetterException {
        for (Map.Entry<TaskType, Map<String, Task>> entry : tasksByType.entrySet()) {
            Map<String, Task> tasks = entry.getValue();
            if (tasks.isEmpty()) continue;
            if (tasks.containsKey(taskID)) {
                remove(taskID, entry.getKey(), tasks);
                return;
            }
        }
    }

    private void remove(String taskID, TaskType taskType) throws TaskRemoveException, TaskGetterException {
        Map<String, Task> tasks = Optional.ofNullable(tasksByType.get(taskType))
                .orElseThrow(() -> new TaskRemoveException("%s c id=%s не найден\n", taskType.getValue(), taskID));
        if (!tasks.containsKey(taskID))
            throw new TaskRemoveException("%s c id=%s не найден\n", taskType.getValue(), taskID);

        remove(taskID, taskType, tasks);
    }

    private void remove(String taskID, TaskType taskType, Map<String, Task> tasks) throws TaskGetterException {
        if (taskType == TaskType.EPIC) {
            Epic epic = (Epic) tasks.get(taskID);
            removeAllSubtasksForEpic(epic);
        }
        if (taskType == TaskType.SUB_TASK) {
            SubTask subTask = (SubTask) tasks.get(taskID);
            Epic epic = (Epic) getEpic(subTask.getEpicID());
            epic.getSubTasks().remove(subTask);
            epic.refreshEpic();
        }
        if (filterStartTimeOff(getById(taskID)))
            prioritized.remove(tasks.get(taskID));
        schedule.freeTime(tasks.get(taskID));
        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    private void removeAllSubtasksForEpic(Epic epic) {
        epic.getSubTasks()
                .forEach(t -> {
                    historyManager.remove(t.getTaskID());
                    getOptionalList(TaskType.SUB_TASK)
                            .orElse(new ArrayList<>()).remove(t);
                });

        epic.getSubTasks().clear();
    }

    private Task getByIdAndType(String taskID, TaskType type) throws TaskGetterException {
        final Optional<Task> t = Optional.ofNullable(tasksByType.get(type))
                .orElseThrow(() -> new TaskGetterException("%s - отсутствуют ", type.getValue()))
                .values()
                .stream()
                .filter(f -> f.getTaskID().equals(taskID))
                .findFirst();

        t.ifPresent(historyManager::add);

        return t.orElseThrow(() -> new TaskGetterException("%s c id =%s не найдена ", type.getValue(), taskID));
    }
}
