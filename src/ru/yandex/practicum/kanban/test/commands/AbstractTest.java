package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.test.TestValidator;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTest implements Test {
    protected TaskManager taskManager;
    protected boolean isPrintHistory = false;

    protected AbstractTest() {
    }

    protected AbstractTest(TaskManager taskManager, boolean isPrintHistory) {
        this.taskManager = taskManager;
        this.isPrintHistory = isPrintHistory;
    }

    void run(TaskManager taskManager, TestCommand op, Consumer<String> p) {
        this.taskManager = taskManager;
        Path file = Paths.get(FileHelper.getFile(op));
        List<String> lines = new ArrayList<>();
        try {
            lines = FileHelper.readFromFile(file);
        } catch (IOException ex) {
            Helper.printMessage(FileHelper.ERROR_FILE_READING, file.toAbsolutePath());
        }
        for (String line : lines) {
            try {
                if (!line.isBlank()) {
                    if (!TestValidator.validateLine(line)) {
                        Helper.printMessage(Helper.WRONG_RECORD, line);
                        continue;
                    }
                    Helper.printMessage(Helper.TEST_LINE_MESSAGE, line);
                    p.accept(line);
                }
            } catch (IllegalArgumentException e) {
                Helper.printMessage(Helper.WRONG_RECORD, line);
            }
        }
    }
}
