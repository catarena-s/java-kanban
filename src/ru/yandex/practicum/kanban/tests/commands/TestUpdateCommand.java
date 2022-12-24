package ru.yandex.practicum.kanban.tests.commands;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.tests.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;


public class TestUpdateCommand extends AbstractTest {
    public TestUpdateCommand() {
    }

    public TestUpdateCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.UPDATE, this::update);
    }

    protected void update(String line) {
        try {
            executeString(line, taskManager, true);
        } catch (TaskGetterException ex) {
            Helper.printMessage(Colors.RED, ex.getDetailMessage());
        }
    }

    public static Task executeString(String line, TaskManager taskManager, boolean isPrint) throws TaskGetterException {
        String[] records = line.split(",");
        TaskType type = TaskType.valueOf(records[1].toUpperCase().trim());
//        Task task = type.create();
        String[] dataId = records[2].split("=");
        String id = dataId[1].trim();
        Task task = getTask(type, id, taskManager);
        if (task != null) {
            if (isPrint) Helper.printMessage("Task before update: %s", task.toCompactString());
            updateData(records, task, taskManager);
        }
        return task;
    }

    private static Task getTask(TaskType type, String id, TaskManager taskManager) throws TaskGetterException {
//        Task task = type.create();
        Task current;
        switch (type) {
            case TASK:
                current= taskManager.getTask(id); break;
            case SUB_TASK:
                current= taskManager.getSubtask(id); break;
            case EPIC:
                current= taskManager.getEpic(id);break;
            default:return null;
        }
//        task.builder().taskId(current.getTaskID())
//                .name(current.getName())
//                .description(current.getDescription());
        if(current instanceof Updatable){
            ((Updatable)current).updateStartTime(current.getStartTime().format(Helper.formatter))                    ;
            ((Updatable)current).updateDuration(current.getDuration());
            ((Updatable)current).updateStatus(current.getStatus());
        }
        if(current instanceof SubTask){
            ((SubTask)current).builder().epic(((SubTask) current).getEpicID());
        }
        return current;
    }

    private static void updateData(String[] records, Task task, TaskManager taskManager) {
        for (int i = 3; i < records.length; i++) {
            String[] data = records[i].split("=");
            setNewDataTask(task, data);
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskGetterException e) {
            Helper.printMessage(Colors.RED, e.getDetailMessage());
        } catch (Exception e) {
            Helper.printMessage(Colors.RED, e.getMessage());
        }
    }

    private static void setNewDataTask(Task task, String[] data) {
        switch (data[0].trim()) {
            case "name":
                task.builder().name(data[1].trim());
                break;
            case "status":
                TaskStatus newStatus = TaskStatus.valueOf(data[1].toUpperCase().trim());
                if(task instanceof Updatable){
                    ((Updatable)task).updateStatus(newStatus);
                }
                break;
            case "description":
                task.builder().description(data[1].trim());
                break;
            case "duration":
                if(task instanceof Updatable){
                    ((Updatable)task).updateDuration(Integer.parseInt(data[1].trim().isBlank() ? "0" : data[1].trim()));
                }
                break;
            case "start_data":
                if(task instanceof Updatable){
                    ((Updatable)task).updateStartTime(data[1].trim());
                }
                break;
            default:
                break;
        }
    }
}
