package ru.yandex.practicum.kanban.tests.unit_tests;

import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.tests.commands.TestRemoveCommand;
import ru.yandex.practicum.kanban.tests.commands.TestUpdateCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.yandex.practicum.kanban.tests.TestHelper.*;

abstract class TaskManagerTest<T extends TaskManager> implements TestLogger {

    protected T taskManager;

    protected void init(int config, String... args) {
        Managers managers = new Managers(config, args);
        taskManager = (T) managers.getDefault();
    }
//    @BeforeEach
//    void beforeEachTest(TestInfo testInfo) {
//        Helper.printMessage(String.format("Info : [%s]",
//                testInfo.getDisplayName()));
//    }
    @Test
    @Tag("EmptyFile")
    @DisplayName("Добавление задач, эпиков и подзадач с корректными данными")
    void testAddTaskClearDataToEmptyTM() throws TaskException, IOException {
//        Helper.printMessage("-----------Test: testAddTask ----------------------------------");
        final List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
        final int before = taskManager.getAll().size();
        int index = 1;
        for (String line : testLines) {
            if (line.isBlank()) continue;
            final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
            final Task task = TestAddCommand.parseLine(testLine, taskManager);
            taskManager.add(task);
            final List<Task> tasks = taskManager.getAll();
            assertEquals(before + index, tasks.size());
            index++;
        }
        assertEquals(before + 14, taskManager.getAll().size());
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Добавление подзадач с ошибочными тестовыми данными:\n" +
            "1- добавление дубликата.\n" +
            "2- добавление подзадачи без неправильным id эпика.\n" +
            "3- добавление подзадачи без указания эпика.\n" +
            "4- добавление подзадачи в пустой таск-менеджер.\n")
    void addSubTaskWrongData() throws Exception {
//        Helper.printMessage("-----------Test: addSubTaskWrongData ----------------------------------");
        Helper.printSeparator();
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER_WRONG_DATA));
        //тест - Добавление дубликата ---
        //      Helper.printMessage("Добавление дубликата.");
        testTestAddCommand(testLines[0], TaskAddException.class);
        //для следующего теста нужно добавть хотябы один эпик
//        TestAddCommand.executeString(testLines[3], taskManager);
//        Helper.printMessage("Добавление подзадачи без неправильным id эпика:");
        testTestAddCommand(testLines[4], TaskGetterException.class);
        taskManager.clear();
//        Helper.printMessage("Добавление подзадачи без указания эпика:");
        testTestAddCommand(testLines[1], TaskAddException.class);
        //----------------------------
