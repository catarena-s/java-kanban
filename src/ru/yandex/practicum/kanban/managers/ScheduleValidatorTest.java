package ru.yandex.practicum.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.tests.TestHelper.TEST_ADD_TO_MANAGER_Swap_Test;

class ScheduleValidatorTest<T extends TaskManager> {
    T taskManager;
    ScheduleValidator validator = new ScheduleValidator();

    @BeforeEach
    void setUp(TestInfo info) throws IOException, TaskGetterException, TaskAddException {
        Managers managers = new Managers(1);
        taskManager = (T) managers.getDefault();

        if (info.getTags().contains("InitData")) {
            List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.TEST_ADD_TO_MANAGER_Swap_Test));
            for (String line : testLines) {
                if (line.isBlank()) continue;
                String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                TestAddCommand.executeString(testLine, taskManager);
            }
        }
    }

    @Test
    void takeTimeForTask() throws IOException, TaskGetterException, TaskAddException {
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER_Swap_Test));
        for (int i = 0; i < testLines.length; i++) {
            String line = testLines[i];
            if (!line.isBlank()) {
                String expected = TestHelper.getExpectation(line).trim();
                if (line.isBlank()) return;
                String testLine = line.substring(0, line.indexOf("["));

                Task task = TestAddCommand.parseLine(testLine, taskManager);
                boolean isTakeTime = validator.takeTimeForTask(task);

//                assertTrue(isTakeTime);
//                assertNotNull(task, "ERROR_MSG_TASK_NOT_FOUND");

                assertEquals(expected, String.valueOf(isTakeTime));

//                final List<Task> tasks = taskManager.getAll();
//                assertEquals(before + i + 1, tasks.size(), "Неверное количество задач.");
            }
        }
        for (ScheduleValidator.DayOfWeek dayOfWeek : validator.getBusyTime()) {
            ScheduleValidator.print(dayOfWeek, false);
        }
//        validator.getBesyTime().stream().forEach(f -> ScheduleValidator.print(f, false));

//        TaskPrinter.printAllTaskManagerList(taskManager);
//        assertEquals(before + 8, taskManager.getAll().size(), "Неверное количество задач.");
    }

    @Test
    @Tag(value = "InitData")
    void freeTime() throws TaskGetterException, TaskRemoveException {

        Task task = taskManager.getTask("0001");
        int countBeforeRemove = taskManager.getAllTasks().size();
//        Helper.printSeparator();
        Helper.printMessage("Удаляем задачу :%s", task.toActualStringFoTest());
        taskManager.removeTask(task.getTaskID());
    }
}