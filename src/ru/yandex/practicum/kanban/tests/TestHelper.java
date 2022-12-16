package ru.yandex.practicum.kanban.tests.utils;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.tests.utils.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.io.IOException;
import java.nio.file.Path;

public class TestHelper {
    private static final String PATH_TEST_FILES = "src/ru/yandex/practicum/kanban/tests/test_data/";
    public static final String DATA_FILE_NAME = "manager_data.csv";
    public static final String DATA_FILE_NAME_EMPTY = "manager_data_empty.csv";
    public static final String DATA_UPDATE_FILE = "update.csv";
    public static final String DATA_UPDATE_EPIC_STATUS = "update_epic_status.csv";
    public static final String INIT_TEST_DATA = "init_Test_data.csv";
    public static final String TEST_ADD_TO_MANAGER = "additional.csv";
    public static final String TEST_ADD_TO_MANAGER_Swap_Test = "additional_swapTest.csv";
    public static final String TEST_ADD_TO_MANAGER_WRONG_DATA = "additional_with_wrong_data.csv";
    public static final String DATA_FILE_NAME_EMPTY_EPIC = "manager_data_empty_epic.csv";
    public static final String DATA_FILE_NAME_SUBTASK_WITHOUT_EPIC = "manager_data_subtask_without_epic.csv";
    public static final String DATA_FILE_NAME_EMPTY_HISTORY = "manager_data_empty_history.csv";
    public static final String ERROR_DATA_FILE_NAME = "manager_data_error.csv";

    public static Path getPath(String fileName) {
        return Path.of(PATH_TEST_FILES + fileName);
    }

    public static String getPathString(String fileName) {
        return PATH_TEST_FILES + fileName;
    }

    public static String getExpectation(String line) {
        String[] records = line.split("\\[");
        for (String record : records) {
            if (!record.contains("expectation")) continue;
            String[] data = record.split("->");
            return data[1].trim();
        }
        return "";
    }

    public static String getCommand(String line) {
        String[] records = line.split(",");
        return records[0].trim();
    }

    public static void initFromFile(FileBackedTasksManager taskManager, String pathString) throws IOException, TaskGetterException, TaskAddException {
        String[] testLines = FileHelper.readFromFileToArray(getPath(pathString));
        for (int i = 0; i < testLines.length; i++) {
            String line = testLines[i];
            if (!line.isBlank()) {
                TestAddCommand.executeString(line, taskManager);
            }
        }
    }
}
