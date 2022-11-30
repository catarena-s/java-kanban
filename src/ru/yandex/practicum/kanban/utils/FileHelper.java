package ru.yandex.practicum.kanban.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static final String ERROR_FILE_READING = "Ошибка чтения файла : %s%n";
    public static final String FILE_ADD_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_additional.csv";
    public static final String FILE_UPDATE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_update.csv";
    public static final String FILE_REMOVE_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_just_remove.csv";
    public static final String FILE_MIX_TEST_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_mix_operations.csv";
    public static final String FILE_GET_DATA = "src/ru/yandex/practicum/kanban/test/test_data/test_get_task.csv";

    private FileHelper(){}

    public static List<String> readFromFile(String file) throws IOException{
        List<String> inputData = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                inputData.add(bufferedReader.readLine());
            }
        }
        return inputData;
    }
}
