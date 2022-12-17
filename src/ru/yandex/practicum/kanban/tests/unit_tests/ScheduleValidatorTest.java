package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleUtil;
import ru.yandex.practicum.kanban.managers.schadule.ScheduleValidator;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.tests.TestHelper.TEST_ADD_TO_MANAGER_Swap_Test;

class ScheduleValidatorTest {
    private TaskManager taskManager;
    private ScheduleValidator validator = new ScheduleValidator();

    @BeforeEach
    void setUp(TestInfo info) throws Exception {
        final Managers managers = new Managers(1);
        taskManager = managers.getDefault();

        if (info.getTags().contains("InitData")) {
            final List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.TEST_ADD_TO_MANAGER_Swap_Test));
            for (String line : testLines) {
                if (line.isBlank()) continue;
                final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                TestAddCommand.executeString(testLine, taskManager);
            }
        }
    }

    @Test
    void takeTimeForTask() throws IOException, TaskException {
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER_Swap_Test));
        for (int i = 0; i < testLines.length; i++) {
            final String line = testLines[i];
            if (!line.isBlank()) {
                final String expected = TestHelper.getExpectation(line).trim();
                if (line.isBlank()) return;
                final String testLine = line.substring(0, line.indexOf("["));

                final Task task = TestAddCommand.parseLine(testLine, taskManager);
                final boolean isTakeTime = validator.takeTimeForTask(task);

//                assertTrue(isTakeTime);
//                assertNotNull(task, "ERROR_MSG_TASK_NOT_FOUND");

                assertEquals(expected, String.valueOf(isTakeTime));

//                final List<Task> tasks = taskManager.getAll();
//                assertEquals(before + i + 1, tasks.size(), "Неверное количество задач.");
            }
        }
        validator.getBusyDays().forEach(ScheduleUtil::print);
//        validator.getBesyTime().stream().forEach(f -> ScheduleValidator.print(f, false));

//        TaskPrinter.printAllTaskManagerList(taskManager);
//        assertEquals(before + 8, taskManager.getAll().size(), "Неверное количество задач.");
    }

    @Test
    @Tag(value = "InitData")
    void freeTime() throws TaskGetterException, TaskRemoveException {

        final Task task = taskManager.getTask("0001");
        final int countBeforeRemove = taskManager.getAllTasks().size();
//        Helper.printSeparator();
        Helper.printMessage("Удаляем задачу :%s", task.toActualStringFoTest());
        taskManager.removeTask(task.getTaskID());
    }
}