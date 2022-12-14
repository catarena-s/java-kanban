package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public class TestCloneCommand extends AbstractTest {
    public TestCloneCommand(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    public TestCloneCommand() {
    }

    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.CLONE, this::clone);
    }

    protected void clone(String line) {
        String[] records = line.split(",");
        if (records.length < 3) return;

        TaskType type = TaskType.valueOf(records[1].trim().toUpperCase());
        try {
            for (int i = 2; i < records.length; i++) {
                Task task = getTask(records[i], type);
                if (task != null) {
                    task = taskManager.clone(task);
                    Helper.printMessage("Cloned %s", task);
                }
            }

        } catch (TaskGetterException | TaskAddException e) {
            Helper.printMessage(Colors.RED, e.getDetailMessage());
        }
    }
/*    public static Task executeString(String line, TaskManager taskManager){
        String[] records = line.split(",");
        if (records.length < 3) return null;
        TaskType type = TaskType.valueOf(records[1].trim().toUpperCase());
        for (int i = 2; i < records.length; i++) {
            Task task = getTask(records[i], type);
            if (task != null) {
                task = taskManager.clone(task);
                Helper.printMessage("Cloned %s", task);
            }
        }
    }*/
    private Task getTask(String id, TaskType type) throws TaskGetterException {
        switch (type) {
            case TASK: {
                return taskManager.getTask(id.trim());
            }
            case EPIC: {
                return taskManager.getEpic(id.trim());
            }
            case SUB_TASK: {
                return taskManager.getSubtask(id.trim());
            }
        }
        return null;
    }

}
