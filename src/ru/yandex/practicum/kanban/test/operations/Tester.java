package ru.yandex.practicum.kanban.test.operations;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public abstract class Tester implements Test {
    protected static TaskManager taskManager;
    protected static boolean isPrintHistory = false;
    public Tester(TaskManager taskManager,boolean isPrintHistory) {
        this.taskManager = taskManager;
        this.isPrintHistory = isPrintHistory;
    }

    public Tester() {
    }
    void run(TaskManager taskManager, String op, Consumer<String> p){
        this.taskManager = taskManager;
        String file = Helper.getFile(op);
        try {
            List<String> lines = FileHelper.readFromFile(file);
            for (String line : lines) {
                if (!line.isBlank()) {
                    if (TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);
                    p.accept(line);
                    // update(line);
                }
            }
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file);
        }
    }
}
