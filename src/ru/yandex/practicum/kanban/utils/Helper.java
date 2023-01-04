package ru.yandex.practicum.kanban.utils;


import java.time.format.DateTimeFormatter;

public class Helper {
    public static final String WRONG_RECORD = "Некорректная тестовая строка: '%s'";
    public static final String TEST_LINE_MESSAGE = "Test: [ %s ]";
    public static final String MSG_TASK_WITH_ID_NOT_EXIST = "Задачи с ID = %s  не существует";
    public static final String EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE = "!!! У эпика нет подзадач. Смена статуса запрещена.";
    public static final String DATA_HEAD = "type,id,status,name,description,epic,duration,start_data";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String MSG_SEPARATOR = "---------------------------------------------------------------------------";
    private static final String MSG_DOTS_SEPARATOR = "...........................................................................";

    private Helper() {
    }

    public static void printSeparator() {
        printMessage(MSG_SEPARATOR);
    }

    public static void printDotsSeparator() {
        printMessage(MSG_DOTS_SEPARATOR);
    }

    public static void printSeparator(String color) {
        System.out.printf(getColoredString(MSG_SEPARATOR, color));
    }

    public static void printEmptySting() {
        printMessage("");
    }

    public static void printMessage(String messageTemplate, Object... args) {
        System.out.printf(messageTemplate + "\n", args);
    }

    public static void print(String messageTemplate, Object... args) {
        System.out.printf(messageTemplate, args);
    }

    public static void printMessage(Colors color, String messageTemplate, Object... args) {
        String format = getColoredString(messageTemplate, color);
        printMessage(format, args);
    }

    public static String getColoredString(String messageTemplate, String color) {
        return String.format("\033[%s%s\033[0m", color, messageTemplate);
    }

    public static String getColoredString(String messageTemplate, Colors color) {
        return getColoredString(messageTemplate, color.getColor());
    }
}
