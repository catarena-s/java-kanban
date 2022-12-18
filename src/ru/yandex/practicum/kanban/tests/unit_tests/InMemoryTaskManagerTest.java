package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

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

//    @BeforeEach
//    @Override
//    void beforeEachTest(TestInfo info) {
//        Helper.printMessage("%s :",taskManager.getClass().getSimpleName());
//        super.beforeEachTest(info);
//    }
}