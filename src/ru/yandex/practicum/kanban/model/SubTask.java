package ru.yandex.practicum.kanban.model;

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
    public String toStringShort(){
        return "SubTask{ "+
                "ID=" +taskID +
                " }";
    }
}
