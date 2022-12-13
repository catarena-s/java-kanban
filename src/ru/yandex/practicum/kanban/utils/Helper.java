package ru.yandex.practicum.kanban.utils;


public class Helper {
    public static final String WRONG_RECORD = "\033[35mНекорректная тестовая строка: '%s'%n\033[0m";
    public static final String TEST_LINE_MESSAGE = "Test: [ %s ]%n";
    public static final String MSG_TASK_WITH_ID_NOT_EXIST = "\033[35mЗадачи с ID = %s  не существует \n\033[0m";
    public static final String EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE = "\033[35m!!! У эпика нет подзадач. Смена статуса запрещена.\n\033[0m";
    public static final String DATA_HEAD = "id,type,status,name,description,epic";
    private static final String MSG_SEPARATOR = "---------------------------------------------------------------------------\n";

    public static String getItalic(Colors color) {
        return String.format("3;%s", color.getColorNumber());
    }

    public static String getBold(Colors color) {
        return String.format("3;%s", color.getColorNumber());
    }

    public static String getUnderlined(Colors color) {
        return String.format("4;%s", color.getColorNumber());
    }

    private Helper() {
    }

    public static void printSeparator() {
        printMessage(MSG_SEPARATOR);
    }

    public static void printSeparator(String color) {
        System.out.printf(getColoredString(MSG_SEPARATOR, color));
    }

    public static void printEmptySting() {
        printMessage("%n");
    }

    public static void printMessage(String messageTemplate, Object... args) {
        System.out.printf(messageTemplate, args);
    }

    public static void printMessageColored(Colors color, String messageTemplate, Object... args) {
        String format = getColoredString(messageTemplate, color);
        printMessage(format, args);
    }

    public static String getColoredString(String messageTemplate, String color) {
        return String.format("\033[%s%s\033[0m", color, messageTemplate);
    }

    public static String getColoredString(String messageTemplate, Colors color) {
        return getColoredString(messageTemplate,color.getColorNumber());
    }
}
