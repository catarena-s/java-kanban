package ru.yandex.practicum.kanban.test;

import java.util.Set;

public class TestValidator {
    private TestValidator() {
    }

    private static final Set<String> removeRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask");
    private static final Set<String> getRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask", "epicsubtask");

    public static boolean validateLine(String line) {
        String[] records = line.split(",");
        final Set<String> firstRow = Set.of("add", "upd", "del", "get");

        boolean isFirstCorrect = firstRow.contains(records[0].trim().toLowerCase());
        if (!isFirstCorrect) return true;

        switch (records[0].trim().toLowerCase()) {
            case "add": {
                return !validateAddLine(records);
            }
            case "upd": {
                return !validateUpdateLine(records);
            }
            case "del": {
                return !validateRemoveLine(records);
            }
            case "get": {
                return !validateGetLine(records);
            }
            default:
                return true;
        }
    }

    private static boolean validateUpdateLine(String[] records) {
        return records[1].toLowerCase().trim().startsWith("type=")
                && records[2].toLowerCase().trim().startsWith("id=") && records.length > 3;
    }

    private static boolean validateGetLine(String[] records) {
        return getRow.contains(records[1].trim().toLowerCase());
    }

    private static boolean validateAddLine(String[] records) {
        return records[1].trim().toLowerCase().startsWith("type=") && records.length > 2;
    }

    private static boolean validateRemoveLine(String[] records) {
        return removeRow.contains(records[1].trim().toLowerCase());
    }
}
