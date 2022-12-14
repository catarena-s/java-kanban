package ru.yandex.practicum.kanban.unitTests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.test.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void init(TestInfo info) throws IOException, TaskGetterException, TaskAddException {
        init(1);
        if (info.getTags().contains("InitData")) {
            List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
            for (String line : testLines) {
                if (line.isBlank()) continue;
                TestAddCommand.executeString(line, taskManager);
            }
        }
    }

}