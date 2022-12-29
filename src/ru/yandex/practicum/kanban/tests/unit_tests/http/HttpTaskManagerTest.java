package ru.yandex.practicum.kanban.tests.unit_tests.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.http.KVTaskClient;
import ru.yandex.practicum.kanban.managers.HttpTaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.commands.TestAddCommand;
import ru.yandex.practicum.kanban.tests.unit_tests.TaskManagerTest;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    public static final String KV_SERVER_HTTP_LOCALHOST_8078 = "http://localhost:8078/";
    private static KVServer kvServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
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
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Order(3)
    @Test
    @Tag("InitData")
    @DisplayName("Загружаем список задач.")
    void loadData() {
        assertEquals(4, taskManager.getAllTasks().size());
        assertEquals(4, taskManager.getAllEpics().size());
        assertEquals(6, taskManager.getAllSubTasks().size());
        assertEquals(14, taskManager.getHistory().size());
    }

    @Order(2)
    @Test
    @DisplayName("Сохранение - в пустое хранилище.")
    void saveToEmptyFile() throws TaskException, URISyntaxException {
        final Task task = new Task("Task1", "TAsk 1 description");
        final Task task2 = new Task("Task2", "TAsk 2 description");
        taskManager.add(task);
        taskManager.add(task2);

        final KVTaskClient kvTaskClient = new KVTaskClient(new URI(KV_SERVER_HTTP_LOCALHOST_8078));
        final String res = kvTaskClient.load(TaskType.TASK.toString());
        final List<Task> resL = gson.fromJson(res, (Type) List.class);

        assertEquals(2, resL.size());
    }

    Gson gson = new Gson();

    @Test
    @Order(4)
    @Tag("InitData")
    @DisplayName("Сохранение")
    void save() throws TaskException, URISyntaxException {
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

        final KVTaskClient kvTaskClient = new KVTaskClient(new URI(KV_SERVER_HTTP_LOCALHOST_8078));
        String res = kvTaskClient.load(TaskType.TASK.toString());
        final JsonArray tasks = JsonParser.parseString(res).getAsJsonArray();

        res = kvTaskClient.load(TaskType.EPIC.toString());
        final JsonArray epics = JsonParser.parseString(res).getAsJsonArray();

        res = kvTaskClient.load(TaskType.SUB_TASK.toString());
        final JsonArray subtasks = JsonParser.parseString(res).getAsJsonArray();

        res = kvTaskClient.load("history");
        final JsonArray history = JsonParser.parseString(res).getAsJsonArray();

        assertEquals(beforeTasks + 2, tasks.size());
        assertEquals(beforeEpics + 1, epics.size());
        assertEquals(beforeSubTasks + 1, subtasks.size());
        assertEquals(beforeHistory + 4, history.size());
    }
}