//        Helper.printMessage("Добавление подзадачи, когда не создано ни одного эпика:");
        testTestAddCommand(testLines[2], TaskGetterException.class);
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получение всех подзадач по эпику")
    void getAllSubtaskByEpic(TestInfo info) throws TaskGetterException {
//        Helper.printMessage("-----------Test: getAllSubtaskByEpic ----------------------------------");
        final Epic epic = (Epic) taskManager.getEpic("0004");
        final List<Task> subTasks = taskManager.getAllSubtaskFromEpic(epic);
        assertEquals(subTasks.size(), epic.getSubTasks().size());

        final Epic epicWithOutSubTask = (Epic) taskManager.getEpic("0014");
        final List<Task> subTasksFoEpic = taskManager.getAllSubtaskFromEpic(epicWithOutSubTask);
        assertEquals(0, epicWithOutSubTask.getSubTasks().size());
        assertEquals(0, subTasksFoEpic.size());
    }

    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получение всех подзадач по эпику в пустом таск-менеджере")
    void getTaskFromEmptyManager(TestInfo info) {
//        Helper.printMessage("-----------Test: getTaskFromEmptyManager ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        final TaskException ex = Assertions.assertThrows(TaskGetterException.class,
                () -> taskManager.getEpic("0004"));
        assertEquals("Ошибка получения: 'Эпик' - отсутствуют", ex.getDetailMessage().trim(), "Эпик не должен быть получен");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получить все задачи, не пустой менеджер.")
    void getAll(TestInfo info) {
//        Helper.printMessage("-----------Test: getAll ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        List<Task> all = taskManager.getAll();
        List<Task> subTasks = taskManager.getAllSubTasks();
        List<Task> epics = taskManager.getAllEpics();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(4, tasks.size(), "Ошибка получения задач");
        assertEquals(4, epics.size(), "Ошибка получения эпиков");
        assertEquals(6, subTasks.size(), "Ошибка получения подзадач");
        assertEquals(14, all.size(), "Ошибка получения всех задач");
    }


    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получить все задачи из пустого менеджера.")
    void getAllFromEmptyManager() {
//        testReporter.publishEntry("a status message");
//
//        Helper.printMessage("-----------Test: getAllFromEmptyManager ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        List<Task> all = taskManager.getAll();
        List<Task> subTasks = taskManager.getAllSubTasks();
        List<Task> epics = taskManager.getAllEpics();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(0, tasks.size() + epics.size() + subTasks.size(), "Ошибка получения задач");
        assertEquals(0, all.size(), "Ошибка получения всех задач");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Обновление задач\n")
    void updateTasks() throws IOException, TaskGetterException {
//        Helper.printMessage("-----------Test: updateTasks ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_FILE));
        int indexTest = 1;
        for (String line : testLines) {
            Helper.printMessage(Colors.UNDERLNE, "Test #%d: %s", indexTest++, line);
            Task taskUpdated = TestUpdateCommand.executeString(line, taskManager, true);
            Helper.printMessage("Task after update : %s\n", taskUpdated.toCompactString());
            Task taskFromTM = taskManager.getById(taskUpdated.getTaskID());
            assertEquals(taskUpdated.toActualStringFoTest(), taskFromTM.toActualStringFoTest(), "Строки отличаются.");
        }
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Обновление статусов эпика:\n" +
            "- при удалении и добавлении подзадач;\n" +
            "- при изменении статуса подзадачи.")
    void updateEpicData() throws Exception {
//        Helper.printMessage("-----------Test: updateEpicStatus ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_EPIC_STATUS));
        final String epicId = testLines[0].trim();
        final Epic epic = (Epic) taskManager.getEpic(epicId);
        Helper.printMessage("Before tests: %n%s", epic.toCompactString());
        TaskPrinter.printSortedTasks(epic.getSubTasks());
        for (int i = 1; i < testLines.length; i++) {
            final String line = testLines[i];

            final TestHelper.Expectations expectations = TestHelper.parse(TestHelper.getExpectation(line).trim());
            final String testLine = line.substring(0, line.indexOf("["));

            Helper.printMessage(Colors.UNDERLNE, "%nTest #%d: %s", i, testLine);
            if (testLine.isBlank()) continue;
            switch (TestHelper.getCommand(testLine).trim().toLowerCase()) {
                case "add": {
                    TestAddCommand.executeString(testLine, taskManager);
                    break;
                }
                case "update": {
                    TestUpdateCommand.executeString(testLine, taskManager, false);
                    break;
                }
                case "remove": {
                    TestRemoveCommand.executeString(testLine, taskManager);
                    break;
                }
                default:
                    break;
            }
            assertEpicData(epic, expectations);

            Helper.printMessage("%s %n%s", AFTER_TEST_MSG, epic.toActualStringFoTest());
            TaskPrinter.printSortedTasks(epic.getSubTasks());
        }
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление задачи.")
    void removeTask() throws TaskGetterException, TaskRemoveException {
//        Helper.printMessage("-----------Test: removeTask ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        Task task = taskManager.getTask("0001");
        int countBeforeRemove = taskManager.getAllTasks().size();
        taskManager.removeTask(task.getTaskID());
        int countAfter = taskManager.getAllTasks().size();
        assertEquals(countBeforeRemove - 1, countAfter, "");

        //Попытка удаление задачи с некорректным id
        TaskException ex = Assertions.assertThrows(TaskRemoveException.class, () -> taskManager.removeTask("004"));
        assertEquals("Ошибка удаления: 'Задача' c id=004 не найден", ex.getDetailMessage().trim(), "Задача не должна быть удалена");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление эпикаю")
    void removeEpic() throws TaskGetterException, TaskRemoveException {
//        Helper.printMessage("-----------Test: removeEpic ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        printTaskManager(BEFORE_TEST_MSG,taskManager);
        Epic task = (Epic) taskManager.getEpic("0004");

        int countBefore = taskManager.getAll().size();
        taskManager.removeEpic(task.getTaskID());
        int countAfter = taskManager.getAll().size();

        printTaskManager(AFTER_TEST_MSG,taskManager);

        assertEquals(countBefore - 4, countAfter, "");
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление подзадач: проверяем обновление данных у эпика(status, startTime, endTime, duration)")
    void removeSubtask() throws TaskGetterException, TaskRemoveException, IOException {
//        Helper.printMessage("-----------Test: removeSubtask ----------------------------------");
//        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());

        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_REMOVE_SUBTASKS));
        final String epicId = testLines[0].trim();
        final Epic epic = (Epic) taskManager.getEpic(epicId);

        for (int i = 1; i < testLines.length; i++) {
            final String line = testLines[i];
            TestHelper.Expectations expectations = TestHelper.parse(TestHelper.getExpectation(line).trim());
            final String testLine = line.substring(0, line.indexOf("["));

            TestRemoveCommand.executeString(testLine, taskManager);

            assertEpicData(epic, expectations);
            Helper.printMessage("%s %n%s", AFTER_TEST_MSG, epic.toCompactString());
            TaskPrinter.printSortedTasks(epic.getSubTasks());
            Helper.printDotsSeparator();
        }
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получить преоритизированный список " +
            "- список упорядочен по возрастанию startTime.\n" +
            "- в конце списка задачи у которых не установлен startTime")
    void getPrioritizedTasks() throws Exception {
        TestHelper.addDataFromFile(taskManager, TEST_ADD_TO_MANAGER);
        List<Task> expectationList = List.of(
                taskManager.getById("0001"),
                taskManager.getById("0005"),
                taskManager.getById("0010"),
                taskManager.getById("0007"),
                taskManager.getById("0006"),
                taskManager.getById("0012"),
                taskManager.getById("0011"),
                taskManager.getById("0003"),
                taskManager.getById("0002"),
                taskManager.getById("0013"),
                taskManager.getById("0015"),
                taskManager.getById("0016"),
                taskManager.getById("0017"),
                taskManager.getById("0019"),
                taskManager.getById("0020")
        );
        List<Task> list = taskManager.getPrioritizedTasks();
        TaskPrinter.printList(list);
        assertEquals(expectationList, list);
        Helper.printDotsSeparator();
    }

    private void testTestAddCommand(String line, Class<? extends TaskException> classException) {
        final String expected = TestHelper.getExpectation(line).trim();
        if (line.isBlank()) return;
        final String testLine = line.substring(0, line.indexOf("["));

        Helper.printMessage("Test : %s", testLine);

        final TaskException ex = Assertions.assertThrows(classException,
                () -> TestAddCommand.executeString(testLine, taskManager));
        assertEquals(expected.trim(), ex.getDetailMessage().trim(), ERROR_MASSAGES_NOT_EQUALS);
    }

    private void assertEpicData(Epic epic, Expectations expectations) {
        assertEquals(expectations.status.trim(), epic.getStatus().name(), "status не совпадают");
        assertEquals(expectations.duration, epic.getDuration(), "duration не совпадают");
        assertEquals(expectations.startTime.trim(), epic.getStartTime().format(formatter), "startTime не совпадают");
        if (expectations.isNullEndTime) {
            assertNull(epic.getEndTime());
        } else
            assertEquals(expectations.endTime.trim(), epic.getEndTime().format(formatter), "EndTime не совпадают");
    }

}