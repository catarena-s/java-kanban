package ru.yandex.practicum.kanban.tasks;

public class SubTask extends Task {
    private final int epicID;
    public SubTask(Integer taskID, String name, String description, Epic epic) {
        super(taskID, name, description);
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
