package ru.yandex.practicum.kanban.tests.unit_tests.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.HttpTaskServer;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.GsonAdapter;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.tests.TestHelper.INIT_TEST_DATA;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpTaskServerTest implements TestLogger {
    public static final String WRONG_ANSWER_FROM_SERVER = "Ответ от сервера не соответствует ожидаемому.";
    public static final String LOCALHOST_8080_TASKS = "http://localhost:8080/tasks/%s";
    private static KVServer kvServer;
    private HttpClient client;
    private static Gson gson;
    Type typeToken = new TypeToken<List<Task>>() {
    }.getType();

    @BeforeAll
    static void beforeAll() throws IOException, TaskException {
        kvServer = new KVServer();
        kvServer.start();
        //загружаем тестовые данные в файл
        final FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(FileHelper.DATA_FILE_NAME_HTTP);
        fileBackedTasksManager.clear();
        TestHelper.addDataFromFile(fileBackedTasksManager, INIT_TEST_DATA);
        new HttpTaskServer(2).start();
        gson = GsonAdapter.getGsonWithAdapter();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
    }

    @Order(1)
    @DisplayName("Получение задачи/эпика/подзадачи по id")
    @ParameterizedTest(name = "Получить {0} id=''{1}'', ответ сервера: {2} ")
    @CsvSource({
            "task    , 0001 , 200",
            "task    , 0004 , 400",
            "subtask , 0005 , 200",
            "subtask , 0002 , 400",
            "epic    , 0004 , 200",
            "epic    , 0001 , 400",
    })
    void getTask(String type, String id, int expected) {
        try {
            final String url = String.format(LOCALHOST_8080_TASKS + "/?id=%s", type, id);
            final HttpResponse<String> response = responseGetHttpRequest(url);

            printResponse(response);

            assertEquals(expected, response.statusCode());
            if (response.statusCode() == 200) {
                final JsonElement jsonElement = JsonParser.parseString(response.body());
                assertTrue(jsonElement.isJsonObject());

                final Task task = gson.fromJson(jsonElement, getClassOfT(type));

                assertEquals(type, task.getClass().getSimpleName().toLowerCase());

                Helper.printMessage(task.toActualStringFoTest());
                Helper.printEmptySting();
            }
        } catch (IOException | InterruptedException e) {
            Helper.printMessage("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Order(2)
    @DisplayName("Получение задач/эпиков/подзадач")
    @ParameterizedTest(name = "Получить все {0} , количество: {1} ")
    @CsvSource({"task ,4", "subtask, 6", "epic, 4"})
    void getTasks(String typeTask, int expected) throws IOException, InterruptedException {
        final String url = String.format(LOCALHOST_8080_TASKS, typeTask);

        final HttpResponse<String> response = responseGetHttpRequest(url);

        printResponse(response);
        assertEquals(200, response.statusCode());
        final List<Task> list = gson.fromJson(response.body(), typeToken);

        assertEquals(expected, list.size());
        if (!list.isEmpty()) {
            assertEquals(typeTask, list.get(0).getClass().getSimpleName().toLowerCase());
        }
    }

    @Order(3)
    @Test
    @DisplayName("Получение истории")
    void getHistory() throws IOException, InterruptedException {
        final String url = String.format(LOCALHOST_8080_TASKS, "history");

        final HttpResponse<String> response = responseGetHttpRequest(url);
        printResponse(response);
        assertEquals(200, response.statusCode());
        final List<Task> list = gson.fromJson(response.body(), typeToken);

        assertEquals(14, list.size());
    }

    @Order(4)
    @Test
    @DisplayName("Получение сортированного списка")
    void getPrioritized() throws TaskException {
        try {
            final String url = String.format(LOCALHOST_8080_TASKS, "");
            final HttpResponse<String> response = responseGetHttpRequest(url);
            // проверяем, успешно ли обработан запрос
            printResponse(response);
            if (response.statusCode() == 200) {
                final JsonElement jsonElement = JsonParser.parseString(response.body());

                if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
                    Helper.printMessage(WRONG_ANSWER_FROM_SERVER);
                    return;
                }
                final List<Task> resultList = gson.fromJson(jsonElement, typeToken);

                final TaskManager fileBackedTasksManager = FileBackedTasksManager
                        .loadFromFile(TestHelper.PATH_TEST_FILES + TestHelper.DATA_FILE_NAME_EMPTY);
                fileBackedTasksManager.clear();
                TestHelper.addDataFromFile(fileBackedTasksManager, INIT_TEST_DATA);

                final List<Task> expectedList = fileBackedTasksManager.getPrioritizedTasks();

                assertEquals(expectedList, resultList);

                fileBackedTasksManager.clear();
            } else {
                Helper.printMessage("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            Helper.printMessage("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Order(5)
    @DisplayName("POST запрос : add или update")
    @ParameterizedTest(name = "POST запрос: {0} {1} id=''{2}'' , ответ: {8}")
    @CsvSource({
            "add    , task    , ''     , httpTaskName1           , httpTaskDescription1           , 0  , ''                  ,      , 201",
            "add    , subtask , ''     , httpSubTaskName1        , httpSubTaskDescription1        , 0  , ''                  ,      , 400",
            "add    , subtask , ''     , httpSubTaskName1        , httpSubTaskDescription1        , 0  , ''                  , 0001 , 400",
            "add    , epic    , ''     , httpEpicName1           , httpEpicDescription1           , 0  , ''                  ,      , 201",
            "add    , subtask , ''     , httpTaskName1           , httpTaskDescription1           , 40 , 01-03-2022 10:00:00 , 0004 , 201",
            "update , task    , 0001   , 'updated httpTaskName1' , 'updated httpTaskDescription1' , 6  , 10-11-2022 15:21:00 ,      , 201",
            "update , task    , 0015   , 'new httpTaskName8'     , ''                             , 5  , 05-11-2022 15:21:00 ,      , 201",
            "update , task    , 0051   , httpTaskName1           , httpTaskDescription1           , 0  , ''                  ,      , 400",
            "update , subtask , 0007   , ''                      , ''                             , 5  , 06-11-2022 15:21:00 , 0009 , 201"
    })
    void postRequest(String op, String typeTask
            , String id, String name, String description, int duration, String startTime, String epicID
            , int expectedResponseCode
    ) throws IOException, InterruptedException {
        final String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        final int countBefore = getCountTasks(url);

        final Task newTask = createTask(typeTask, id, name, description, duration, startTime, epicID);
        if (newTask != null) {
            final HttpResponse<String> response = responsePostHttpRequest(url, gson.toJson(newTask));

            printResponse(response);
            assertEquals(expectedResponseCode, response.statusCode());
            if (response.statusCode() == 201) {
                if (op.equals("add")) {
                    final int countAfter = getCountTasks(url);
                    assertEquals(countBefore + 1, countAfter);
                }
                if (op.equals("update")) {
                    final HttpResponse<String> beforeTest = responseGetHttpRequest(url + "/?id=" + id);
                    gson.fromJson(beforeTest.body(), getClassOfT(typeTask));
                    final Task task = gson.fromJson(beforeTest.body(), getClassOfT(typeTask));
                    final int countAfter = getCountTasks(url);
                    assertEquals(countBefore, countAfter);
                    assertEquals(task.toString(), newTask.toString());
                }
            }
        }
    }

    @Order(6)
    @ParameterizedTest(name = "Получение подзадач для эпика id={0} -> подзадач {1} шт")
    @CsvSource({"0004, 4", "0008, 0"})
    @DisplayName("Получение подзадач для эпика")
    void getEpicSubtasks(String id, int countSubtasks) throws IOException, InterruptedException {
        final String path = String.format(LOCALHOST_8080_TASKS, "subtask/epic/?id=%s");
        final String url = String.format(path, id);
        final HttpResponse<String> response = responseGetHttpRequest(url);

        assertEquals(200, response.statusCode());
        final List<Task> list = gson.fromJson(response.body(), typeToken);
        Helper.printMessage(response.body());
        assertEquals(countSubtasks, list.size());
        if (!list.isEmpty()) {
            assertEquals("SubTask", list.get(0).getClass().getSimpleName());
        }
    }

    @Order(7)
    @DisplayName("Удаление по id задачи/подзадачи/эпика")
    @ParameterizedTest(name = "Удалить {0} id = {1}")
    @CsvSource({
            "task    , 0001",
            "subtask , 0005",
            "epic    , 0004"
    })
    void deleteTask(String typeTask, String id) throws IOException, InterruptedException {
        final String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        final int countBefore = getCountTasks(url);

        final String urlDel = String.format(url + "/?id=%s", id);
        final HttpResponse<String> response = responseDeleteHttpRequest(urlDel);
        printResponse(response);
        assertEquals(200, response.statusCode());

        final int countAfter = getCountTasks(url);
        assertEquals(countBefore - 1, countAfter);
    }


    @Order(8)
    @ParameterizedTest(name = "Удаление delAll- {0}s")
    @DisplayName("Удаление все задач/подзадач/эпиков")
    @CsvSource({
            "task", "subtask", "epic"
    })
    void delete(String typeTask) throws IOException, InterruptedException {
        final String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        final String urlTask = String.format(LOCALHOST_8080_TASKS, "task");
        final String urlSubtask = String.format(LOCALHOST_8080_TASKS, "subtask");
        final String urlEpic = String.format(LOCALHOST_8080_TASKS, "epic");
        final int countBefore = getCountTasks(url);
        final int countTask = getCountTasks(urlTask);
        final int countEpic = getCountTasks(urlEpic);
        final int countSubtask = getCountTasks(urlSubtask);
        final int sumBefore = countEpic + countTask + countSubtask;

        final HttpResponse<String> response = responseDeleteHttpRequest(url);
        printResponse(response);
        assertEquals(200,response.statusCode());

        final int countTaskAfter = getCountTasks(urlTask);
        final int countEpicAfter = getCountTasks(urlEpic);
        final int countSubtaskAfter = getCountTasks(urlSubtask);
        final int sumAfter = countEpicAfter + countTaskAfter + countSubtaskAfter;
        final int countAfter = getCountTasks(url);

        assertEquals(0, countAfter);
        assertEquals(sumBefore - countBefore, sumAfter);
    }


    private void printResponse(final HttpResponse<String> response) {
        Helper.printMessage("Response code: %d -> %s", response.statusCode(), response.body());
    }

    private Task createTask(final String typeTask, final String id, final String name,
                            final String description, final int duration, final String startTime, final String epicID) {
        Task newTask = null;
        switch (typeTask) {
            case "task":
                newTask = new Task(name, description, duration, startTime);
                break;
            case "epic":
                newTask = new Epic(name, description);
                break;
            case "subtask":
                newTask = new SubTask(name, description, duration, startTime, epicID);
                break;
            default:
                Helper.printMessage("Некорректне тестовые данные");
        }
        if (!id.isBlank() && newTask != null) {
            newTask.setTaskID(id);
        }
        return newTask;
    }

    private Class<? extends Task> getClassOfT(final String typeTask) {
        switch (typeTask) {
            case "task":
            case "TASK":
                return Task.class;
            case "epic":
            case "EPIC":
                return Epic.class;
            case "subtask":
            case "SUB_TASK":
                return SubTask.class;
            default:
                return null;
        }
    }

    private int getCountTasks(final String url) throws IOException, InterruptedException {
        final HttpResponse<String> response = responseGetHttpRequest(url);
        List<Task> list = gson.fromJson(response.body(), typeToken);
        return list.size();
    }

    private HttpResponse<String> responseGetHttpRequest(final String urlStr) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> responseDeleteHttpRequest(final String urlStr) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> responsePostHttpRequest(final String url, final String json) throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(body)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}