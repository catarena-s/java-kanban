package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.Helper;

public class AdditionalTest extends Tester {
    public AdditionalTest(TaskManager taskManager, boolean isPrintHistory) {
        super(taskManager, isPrintHistory);
    }

    public AdditionalTest() {

    }
    private static Epic lastEpic = null;
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {

        run(taskManager,"add", AdditionalTest::insert);

      /*  this.taskManager = taskManager;
        String file = Helper.getFile("add");
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (!line.isBlank()) {
                    if (TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);

                    (Epic) insert(line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }*/
    }

    @Override
    public void runTest(TaskManager taskManager) {
        runTest(taskManager, false);
    }

    protected static Task insert(String line) {
        String[] records = line.split(",");
        Record newData = new Record();
        newData.epicID = lastEpic != null ? lastEpic.getTaskID() : "";
        String[] type = records[1].trim().split("=");

        initNewTask(records, newData);
        try {
            Task newTask = createNewTask(newData, type);
            if (newTask instanceof Epic) return lastEpic = (Epic) newTask;
            else return lastEpic;
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getMessage());
        }
        return null;
    }

    private static void initNewTask(String[] records, Record newdata) {
        for (int i = 2; i < records.length; i++) {
            String[] data = records[i].split("=");
            switch (data[0].trim()) {
                case "name":
                    newdata.name = data[1].trim();
                    break;
                case "description":
                    newdata.description = data[1].trim();
                    break;
                case "epicId": {
                    String[] type = records[1].trim().split("=");
                    if ("SUB_TASK".equals(type[1].toUpperCase().trim())) {
                        newdata.epicID = data[1].trim();
                    }
                    break;
                }
                default:
            }
        }
    }

    private static Task createNewTask(Record newData, String[] type) throws TaskGetterException {
        switch (type[1].toUpperCase().trim()) {
            case "SUB_TASK":
                SubTask subTask = new SubTask(newData.name, newData.description, newData.epicID);
                taskManager.addSubtask(subTask);
                return subTask;
            case "EPIC":
                Epic epic = new Epic(newData.name, newData.description);
                taskManager.addEpic(epic);
                return epic;
            case "TASK":
                Task task = new Task(newData.name, newData.description);
                taskManager.addTask(task);
                return task;
            default:
                return null;
        }
    }

    private static class Record {
        String name = "";
        String description = "";
        String epicID = "";

    }
}
