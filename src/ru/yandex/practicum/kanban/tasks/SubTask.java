package ru.yandex.practicum.kanban.tasks;

public class SubTask extends Task {
    private final int epicID;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epicID = epic.getTaskID();
    }

    public int getEpicID() {
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
}
