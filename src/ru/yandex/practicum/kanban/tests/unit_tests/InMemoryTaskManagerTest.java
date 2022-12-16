package ru.yandex.practicum.kanban.tests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.tests.utils.TestHelper;
import ru.yandex.practicum.kanban.tests.utils.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.io.IOException;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp(TestInfo info) throws IOException, TaskGetterException, TaskAddException, InterruptedException {
        init(1);
        if (info.getTags().contains("InitData")) {
            List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
            for (String line : testLines) {
                if (line.isBlank()) continue;
                TestAddCommand.executeString(line, taskManager);
                Thread.sleep(100);
            }
        }
    }

}