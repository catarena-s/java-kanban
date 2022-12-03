package ru.yandex.practicum.kanban.model;

public class SubTask extends Task {
    private String epicID;

    public SubTask() {
        super();
        this.epicID = "0";
    }

    public SubTask(String name, String description, String epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public void init(String... args) {
        super.init(args);
        epicID = args[3];
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
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
        String resFormat = DEFAULT_FORMAT_OUT_DATA + "%6s %n";

        return String.format(resFormat, taskID, TaskType.SUB_TASK, taskStatus, name, description,epicID);
    }
}
