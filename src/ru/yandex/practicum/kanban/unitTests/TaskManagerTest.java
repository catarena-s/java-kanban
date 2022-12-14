package ru.yandex.practicum.kanban.unitTests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.test.commands.TestAddCommand;
import ru.yandex.practicum.kanban.test.commands.TestRemoveCommand;
import ru.yandex.practicum.kanban.test.commands.TestUpdateCommand;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.unitTests.TestHelper.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public static final String ERROR_MSG_TASK_NOT_FOUND = "Задача не найдена.";
    public static final String ERROR_MSG_TASK_NOT_EQUALS = "Задачи не совпадают.";
    public static final String ERROR_MASSAGES_NOT_EQUALS = "Сообщения об ошибке не совпадают";
    public static final String INFO_FORMATER = "Info: %s";
    T taskManager;

    protected void init(int config, String... args) {
        Managers managers = new Managers(config, args);
        taskManager = (T) managers.getDefault();
    }

    @Test
    @DisplayName("Добавление задач, эпиков и подзадач")
    void addTask() throws TaskGetterException, TaskAddException, IOException {
        Helper.printMessage("-----------Test: addTask ----------------------------------");
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER));
        int before = taskManager.getAll().size();
        for (int i = 0; i < testLines.length; i++) {
            String line = testLines[i];
            if (!line.isBlank()) {
                Helper.printMessage("Test #%d: %s", before + i + 1, line);
                Task task = TestAddCommand.executeString(line, taskManager);
                final Task savedTask = taskManager.getById(task.getTaskID());

                assertNotNull(savedTask, ERROR_MSG_TASK_NOT_FOUND);
                assertEquals(task, savedTask, ERROR_MSG_TASK_NOT_EQUALS);

                final List<Task> tasks = taskManager.getAll();
                assertEquals(before + i + 1, tasks.size(), "Неверное количество задач.");
            }
        }
        Printer.printAllTaskManagerList(taskManager);
        assertEquals(before + 8, taskManager.getAll().size(), "Неверное количество задач.");

    }

    @Test
    @DisplayName("Добавление подзадач с некорректными тестовыми данными")
    @Tag(value = "EmptyFile")
    void addSubTaskWrongData() throws IOException, TaskGetterException, TaskAddException {
        Helper.printMessage("-----------Test: addSubTaskWrongData ----------------------------------");
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(TEST_ADD_TO_MANAGER_WRONG_DATA));
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
        String expected = TestHelper.getExpectation(line).trim();
        if (line.isBlank()) return;
        String testLine = line.substring(0, line.indexOf("["));
        Helper.printMessage("Test : %s%n", testLine);
        TaskException ex = Assertions.assertThrows(classException,
                () -> TestAddCommand.executeString(testLine, taskManager));
        assertEquals(expected.trim(), ex.getDetailMessage().trim(), ERROR_MASSAGES_NOT_EQUALS);
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получение всех подзадач по эпику")
    void getAllSubtaskByEpic(TestInfo info) throws TaskGetterException {
        Helper.printMessage("-----------Test: getAllSubtaskByEpic ----------------------------------");
        Helper.printMessage(INFO_FORMATER, info.getDisplayName());
        Epic epic = (Epic) taskManager.getEpic("0004");
        List<Task> subTasks = taskManager.getAllSubtaskByEpic(epic);
        assertEquals(subTasks.size(), epic.getSubTasks().size(), "Ошибка получения подзадач у эпика");
    }

    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получение всех подзадач по эпику в пустом таск-менеджере")
    void getTaskFromEmptyManager(TestInfo info) {
        Helper.printMessage("-----------Test: getTaskFromEmptyManager ----------------------------------");
        Helper.printMessage(INFO_FORMATER, info.getDisplayName());
        TaskException ex = Assertions.assertThrows(TaskGetterException.class, () -> taskManager.getEpic("004"));
        assertEquals("Ошибка получения: 'Эпик' - отсутствуют", ex.getDetailMessage().trim(), "Эпик не должен быть получен");
    }

    @Test
    @Tag(value = "InitData")
    @DisplayName("Получение всех задач")
    void getAll(TestInfo info) {
        Helper.printMessage("-----------Test: getAll ----------------------------------");
        Helper.printMessage(INFO_FORMATER, info.getDisplayName());
        List<Task> all = taskManager.getAll();
        List<Task> subTasks = taskManager.getAllSubTasks();
        List<Task> epics = taskManager.getAllEpics();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(4, tasks.size(), "Ошибка получения задач");
        assertEquals(3, epics.size(), "Ошибка получения эпиков");
        assertEquals(4, subTasks.size(), "Ошибка получения подзадач");
        assertEquals(11, all.size(), "Ошибка получения всех задач");
    }


    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Получение всех задач")
    void getAllFromEmptyManager(TestInfo info) {
        Helper.printMessage("-----------Test: getAllFromEmptyManager ----------------------------------");
        Helper.printMessage(INFO_FORMATER, info.getDisplayName());
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
        Helper.printMessage(INFO_FORMATER, info.getDisplayName());
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_FILE));
        int indexTest = 1;
        for (String line : testLines) {
            Helper.printMessage(Colors.UnderLine, "Test #%d: %s", indexTest++, line);
            Task taskUpdated = TestUpdateCommand.executeString(line, taskManager, true);
            Helper.printMessage("Task after update : %s\n", taskUpdated.toCompactString());
            Task taskGeted = taskManager.getById(taskUpdated.getTaskID());
            assertEquals(taskUpdated.toActualStringFoTest(), taskGeted.toActualStringFoTest(), "Строки отличаются.");
        }
    }

    @Test
    @Tag(value = "InitData")
    void updateEpicStatus(TestInfo info) throws IOException, TaskGetterException, TaskAddException, TaskRemoveException {
        Helper.printMessage("-----------Test: updateEpicStatus ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
        String[] testLines = FileHelper.readFromFileToArray(TestHelper.getPath(DATA_UPDATE_EPIC_STATUS));
        String epicId = testLines[0].trim();
        Epic epic = (Epic) taskManager.getEpic(epicId);
        Helper.printMessage("Before tests: %n%s", epic.toCompactString());
        Printer.printSortedTasks(epic.getSubTasks());
        for (int i = 1; i < testLines.length; i++) {
            String line = testLines[i];
            String expected = TestHelper.getExpectation(line).trim();
            String testLine = line.substring(0, line.indexOf("["));
            Helper.printMessage(Colors.UnderLine, "Test #%d: %s", i, testLine);
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
            Helper.printMessage("After test: %n%s", epic.toCompactString());
            Printer.printSortedTasks(epic.getSubTasks());
            assertEquals(expected.trim(), epic.toActualStringFoTest().trim(), "Некорректное обновление.");
        }
    }


    @Test
    @Tag(value = "InitData")
    void setStartDateToTaskAndDuration(TestInfo info) throws TaskGetterException, TaskAddException {
        Helper.printMessage("-----------Test: setStartDateToTaskAndDuration ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
        final Task task = new Task("Task 1", "Description 1");
        taskManager.add(task);
        Task taskForUpdate = taskManager.getTask("0001");
        taskForUpdate.builder().startTime("15/12/22 15:20").duration(20);

        taskManager.updateTask(taskForUpdate);
        Task taskGetted = taskManager.getTask("0001");
        assertEquals(taskGetted.toActualStringFoTest(), taskForUpdate.toActualStringFoTest(), "Ошибка обновления задачи");
    }

    @Test
    @Tag(value = "InitData")
    void setNegativeDuration(TestInfo info) throws TaskGetterException, TaskAddException {
        Helper.printMessage("-----------Test: setNegativeDuration ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
        taskManager.add(new Task("Task 1", "Description 1"));
        final Task taskForUpdate = taskManager.getTask("0001");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskForUpdate.builder().duration(-20));
        assertEquals("Значение <duration> должно быть больше 0", ex.getMessage());
    }

    @Test
    @Tag(value = "InitData")
    void removeTask(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeTask ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
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
    void removeEpic(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeEpic ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
        Helper.printMessage(Colors.UnderLine, "Before:");
        Printer.printAllTaskManagerList(taskManager);

        Epic task = (Epic) taskManager.getEpic("0004");

        int countBefore = taskManager.getAll().size();
        taskManager.removeEpic(task.getTaskID());
        int countAfter = taskManager.getAll().size();

        Helper.printMessage(Colors.UnderLine, "\nAfter:");
        Printer.printAllTaskManagerList(taskManager);

        assertEquals(countBefore - 4, countAfter, "");

    }

    @Test
    @Tag(value = "InitData")
    void removeSubtask(TestInfo info) throws TaskGetterException, TaskRemoveException {
        Helper.printMessage("-----------Test: removeSubtask ----------------------------------");
        Helper.printMessage(INFO_FORMATER,info.getDisplayName());
        Epic epic = (Epic) taskManager.getEpic("0009");
        Helper.printMessage("Before tests: %n%s", epic.toCompactString());
        Printer.printSortedTasks(epic.getSubTasks());

        SubTask subTask = (SubTask) taskManager.getSubtask("0010");
        taskManager.removeSubtask(subTask.getTaskID());
        Helper.printMessage("After tests: %n%s", epic.toCompactString());
        Printer.printSortedTasks(epic.getSubTasks());

        assertEquals("0009, EPIC, NEW, Эпик 2, Описание эпика 2", epic.toActualStringFoTest(), "Некоректная смена статуса эпика при удалении всех подзадач");
    }
}