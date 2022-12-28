package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

import java.io.IOException;
import java.nio.file.Path;

public class TestHelper {
    public static final String ERROR_MSG_TASK_NOT_FOUND = "Задача не найдена.";
    public static final String ERROR_MSG_TASK_NOT_EQUALS = "Задачи не совпадают.";
    public static final String ERROR_MASSAGES_NOT_EQUALS = "Сообщения об ошибке не совпадают";
    public static final String INFO_FORMATTER = "Info: %s";
    public static final String AFTER_TEST_MSG = "After test:";
    public static final String BEFORE_TEST_MSG = "Before test:";
    public static final String PATH_TEST_FILES = "src/ru/yandex/practicum/kanban/tests/test_data/work/";
    public static final String DATA_FILE_NAME = "manager_data.csv";
    public static final String DATA_FILE_NAME_EMPTY = "fileManager/manager_data_empty.csv";
    public static final String DATA_UPDATE_FILE = "update.csv";
    public static final String DATA_UPDATE_EPIC_STATUS = "update_epic_status.csv";
    public static final String TEST_REMOVE_SUBTASKS = "removeSubtask.csv";
    public static final String INIT_TEST_DATA = "_init_Test_data_without_errors.csv";
    public static final String TEST_ADD_TO_MANAGER = "additional.csv";
    public static final String TEST_ADD_TO_MANAGER_SWAP_TEST = "additional_swapTest.csv";
    public static final String TEST_ADD_TO_MANAGER_WRONG_DATA = "additional_with_wrong_data.csv";
    public static final String DATA_FILE_NAME_EMPTY_EPIC = "fileManager/manager_data_empty_epic.csv";
    public static final String DATA_FILE_NAME_SUBTASK_WITHOUT_EPIC = "manager_data_subtask_without_epic.csv";
    public static final String DATA_FILE_NAME_EMPTY_HISTORY = "fileManager/manager_data_empty_history.csv";
    public static final String ERROR_DATA_FILE_NAME = "manager_data_error.csv";

    private TestHelper() {
    }

    public static Path getPath(String fileName) {
        return Path.of(PATH_TEST_FILES + fileName);
    }

    public static String getPathString(String fileName) {
        return PATH_TEST_FILES + fileName;
    }

    /**
     * Получаем из файла с тестовыми данными expected, если они там есть
     *
     * @param line
     * @return
     */
    public static String getExpectation(String line) {
        if (!line.contains("[")) return "";
        String[] records = line.split("\\[");
        for (String value : records) {
            if (!value.contains("expectation")) continue;
            String[] data = value.split("->");
            return data[1].trim();
        }
        return "";
    }

    /**
     * получаем тип команды(add,update,remove)
     *
     * @return
     */
    public static String getCommand(String line) {
        String[] records = line.split(",");
        return records[0].trim();
    }

    /**
     * Дабавление данных из тестового файла
     */
    public static void addDataFromFile(TaskManager taskManager, String pathString) throws TaskException, IOException {
        String[] testLines = FileHelper.readFromFileToArray(getPath(pathString));
        for (int i = 0; i < testLines.length; i++) {
            String line = testLines[i];
            if (!line.isBlank()) {
                final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                TestAddCommand.executeString(testLine, taskManager);
            }
        }
    }

    public static void printTaskManager(String x, TaskManager taskManager) {
        Helper.printMessage(Colors.UNDERLNE, x);
        TaskPrinter.printAllTaskManagerList(taskManager);
    }

    public static class Expectations {
        public String status;
        public int duration = 0;
        public String startTime;
        public String endTime;
        public boolean isNullEndTime = false;

    }

    public static Expectations parse(String value) {
        String[] split = value.split(";");
        Expectations expectations = new Expectations();
        for (String str : split) {
            String[] data = str.split("=");
            switch (data[0].trim().toLowerCase()) {
                case "status":
                    expectations.status = data[1].trim();
                    break;
                case "duration":
                    expectations.duration = Integer.parseInt(data[1].trim());
                    break;
                case "start_time":
                    expectations.startTime = data[1].trim();
                    break;
                case "end_time":
                    if (data[1].trim().equalsIgnoreCase("null")) expectations.isNullEndTime = true;
                    else expectations.endTime = data[1];
                    break;
                default:
            }
        }
        return expectations;
    }
}
