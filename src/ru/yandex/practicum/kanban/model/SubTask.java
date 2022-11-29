package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.utils.Helper;

public class SubTask extends Task {
    private final String epicID;

    public SubTask(String name, String description, String epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public String getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "SubTask{ " +
                "ID=" + getTaskID() +
                ", epicID=" + epicID +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                " }";
    }
    @Override
    public String toCompactString(){
        String resFormat = Helper.DEFAULT_FORMAT_OUT_DATA + "%6s %n";

        return String.format(resFormat, taskID, TaskType.SUB_TASK, taskStatus, name, description,epicID);
//        return "SubTask{ "+
//                "ID=" +taskID +
//                " }";
    }
}
