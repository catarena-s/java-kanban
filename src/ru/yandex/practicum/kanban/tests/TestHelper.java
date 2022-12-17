package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.nio.file.Path;

public class TestHelper {
    public static final String ERROR_MSG_TASK_NOT_FOUND = "Задача не найдена.";
    public static final String ERROR_MSG_TASK_NOT_EQUALS = "Задачи не совпадают.";
    public static final String ERROR_MASSAGES_NOT_EQUALS = "Сообщения об ошибке не совпадают";
    public static final String INFO_FORMATTER = "Info: %s";
    public static final String AFTER_TEST_MSG = "After test:";
    public static final String BEFORE_TEST_MSG = "Before test:";
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

    /**
     * Получаем из файла с тестовыми данными expectation, если они там есть
     *
     * @param line
     * @return
     */
    public static String getExpectation(String line) {
        if (!line.contains("[")) return "";
        String[] records = line.split("\\[");
        for (String record : records) {
            if (!record.contains("expectation")) continue;
            String[] data = record.split("->");
            return data[1].trim();
        }
        return "";
    }

    /**
     * получаем тип команды(add,update,remove)
     * @param line
     * @return
     */
    public static String getCommand(String line) {
        String[] records = line.split(",");
        return records[0].trim();
    }

    /**
     * Дабавление данных из тестового файла
     * @param taskManager
     * @param pathString
     * @throws Exception
     */
    public static void addDataFromFile(FileBackedTasksManager taskManager, String pathString) throws Exception {
        String[] testLines = FileHelper.readFromFileToArray(getPath(pathString));
        for (int i = 0; i < testLines.length; i++) {
            String line = testLines[i];
            if (!line.isBlank()) {
                TestAddCommand.executeString(line, taskManager);
            }
        }
    }
}
