package ru.yandex.practicum.kanban.utils;

public class Helper {
    public static final String WRONG_RECORD = "Некорректная тестовая строка: '%s'%n";
    public static final String TEST_LINE_MESSAGE = "Test: [ %s ]%n";
    public static final String MSG_TASK_WITH_ID_NOT_EXIST = "Задачи с ID = %s  не существует \n";
    public static final String EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE = "!!! У эпика нет подзадач. Смена статуса запрещена.\n";
    public static final String DATA_HEAD = "id,type,status,name,description,epic";
    private static final String MSG_SEPARATOR = "---------------------------------------------------------------------------\n";

    private Helper() {
    }

    public static void printSeparator() {
        System.out.println(MSG_SEPARATOR);
    }

    public static void printEmptySting() {
        System.out.println();
    }

    public static void printMessage(String messageTemplate, Object... args) {
        System.out.printf(messageTemplate, args);
    }

}
