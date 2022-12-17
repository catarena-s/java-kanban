package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
//        int durationSubtasks = 0;
//        for (Task subTask : subTasks) {
//            durationSubtasks += subTask.duration;
//        }
        int durationSubtasks = subTasks.stream()
                .mapToInt(m -> m.duration)
                .reduce(0, (v, task) -> v += task);

        this.duration = durationSubtasks;
    }

    private void setStartTime() {
        if (!subTasks.isEmpty()) {
            Optional<List<Task>> subTasksWithStartTime = Optional.ofNullable(subTasks.stream()
                    .filter(f -> f.getStartTime() != LocalDateTime.of(2222, 1, 1, 0, 0))
                    .collect(Collectors.toList()));
            subTasksWithStartTime.ifPresent(tasks -> {
                        startTime = tasks.stream()
                                .sorted(Comparator.comparing(Task::getStartTime))
                                .findFirst().get().getStartTime();
                        endTime = tasks.stream()
                                .sorted(Comparator.comparing(Task::getStartTime).reversed())
                                .findFirst().get().getEndTime();
                    }
            );
//            startTime = subTasks.stream()
//                    .sorted(Comparator.comparing(Task::getStartTime))
//                    .findFirst()
//                    .orElseThrow().getStartTime();
//
//            SubTask subTask = (SubTask) subTasks.stream()
//                    .sorted(Comparator.comparing(Task::getStartTime).reversed())
//
//                    .findFirst().orElseThrow();
//            endTime = subTask.getStartTime().plusMinutes(subTask.getDuration());
        } else {
            startTime = LocalDateTime.of(2222, 1, 1, 0, 0);
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
//        return "Epic{ " +
//                "ID=" + getTaskID() +
//                ", name='" + getName() + '\'' +
//                ", description='" + getDescription() + '\'' +
//                ", status=" + getStatus() +
//                ", subTasks=[" + String.join(", ", listSubTaskId) +
//                "] }";
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
