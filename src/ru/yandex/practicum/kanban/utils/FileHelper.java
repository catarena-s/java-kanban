package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.test.TestCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHelper {
    public static final String ERROR_FILE_READING = "Ошибка чтения файла : '%s'%n";
    public static final String DATA_FILE_NAME = "src/ru/yandex/practicum/kanban/data/task_manager_data.csv";
    private static final String FILE_ADD_TEST_DATA = "src/ru/yandex/practicum/kanban/data/data_for_test/test_additional.csv";
    private static final String FILE_UPDATE_TEST_DATA = "src/ru/yandex/practicum/kanban/data/data_for_test/test_update.csv";
    private static final String FILE_REMOVE_TEST_DATA = "src/ru/yandex/practicum/kanban/data/data_for_test/test_remove.csv";
    private static final String FILE_MIX_TEST_DATA = "src/ru/yandex/practicum/kanban/data/data_for_test/test_mix_commands.csv";
    private static final String FILE_GET_DATA = "src/ru/yandex/practicum/kanban/data/data_for_test/test_get_task.csv";

    private FileHelper() {
    }

    public static List<String> readFromFile(Path file) throws IOException {
        String[] dataFromFile = Files.readString(file).split(System.lineSeparator());
        return new ArrayList<>(Arrays.asList(dataFromFile));
    }

    public static void saveToFile(Path file, String data) throws IOException {
        try (FileWriter fw = new FileWriter(file.toString())) {
            fw.write(data);
        }
    }

    public static String getFile(TestCommand comand) {
        switch (comand) {
            case ADD:
                return FILE_ADD_TEST_DATA;
            case REMOVE:
                return FILE_REMOVE_TEST_DATA;
            case UPDATE:
                return FILE_UPDATE_TEST_DATA;
            case GET:
                return FILE_GET_DATA;
            case MIX:
                return FILE_MIX_TEST_DATA;
            default:
                return "";
        }
    }
}
