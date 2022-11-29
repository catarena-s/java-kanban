package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        List<String> listSubTaskId = new ArrayList<>();
        listSubTaskId.addAll(subTasks.stream().map(s -> s.taskID).collect(Collectors.toList()));
        return "Epic{ " +
                "ID=" + getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subTasks=[" + String.join(", ", listSubTaskId) +
                "] }";
    }

    @Override
    public String toCompactString() {
            String resFormat = Helper.DEFAULT_FORMAT_OUT_DATA + "%n";

            return String.format(resFormat, taskID, TaskType.EPIC, taskStatus, name, description);
         /*return "Task{ "+
                 "ID=" +taskID +
         " }";*/
//        }
//        List<String> listSubTaskId = new ArrayList<>();
//        listSubTaskId.addAll(subTasks.stream().map(s -> s.taskID).collect(Collectors.toList()));
//        return "Epic{ " +
//                "ID=" + taskID +
//                ", subTasks=[" + String.join(", ", listSubTaskId) +
//                "] }";
    }
}
