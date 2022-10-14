package ru.yandex.practicum.kanban.tasks;

public class SubTask extends Task {
    private Epic epic;
    public SubTask(Integer taskID, String name, String description, Epic epic) {
        super(taskID, name, description);
        this.epic = epic;
    }
    public Epic getEpic() {
        return epic;
    }
    @Override
    public String toString() {
        return "SubTask{ " +
                "ID=" + getTaskID() +
                ", epicID=" + epic.getTaskID() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                " }";
    }
}
