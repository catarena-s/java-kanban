package ru.yandex.practicum.kanban.test;

import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

public class TestValidator {
    private static final Set<String> removeRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask");
    private static final Set<String> getRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask", "EpicSubtask");

    public static boolean validateUpdateLine(String[] records,int index) {
        return records[index].toLowerCase().trim().startsWith("type=")
                && records[index+1].toLowerCase().trim().startsWith("id=");
    }
    public static boolean validateGetLine(String[] records, int i) {
        return getRow.contains(records[i].trim().toLowerCase());
    }

    public static boolean validateLine(String line) {
        String[] records = line.split(",");
        Set<String> firstRow = Set.of("add", "upd", "del","get");

        boolean isFirstCorrect = firstRow.contains(records[0].trim().toLowerCase());
        if (!isFirstCorrect) return false;

        switch (records[0].trim().toLowerCase()) {
            case "add": {
                return validateAddLine(records, 1);
            }
            case "upd": {
                return /*records[1].toLowerCase().trim().startsWith("type=") &&
                        records[2].toLowerCase().trim().startsWith("id=") &&*/
                        validateUpdateLine(records,1)&&
                        records.length > 3;
            }
            case "del": {
                return validateRemoveLine(records, 1);
            }
            case "get":{
                return validateGetLine(records,1);
            }
            default:
                return false;
        }
    }

    private static boolean validateAddLine(String[] records, int index) {
        return records[index].trim().toLowerCase().startsWith("type=") && records.length > 2;
    }

    public static boolean validateRemoveLine(String[] records, int index) {
        return removeRow.contains(records[index].trim().toLowerCase());
    }
}
