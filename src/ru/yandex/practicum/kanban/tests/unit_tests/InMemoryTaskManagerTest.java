package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.io.IOException;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp(TestInfo info) throws TaskException, IOException {
        init(1);
        if (info.getTags().contains("InitData")) {
            List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
            for (String line : testLines) {
                final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                if (testLine.isBlank()) continue;
                TestAddCommand.executeString(testLine, taskManager);
            }
        }
    }
}