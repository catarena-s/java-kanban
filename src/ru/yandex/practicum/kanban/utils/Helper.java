package ru.yandex.practicum.kanban.utils;

public class Helper {
    private static final String MSG_SEPARATOR = "---------------------------------------------------------------------------\n";
    public static final String MSG_ADD_TASK = "Добавлена задача: %s - %s\n";
    public static final String MSG_TASK_WITH_ID_NOT_EXIST = "Задачи с ID = %s  не существует \n";
    public static final String MSG_UPDATE_TASK_BY_ID = ">>Обновляем задачу ID = %s - %s\n";
    public static final String MSG_GET_TASK_BY_ID = ">>Получаем %s ID = %s\n";
    public static final String MSG_DELETE_BY_ID = ">>Удаляем задачу ID = %s\n";
    public static final String MSG_TEMPLATE_TASK_PRINT = " -> %s\n";
    public static final String EPIC_HAS_NO_SUBTASKS_DISABLED_STATUS_CHANGE = "!!! У эпика нет подзадач. Смена статуса запрещена.\n";
    public static final String DATA_HEAD = "id,  type,status,name,description,epic";
    public static final String DATA_FILE_NAME = "src/ru/yandex/practicum/kanban/data/task_manager_data.csv";

    private Helper() {
    }

    public static void printSeparator(){
        System.out.println(MSG_SEPARATOR);
    }
    public static void printEmptySting() {
        System.out.println();
    }

    public static void printMessage(String messageTemplate, Object... args) {
        System.out.printf(messageTemplate, args);
    }

}
