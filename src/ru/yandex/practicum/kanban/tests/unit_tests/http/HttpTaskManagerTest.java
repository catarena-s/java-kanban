package ru.yandex.practicum.kanban.tests.unit_tests.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.managers.HttpTaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.tests.unit_tests.TaskManagerTest;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.GsonAdapter;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    public static final String KV_SERVER_HTTP_LOCALHOST_8078 = "http://localhost:8078/";
    private static KVServer kvServer;
    static Gson gson;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        gson = GsonAdapter.getGsonWithAdapter();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @BeforeEach
    void setUp(TestInfo info) throws IOException, TaskException {
        init(3);
        if (info.getTags().contains("InitData")) {
            final List<String> testLines = FileHelper.readFromFile(TestHelper.getPath(TestHelper.INIT_TEST_DATA));
            for (String line : testLines) {
                final String testLine = (line.contains("[")) ? line.substring(0, line.indexOf("[")) : line;
                if (testLine.isBlank()) continue;
                TestAddCommand.executeString(testLine, taskManager);
            }
        }
    }

    @AfterEach
    void afterEach() {
        taskManager.clear();
    }


    @Order(1)
    @Test
    @DisplayName("Загружаем пустой список задач.")
    void loadDataFromEmpty() {
        HttpTaskManager newManager = new HttpTaskManager(KV_SERVER_HTTP_LOCALHOST_8078);
        assertEquals(0, newManager.getAllTasks().size());
        assertEquals(0, newManager.getAllEpics().size());
        assertEquals(0, newManager.getAllSubTasks().size());
        assertEquals(0, newManager.getHistory().size());
    }

    @Order(2)
    @Test
    @Tag("InitData")
    @DisplayName("Загружаем список задач.")
    void loadData() {
        HttpTaskManager newManager = new HttpTaskManager(KV_SERVER_HTTP_LOCALHOST_8078);

        assertEquals(taskManager.getAllTasks().size(), newManager.getAllTasks().size());
        assertEquals(taskManager.getAllEpics().size(), newManager.getAllEpics().size());
        assertEquals(taskManager.getAllSubTasks().size(), newManager.getAllSubTasks().size());
        assertEquals(taskManager.getHistory().size(), newManager.getHistory().size());
    }

    @Order(3)
    @Test
    @DisplayName("Сохранение - в пустое хранилище.")
    void saveToEmptyFile() throws TaskException {
        final Task task = new Task("Task1", "TAsk 1 description", 5, "12-05-2022 15:02:00");
        final Task task2 = new Task("Task2", "TAsk 2 description");
        taskManager.add(task);
        taskManager.add(task2);

        final Epic epic1 = new Epic("EPIC1", "EPIC 1 description");
        taskManager.add(epic1);
        final SubTask subTask1 = new SubTask("Task2", "TAsk 2 description,", 5, "13-05-2022 15:02:00", epic1.getTaskID());
        final SubTask subTask2 = new SubTask("Task2", "TAsk 2 description", 5, "12-04-2022 15:02:00", epic1.getTaskID());
        taskManager.add(subTask1);
        taskManager.add(subTask2);

        HttpTaskManager newManager = new HttpTaskManager(KV_SERVER_HTTP_LOCALHOST_8078);

        assertEquals(2, newManager.getAllTasks().size());
        assertEquals(1, newManager.getAllEpics().size());
        assertEquals(2, newManager.getAllSubTasks().size());
    }

    @Test
    @Order(4)
    @Tag("InitData")
    @DisplayName("Сохранение")
    void save() throws TaskException {
        final int beforeTasks = taskManager.getAllTasks().size();
        final int beforeEpics = taskManager.getAllEpics().size();
        final int beforeSubTasks = taskManager.getAllSubTasks().size();
        final int beforeHistory = taskManager.getHistory().size();

        final Task task = new Task("Task1", "Task 1 description");
        taskManager.add(task);
        final Task task2 = new Task("Task2", "Task 2 description");
        taskManager.add(task2);
        final Epic epic1 = new Epic("Epic1", "Epic 1 description");
        taskManager.add(epic1);
        final SubTask subtask = new SubTask("SubTask 1", "SubTask1 description", epic1.getTaskID());
        taskManager.add(subtask);

        HttpTaskManager newManager = new HttpTaskManager(KV_SERVER_HTTP_LOCALHOST_8078);

        assertEquals(beforeTasks + 2, newManager.getAllTasks().size());
        assertEquals(beforeEpics + 1, newManager.getAllEpics().size());
        assertEquals(beforeSubTasks + 1, newManager.getAllSubTasks().size());
        assertEquals(beforeHistory + 4, newManager.getHistory().size());
    }
}