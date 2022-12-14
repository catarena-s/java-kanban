package ru.yandex.practicum.kanban.unitTests;

import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.test.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {
    private static final String PATH_TEST_FILES = "src/ru/yandex/practicum/kanban/unitTests/test_data/";
    public static final String DATA_FILE_NAME = "task_manager_data.csv";
    public static final String DATA_FILE_NAME_EMPTY = "task_manager_data_empty.csv";
    public static final String DATA_UPDATE_FILE = "test_update.csv";
    public static final String DATA_UPDATE_EPIC_STATUS = "test_update_epic_status.csv";
    public static final String INIT_TEST_DATA = "init_Test_data.csv";
    public static final String TEST_ADD_TO_MANAGER = "test_additional.csv";
    public static final String TEST_ADD_TO_MANAGER_WRONG_DATA = "test_additional_with_wrong_data.csv";
    public static final String DATA_FILE_NAME_EMPTY_EPIC = "task_manager_data_empty_epic.csv";
    public static final String DATA_FILE_NAME_SUBTASK_WITHOUT_EPIC = "task_manager_data_subtask_without_epic.csv";
    public static final String DATA_FILE_NAME_EMPTY_HISTORY = "task_manager_data_empty_history.csv";
    public static final String ERROR_DATA_FILE_NAME = "task_manager_data_error.csv";

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

    private void readTestData(String testFile) {
        Path file = Paths.get(testFile);
        List<String> lines = new ArrayList<>();
        try {
            lines = FileHelper.readFromFile(file);
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file.toAbsolutePath());
        }
        for (String line : lines) {
            try {
                if (!line.isBlank()) {
                    if (!TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);
                    //  p.accept(line);
                }
            } catch (IllegalArgumentException e) {
                Helper.printMessage(Helper.WRONG_RECORD, line);
            }
        }
    }
}
