package ru.yandex.practicum.kanban.test;

import java.util.Set;

public class TestValidator {
    private TestValidator() {
    }

    private static final Set<String> removeRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask");
    private static final Set<String> getRow = Set.of("task", "epic", "sub_task", "allepic", "all", "alltask", "allsubtask", "epicsubtask");

    public static boolean validateLine(String line) throws IllegalArgumentException {
        String[] records = line.split(",");
        TestCommand command = TestCommand.valueOf(records[0].trim().toUpperCase());
        switch (command) {
            case ADD: {
                return validateAddLine(records);
            }
            case UPDATE: {
                return validateUpdateLine(records);
            }
            case REMOVE: {
                return validateRemoveLine(records);
            }
            case GET: {
                return validateGetLine(records);
            }
            case CLONE: {
                return records.length > 2;
            }
            default: return false;
        }
    }

    private static boolean validateUpdateLine(String[] records) {
        return records[2].toLowerCase().trim().startsWith("id=") && records.length > 3;
    }

    private static boolean validateGetLine(String[] records) {
        return getRow.contains(records[1].trim().toLowerCase());
    }

    private static boolean validateAddLine(String[] records) {
        return records.length >= 2;
    }

    private static boolean validateRemoveLine(String[] records) {
        return removeRow.contains(records[1].trim().toLowerCase());
    }
}
