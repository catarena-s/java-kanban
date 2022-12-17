package ru.yandex.practicum.kanban.tests.unit_tests;

import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.tests.commands.TestRemoveCommand;
import ru.yandex.practicum.kanban.tests.commands.TestUpdateCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

import javax.management.DescriptorKey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.yandex.practicum.kanban.tests.TestHelper.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected void init(int config, String... args) {
        Managers managers = new Managers(config, args);
        taskManager = (T) managers.getDefault();
    }

    @Test
    @DisplayName("Тестирование добавление задач, эпиков и подзадач с корректными данными")
    void testAddTask() throws Exception {
        Helper.printMessage("-----------Test: testAddTask ----------------------------------");
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER));
        int before = taskManager.getAll().size();
        for (int i = 0; i < testLines.length; i++) {
            final String line = testLines[i];
            if (!line.isBlank()) {
                Helper.printMessage("Test #%d: %s", before + i + 1, line);
                final Task task = TestAddCommand.executeString(line, taskManager);
                final Task savedTask = taskManager.getById(task.getTaskID());

                assertNotNull(savedTask, ERROR_MSG_TASK_NOT_FOUND);
                assertEquals(task, savedTask, ERROR_MSG_TASK_NOT_EQUALS);

                final List<Task> tasks = taskManager.getAll();
                assertEquals(before + i + 1, tasks.size(), "Неверное количество задач.");
            }
        }
        TaskPrinter.printAllTaskManagerList(taskManager);
        assertEquals(before + 8, taskManager.getAll().size(), "Неверное количество задач.");

    }

    @Test
    @DisplayName("Добавление подзадач с некорректными тестовыми данными")
    @Tag(value = "EmptyFile")
    void addSubTaskWrongData() throws Exception {
        Helper.printMessage("-----------Test: addSubTaskWrongData ----------------------------------");
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER_WRONG_DATA));
        Helper.printMessage("Добавление подзадачи без указания эпика:");
        testTestAddCommand(testLines[0], TaskAddException.class);

        Helper.printMessage("Добавление подзадачи, когда не создано ни одного эпика:");
        testTestAddCommand(testLines[1], TaskGetterException.class);

        //для следующего теста нужно добавть хотябы один эпик
        TestAddCommand.executeString(testLines[2], taskManager);
        Helper.printMessage("Добавление подзадачи без неправильным id эпика:");
        testTestAddCommand(testLines[3], TaskGetterException.class);
    }

    private void testTestAddCommand(String line, Class<? extends TaskException> classException) {
        final String expected = TestHelper.getExpectation(line).trim();
        if (line.isBlank()) return;
        final String testLine = line.substring(0, line.indexOf("["));
        Helper.printMessage("Test : %s%n", testLine);
        final TaskException ex = Assertions.assertThrows(classException,
                () -> TestAddCommand.executeString(testLine, taskManager));
          assertEquals(expected.trim(), ex.getDetailMessage().trim(), ERROR_MASSAGES_NOT_EQUALS);
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получение всех подзадач по эпику")
    void getAllSubtaskByEpic(TestInfo info) throws TaskGetterException {
        Helper.printMessage("-----------Test: getAllSubtaskByEpic ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        final Epic epic = (Epic) taskManager.getEpic("0004");
        final List<Task> subTasks = taskManager.getAllSubtaskFromEpic(epic);
        assertEquals(subTasks.size(), epic.getSubTasks().size(), "Ошибка получения подзадач у эпика");
    }

    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получение всех подзадач по эпику в пустом таск-менеджере")
    void getTaskFromEmptyManager(TestInfo info) {
        Helper.printMessage("-----------Test: getTaskFromEmptyManager ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        final TaskException ex = Assertions.assertThrows(TaskGetterException.class,
                () -> taskManager.getEpic("004"));
        assertEquals("Ошибка получения: 'Эпик' - отсутствуют", ex.getDetailMessage().trim(), "Эпик не должен быть получен");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получение всех задач")
    void getAll(TestInfo info) {
        Helper.printMessage("-----------Test: getAll ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        List<Task> all = taskManager.getAll();
        List<Task> subTasks = taskManager.getAllSubTasks();
        List<Task> epics = taskManager.getAllEpics();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(4, tasks.size(), "Ошибка получения задач");
        assertEquals(3, epics.size(), "Ошибка получения эпиков");
        assertEquals(6, subTasks.size(), "Ошибка получения подзадач");
        assertEquals(13, all.size(), "Ошибка получения всех задач");
    }


    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получение всех задач из пустого менеджера")
    void getAllFromEmptyManager(TestInfo info,TestReporter testReporter) {
        testReporter.publishEntry("a status message");

        Helper.printMessage("-----------Test: getAllFromEmptyManager ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        List<Task> all = taskManager.getAll();
        List<Task> subTasks = taskManager.getAllSubTasks();
        List<Task> epics = taskManager.getAllEpics();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(0, tasks.size() + epics.size() + subTasks.size(), "Ошибка получения задач");
        assertEquals(0, all.size(), "Ошибка получения всех задач");
    }

    @Test
    @Tag(value = "InitData")
    void updateTasks(TestInfo info) throws IOException, TaskGetterException {
        Helper.printMessage("-----------Test: updateTasks ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_FILE));
        int indexTest = 1;
        for (String line : testLines) {
            Helper.printMessage(Colors.CURENT.getUnderLine(), "Test #%d: %s", indexTest++, line);
            Task taskUpdated = TestUpdateCommand.executeString(line, taskManager, true);
            Helper.printMessage("Task after update : %s\n", taskUpdated.toCompactString());
            Task taskFromTM = taskManager.getById(taskUpdated.getTaskID());
            assertEquals(taskUpdated.toActualStringFoTest(), taskFromTM.toActualStringFoTest(), "Строки отличаются.");
        }
    }

    @Test
    @Tag(value = "InitData")
    @Description(value = "Тестируем обновление статусов эпика")
    void updateEpicStatus(TestInfo info) throws Exception {
        Helper.printMessage("-----------Test: updateEpicStatus ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        final String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_EPIC_STATUS));
        final String epicId = testLines[0].trim();
        final Epic epic = (Epic) taskManager.getEpic(epicId);
        Helper.printMessage("Before tests: %n%s", epic.toCompactString());
        TaskPrinter.printSortedTasks(epic.getSubTasks());
        for (int i = 1; i < testLines.length; i++) {
            final String line = testLines[i];
            final String expected = TestHelper.getExpectation(line).trim();
            final String testLine = line.substring(0, line.indexOf("["));
            Helper.printMessage(Colors.UNDERLNE, "Test #%d: %s", i, testLine);
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
            Helper.printMessage("%s %n%s", AFTER_TEST_MSG, epic.toCompactString());
            TaskPrinter.printSortedTasks(epic.getSubTasks());
            assertEquals(expected.trim(), epic.toActualStringFoTest().trim(), "Некорректное обновление.");
        }
    }

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    static Stream<Arguments> stringIntAndListProvider() {
        return Stream.of(
                arguments("0005", "13-12-2022 09:21", "13-12-2022 09:21"),
                arguments("0006", "13-12-2022 19:21", "13-12-2022 19:21")
        );
    }
    @Test
    @Tag(value = "InitData")
    @Tag(value = "Global")
    @ParameterizedTest
//    @MethodSource("stringIntAndListProvider")
    @CsvFileSource(resources = "/ru/yandex/practicum/kanban/tests/test_data/employeeData.csv", numLinesToSkip = 0)
    void setStartDate1(String id,String time,String expect) throws Exception {
//        Helper.printSeparator();
//        Helper.printMessage(BEFORE_TEST_MSG);
        final Task epic = taskManager.getEpic("0004");
//        TaskPrinter.printEpicInfo((Epic) epic);
        final SubTask subTaskForUpdate1 = (SubTask) taskManager.getSubtask(id);
        subTaskForUpdate1.builder().startTime(time);
        taskManager.updateTask(subTaskForUpdate1);

        //        final SubTask subtaskFromTM1 = (SubTask) taskManager.getSubtask(id);
        final Task epicFromTM = taskManager.getEpic("0004");

//        Helper.printMessage(AFTER_TEST_MSG);
        TaskPrinter.printEpicInfo((Epic) epicFromTM);
//        assertEquals(subtaskFromTM1.toActualStringFoTest(), subTaskForUpdate1.toActualStringFoTest(), "Ошибка обновления задачи");
//        assertEquals(subtaskFromTM2.toActualStringFoTest(), subTaskForUpdate2.toActualStringFoTest(), "Ошибка обновления задачи");

        LocalDateTime dateTimeEpic = epicFromTM.getStartTime();
        assertEquals(LocalDateTime.parse(expect, formatter), dateTimeEpic);
    }


    @Test
    @Tag(value = "InitData")
    void setStartDate(TestInfo info) throws Exception {

        Helper.printMessage("-----------Test: setStartDateToTaskAndDuration ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        //--Test 1 -------------------------------------------------------------------------
        Helper.printMessage(BEFORE_TEST_MSG);
        TaskPrinter.printSortedTasks(taskManager.getAllTasks());

        final Task taskForUpdate = taskManager.getTask("0001");
        ((SimpleTask) taskForUpdate).builder().startTime("15-12-2022 15:20");
        taskManager.updateTask(taskForUpdate);
        Task taskFromTM = taskManager.getTask("0001");
        Helper.printMessage(AFTER_TEST_MSG);
        TaskPrinter.printSortedTasks(taskManager.getAllTasks());
        assertEquals(taskFromTM.toActualStringFoTest(), taskForUpdate.toActualStringFoTest(),
                "Ошибка обновления задачи");
        //--Test 2 -------------------------------------------------------------------------
        Helper.printSeparator();
        Helper.printMessage(BEFORE_TEST_MSG);
        final Task epic = taskManager.getEpic("0004");
        TaskPrinter.printEpicInfo((Epic) epic);

        final SubTask subTaskForUpdate1 = (SubTask) taskManager.getSubtask("0005");
        subTaskForUpdate1.builder().startTime("13-12-2022 10:21");
        taskManager.updateTask(subTaskForUpdate1);

        final SubTask subTaskForUpdate2 = (SubTask) taskManager.getSubtask("0006");
        subTaskForUpdate2.builder().startTime("13-12-2022 09:21");
        taskManager.updateTask(subTaskForUpdate2);

        final SubTask subtaskFromTM1 = (SubTask) taskManager.getSubtask("0005");
        final SubTask subtaskFromTM2 = (SubTask) taskManager.getSubtask("0006");
        final Task epicFromTM = taskManager.getEpic("0004");

        Helper.printMessage(AFTER_TEST_MSG);
        TaskPrinter.printEpicInfo((Epic) epicFromTM);
        assertEquals(subtaskFromTM1.toActualStringFoTest(), subTaskForUpdate1.toActualStringFoTest(), "Ошибка обновления задачи");
        assertEquals(subtaskFromTM2.toActualStringFoTest(), subTaskForUpdate2.toActualStringFoTest(), "Ошибка обновления задачи");

        LocalDateTime dateTimeEpic = epicFromTM.getStartTime();
        assertEquals(LocalDateTime.parse("13-12-2022 09:21", formatter), dateTimeEpic);

        //--Test 3 -------------------------------------------------------------------------
        final SubTask subTaskForUpdate3 = (SubTask) taskManager.getSubtask("0007");
        subTaskForUpdate3.builder().startTime("11-12-2022 19:21").duration(10);
        taskManager.updateTask(subTaskForUpdate3);
        final Task epicFromTM2 = taskManager.getEpic("0004");

        Helper.printSeparator();
        TaskPrinter.printEpicInfo((Epic) epicFromTM2);
        LocalDateTime dateTimeEpic2 = epicFromTM2.getStartTime();
        assertEquals(LocalDateTime.parse("11-12-2022 19:21", formatter), dateTimeEpic2);
        //-------------------
        Helper.printMessage("SortedData:");
        Helper.printSeparator();
        TaskPrinter.printList(taskManager.getPrioritizedTasks());
    }

    @Test
    @Tag(value = "InitData")
    @DescriptorKey("Проверяем установку duration")
    void testSetDuration(TestInfo info) throws Exception {
        Helper.printMessage("-----------Test: setDuration ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        //--Test 1 -------------------------------------------------------------------------
        //меняем duration у простой задачи
        Helper.printMessage(BEFORE_TEST_MSG);
        TaskPrinter.printSortedTasks(taskManager.getAllTasks());
        final Task taskForUpdate = taskManager.getTask("0001");

        ((SimpleTask) taskForUpdate).builder().duration(20);
        taskManager.updateTask(taskForUpdate);

        final Task taskFromTM = taskManager.getTask("0001");

        Helper.printMessage(AFTER_TEST_MSG);
        TaskPrinter.printSortedTasks(taskManager.getAllTasks());
        assertEquals(taskFromTM.toActualStringFoTest(), taskForUpdate.toActualStringFoTest(), "Ошибка обновления задачи");

        //--Test 2 -------------------------------------------------------------------------
        Helper.printSeparator();
        Helper.printMessage("Before tests:");
        final Task epic = taskManager.getEpic("0004");
        TaskPrinter.printEpicInfo((Epic) epic);
        // 1я подзадача
        final SubTask subTaskForUpdate1 = (SubTask) taskManager.getSubtask("0005");
        subTaskForUpdate1.builder().duration(20);
        taskManager.updateTask(subTaskForUpdate1);
        // 2я подзадача
        final SubTask subTaskForUpdate2 = (SubTask) taskManager.getSubtask("0006");
        subTaskForUpdate2.builder().duration(10);
        taskManager.updateTask(subTaskForUpdate2);
        final SubTask subtaskFromTM1 = (SubTask) taskManager.getSubtask("0005");
        final SubTask subtaskFromTM2 = (SubTask) taskManager.getSubtask("0006");
        final Task epicFromTM = taskManager.getEpic("0004");

        Helper.printMessage(AFTER_TEST_MSG);
        TaskPrinter.printEpicInfo((Epic) epicFromTM);
        assertEquals(subtaskFromTM1.toActualStringFoTest(), subTaskForUpdate1.toActualStringFoTest(), "Ошибка обновления задачи");
        assertEquals(subtaskFromTM2.toActualStringFoTest(), subTaskForUpdate2.toActualStringFoTest(), "Ошибка обновления задачи");

        assertEquals(30, epicFromTM.getDuration());

        //--Test 3 -------------------------------------------------------------------------
        // 3я подзадача
        final SubTask subTaskForUpdate3 = (SubTask) taskManager.getSubtask("0007");
        subTaskForUpdate3.builder().duration(5);
        taskManager.updateTask(subTaskForUpdate3);
        final Task epicFromTM2 = taskManager.getEpic("0004");

        Helper.printSeparator();
        TaskPrinter.printEpicInfo((Epic) epicFromTM2);
        assertEquals(35, epicFromTM2.getDuration());
    }

    @Test
    @Tag(value = "InitData")
    void setNegativeDuration(TestInfo info) throws Exception {
        Helper.printMessage("-----------Test: setNegativeDuration ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());

        taskManager.add(new SimpleTask("Task 1", "Description 1"));
        final SimpleTask taskForUpdate = (SimpleTask) taskManager.getTask("0001");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskForUpdate.builder().duration(-20));
        assertEquals("Значение <duration> должно быть больше 0", ex.getMessage());
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("удаление задачи")
    void removeTask(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeTask ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        Task task = taskManager.getTask("0001");
        int countBeforeRemove = taskManager.getAllTasks().size();
        taskManager.removeTask(task.getTaskID());
        int countAfter = taskManager.getAllTasks().size();
        assertEquals(countBeforeRemove - 1, countAfter, "");

        //удаление некорректного id
        TaskException ex = Assertions.assertThrows(TaskRemoveException.class, () -> taskManager.removeTask("004"));
        assertEquals("Ошибка удаления: 'Задача' c id=004 не найден", ex.getDetailMessage().trim(), "Задача не должна быть удалена");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("удаление эпика")
    void removeEpic(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeEpic ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        printTaskManager(BEFORE_TEST_MSG);

        Epic task = (Epic) taskManager.getEpic("0004");

        int countBefore = taskManager.getAll().size();
        taskManager.removeEpic(task.getTaskID());
        int countAfter = taskManager.getAll().size();

        printTaskManager(AFTER_TEST_MSG);

        assertEquals(countBefore - 4, countAfter, "");

    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Удаление подзадачи->проверяем обновление данных у эпика(статус, дата начала, duration)")
    void removeSubtask(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeSubtask ----------------------------------");
        Helper.printMessage(INFO_FORMATTER, info.getDisplayName());
        Epic epic = (Epic) taskManager.getEpic("0009");
        Helper.printMessage("%s %n%s", BEFORE_TEST_MSG, epic.toCompactString());
        TaskPrinter.printSortedTasks(epic.getSubTasks());
        //------ удаляем 1-ю подзадачу -----------------------------------------------------
        SubTask subTask = (SubTask) taskManager.getSubtask("0010");
        taskManager.removeSubtask(subTask.getTaskID());
        Helper.printMessage("%s %n%s", AFTER_TEST_MSG, epic.toCompactString());
        TaskPrinter.printSortedTasks(epic.getSubTasks());

        assertEquals(100, epic.getDuration());
        assertEquals("01-12-2022 10:12", epic.getStartTime().format(formatter));
        assertEquals("09-12-2022 11:00", epic.getEndTime().format(formatter));
        assertEquals("EPIC, 0009, NEW, Эпик 2, Описание эпика 2, 100, 01-12-2022 10:12", epic.toActualStringFoTest(), "Некоректная смена статуса эпика при удалении всех подзадач");
        //------ удаляем 2-ю подзадачу -----------------------------------------------------
        subTask = (SubTask) taskManager.getSubtask("0011");
        taskManager.removeSubtask(subTask.getTaskID());
        Helper.printMessage("%s %n%s", AFTER_TEST_MSG, epic.toCompactString());
        TaskPrinter.printSortedTasks(epic.getSubTasks());

        assertEquals(50, epic.getDuration());
        assertEquals("01-12-2022 10:12", epic.getStartTime().format(formatter));
        assertEquals("01-12-2022 11:02", epic.getEndTime().format(formatter));
//        assertNull(epic.getEndTime());
        assertEquals("EPIC, 0009, NEW, Эпик 2, Описание эпика 2, 50, 01-12-2022 10:12", epic.toActualStringFoTest(), "Некоректная смена статуса эпика при удалении всех подзадач");

    }

    private void printTaskManager(String x) {
        Helper.printMessage(Colors.CURENT.getUnderLine(), x);
        TaskPrinter.printAllTaskManagerList(taskManager);
    }
}