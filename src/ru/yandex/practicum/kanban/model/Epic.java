package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {

    private final List<Task> subTasks;
    private LocalDateTime endTime;

    public Epic() {
        super();
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void setDuration() {
        this.duration = subTasks.stream()
                .mapToInt(m -> m.duration)
                .reduce(0, (v, task) -> v += task);

    }

    private void setStartTime() {
        if (!subTasks.isEmpty()) {
            startTime = subTasks.stream()
                    .min(Comparator.comparing(Task::getStartTime))
                    .map(Task::getStartTime).get();
            endTime = subTasks.stream()
                    .max(Comparator.comparing(Task::getStartTime))
                    .map(Task::getEndTime).get();
        } else {
            startTime = Helper.MAX_DATE;
            endTime = null;
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
        refreshEpic();
    }

    public void refreshEpic() {
        updateEpicStatus();
        setDuration();
        setStartTime();
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        List<String> listSubTaskId = subTasks.stream().map(s -> s.taskID).collect(Collectors.toList());
        return "Epic{" +
                super.toString() +
                ", endTime=" + (endTime == null ? "'-', " : endTime) +
                "subTasks=[" + String.join(", ", listSubTaskId) +
                "]" +
                '}';
    }

    private void updateEpicStatus() {
        if (subTasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean isDone = true;
        boolean isNew = true;
        for (Task subTask : subTasks) {
            TaskStatus currentStatus = subTask.getStatus();

            isDone &= (currentStatus == TaskStatus.DONE);
            isNew &= (currentStatus == TaskStatus.NEW);
            boolean isInProgress = (!isDone && !isNew) || (currentStatus == TaskStatus.IN_PROGRESS);

            if (isInProgress) {
                setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
        }
        if (isNew) {
            setStatus(TaskStatus.NEW);
        } else if (isDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toCompactString() {
        return String.format("%-8s, %s", TaskType.EPIC, super.toCompactString());
    }

    @Override
    public String toActualStringFoTest() {
        return String.format("%s, %s", TaskType.EPIC, super.toActualStringFoTest());
    }

}
