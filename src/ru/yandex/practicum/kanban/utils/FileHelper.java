package ru.yandex.practicum.kanban.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static final String ERROR_FILE_READING = "Ошибка чтения из файла %s%n";

    private FileHelper(){}

    public static List<String> readFromFile(Path file) throws IOException{
        List<String> inputData = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(String.valueOf(file.getFileName())))) {
            while (bufferedReader.ready()) {
                inputData.add(bufferedReader.readLine());
            }
        }
        return inputData;
    }
}
