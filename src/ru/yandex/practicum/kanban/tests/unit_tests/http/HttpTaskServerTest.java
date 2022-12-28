package ru.yandex.practicum.kanban.tests.unit_tests.http;

import com.google.gson.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.HttpTaskServer;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.tests.unit_tests.TestLogger;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.tests.TestHelper.INIT_TEST_DATA;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpTaskServerTest implements TestLogger {
    public static final String WRONG_ANSWER_FROM_SERVER = "Ответ от сервера не соответствует ожидаемому.";
    public static final String LOCALHOST_8080_TASKS = "http://localhost:8080/tasks/%s";
    private HttpClient client;
    static Gson gson = new Gson();

    @BeforeAll
    static void beforeAll() throws IOException, TaskException {
        new KVServer().start();
        //загружаем тестовые данные в файл
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(FileHelper.DATA_FILE_NAME_HTTP);
        fileBackedTasksManager.clear();
        TestHelper.addDataFromFile(fileBackedTasksManager, INIT_TEST_DATA);

        new HttpTaskServer(2).start();
//        gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .registerTypeAdapter(LocalDateTime.class, new Helper.LocalDateAdapter())
//                .create();
    }

    @AfterAll
    static void afterAll() {
//        httpTaskServer.stop();
//        kvServer.stop();
    }

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
    }

    @Order(1)
    @DisplayName("Получение задачи/эпика/подзадачи по id")
    @ParameterizedTest(name = "Получить {0} id='{1}', ответ сервера: {2} ")
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
            String url = String.format(LOCALHOST_8080_TASKS + "/?id=%s", type, id);
            HttpResponse<String> response = responseGetHttpRequest(url);

            printResponse(response);

            assertEquals(expected, response.statusCode());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                assertTrue(jsonElement.isJsonObject());
                Task task = gson.fromJson(jsonElement, (Type) getClassOfT(type));
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
    void getTasks(String typeTask, int expect) throws TaskException, IOException, InterruptedException {
        String url = String.format(LOCALHOST_8080_TASKS, typeTask);

        HttpResponse<String> response = responseGetHttpRequest(url);

        printResponse(response);
        int count = getCountFromResponse(response);

        assertEquals(200, response.statusCode());
        assertEquals(expect, count);
    }

    @Order(3)
    @Test
    @DisplayName("Получение истории")
    void getHistory() throws TaskException, IOException, InterruptedException {
        String url = String.format(LOCALHOST_8080_TASKS, "history");

        HttpResponse<String> response = responseGetHttpRequest(url);
        printResponse(response);
        int count = getCountFromResponse(response);

        assertEquals(200, response.statusCode());
        assertEquals(14, count);
    }

    @Order(4)
    @DisplayName("POST запрос : add или update")
    @ParameterizedTest(name = "POST запрос: {0} {1} id=''{2}'' , ответ: {8}")
    @CsvSource({
            "add    , task    , ''     , httpTaskName1           , httpTaskDescription1           , 0  , ''                    ,      , 200",
            "add    , subtask , ''     , httpSubTaskName1        , httpSubTaskDescription1        , 0  , ''                    ,      , 400",
            "add    , subtask , ''     , httpSubTaskName1        , httpSubTaskDescription1        , 0  , ''                    , 0001 , 400",
//          "add    , task    , '0001' , httpTaskName1           , httpTaskDescription1           , 0  , ''                    ,      , 400" ,
            "update , task    , '0051' , httpTaskName1           , httpTaskDescription1           , 0  , ''                    ,      , 400",
            "add    , epic    , ''     , httpEpicName1           , httpEpicDescription1           , 0  , ''                    ,      , 200",
            "add    , subtask , ''     , httpTaskName1           , httpTaskDescription1           , 40 , '01-03-2022 10:00:00' , 0004 , 200",
            "update , task    , 0001   , 'updated httpTaskName1' , 'updated httpTaskDescription1' , 6  , 10-11-2022 15:21:00   ,      , 200",
            "update , task    , 0015   , 'new httpTaskName8'     , ''                             , 5  , 05-11-2022 15:21:00   ,      , 200",
            "update , subtask , 0007   , ''                      , ''                             , 5  , 06-11-2022 15:21:00   , 0009 , 200"
    })
    void postRequest(String op, String typeTask
            , String id, String name, String description, int d, String startTime, String epicID
            , int responseCodeExpected
    ) throws TaskException, IOException, InterruptedException {
        String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        int countBefore = getCountTasks(url);
        //------------------------------------
        Task newTask = createTask(typeTask, id, name, description, d, startTime, epicID);
        if (newTask != null) {
            String json = gson.toJson(newTask);
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(body)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //-------------------------------------

            printResponse(response);
            assertEquals(responseCodeExpected, response.statusCode());
            if (response.statusCode() == 200) {
                if (op.equals("add")) {
                    int countAfter = getCountTasks(url);
                    assertEquals(countBefore + 1, countAfter);
                }
                if (op.equals("update")) {
                    Task task = getCountTaskFromServer(typeTask, url + "/?id=" + id);
                    int countAfter = getCountTasks(url);
                    assertEquals(countBefore, countAfter);
                    assertEquals(task.toString(), newTask.toString());
                }
            }
        }

    }

    @Order(5)
    @ParameterizedTest(name = "Получение подзадач для эпика id={0} -> подзадач {1} шт")
    @CsvSource({"0004, 4", "0008, 0"})
    @DisplayName("Получение подзадач для эпика")
    void getEpicSubtasks(String id, int countSubtasks) throws IOException, InterruptedException, TaskException {
        String path = String.format(LOCALHOST_8080_TASKS, "subtask/epic/?id=%s");
        String url = String.format(path, id);
        HttpResponse<String> response = responseGetHttpRequest(url);
        int count = getCountFromResponse(response);
        Helper.printMessage(response.body());
        assertEquals(200, response.statusCode());
        assertEquals(countSubtasks, count);
    }

    @Order(6)
    @Test
    @DisplayName("Получение сортированного списка")
    void getPrioritized() {
        try {
            String url = String.format(LOCALHOST_8080_TASKS, "");
            HttpResponse<String> response = responseGetHttpRequest(url);
            // проверяем, успешно ли обработан запрос
            printResponse(response);
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());

                if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
                    Helper.printMessage(WRONG_ANSWER_FROM_SERVER);
                    return;
                }

                JsonArray jsonObjects = jsonElement.getAsJsonArray();
                List<Task> list = new ArrayList<>();
                List<Task> listSorted = new ArrayList<>();
                for (int i = 0; i < jsonObjects.size(); i++) {
                    Task task = gson.fromJson(jsonObjects.get(i), (Type) getClassOfT(jsonObjects.get(i).getAsJsonObject().get("taskType").getAsString()));
                    list.add(task);
                    listSorted.add(task);
                    Helper.printMessage(task.toCompactString());
                }
                listSorted.sort(Comparator.comparing((Task task) -> Optional.ofNullable(task.getStartTime()).orElse(LocalDateTime.MAX))
                        .thenComparing(Task::getTaskID));

                assertEquals(listSorted, list);
            } else {
                Helper.printMessage("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            Helper.printMessage("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
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
    void deleteTask(String typeTask, String id) throws IOException, InterruptedException, TaskException {
        String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        int countBefore = getCountTasks(url);

        String urlDel = String.format(url + "/?id=%s", id);
        HttpResponse<String> response = responseDeleteHttpRequest(urlDel);
        printResponse(response);
        assertEquals(200, response.statusCode());

        int countAfter = getCountTasks(url);
        assertEquals(countBefore - 1, countAfter);
    }

    @Order(8)
    @ParameterizedTest(name = "Удаление delAll- {0}s")
    @DisplayName("Удаление все задач/подзадач/эпиков")
    @CsvSource({
            "task", "subtask", "epic"
    })
    void delete(String typeTask) throws TaskException, IOException, InterruptedException {
        String url = String.format(LOCALHOST_8080_TASKS, typeTask);
        String urlTask = String.format(LOCALHOST_8080_TASKS, "task");
        String urlSubtask = String.format(LOCALHOST_8080_TASKS, "subtask");
        String urlEpic = String.format(LOCALHOST_8080_TASKS, "epic");
        int countBefore = getCountTasks(url);
        int countTask = getCountTasks(urlTask);
        int countEpic = getCountTasks(urlEpic);
        int countSubtask = getCountTasks(urlSubtask);
        int sumBefore = countEpic + countTask + countSubtask;

        HttpResponse<String> response = responseDeleteHttpRequest(url);
        printResponse(response);

        int countTaskAfter = getCountTasks(urlTask);
        int countEpicAfter = getCountTasks(urlEpic);
        int countSubtaskAfter = getCountTasks(urlSubtask);
        int sumAfter = countEpicAfter + countTaskAfter + countSubtaskAfter;
        int countAfter = getCountTasks(url);

        assertEquals(200, response.statusCode());
        assertEquals(0, countAfter);
        assertEquals(sumBefore - countBefore, sumAfter);
    }


    private void printResponse(HttpResponse<String> response) {
        Helper.printMessage("Response code: %d -> %s", response.statusCode(), response.body());
    }

    private Task createTask(String typeTask, String id, String name, String description, int d, String startTime, String epicID) {
        Task newTask = null;
        switch (typeTask) {
            case "task":
                newTask = new Task(name, description, d, startTime);
                break;
            case "epic":
                newTask = new Epic(name, description);
                break;
            case "subtask":
                newTask = new SubTask(name, description, d, startTime, epicID);
                break;
            default:
                Helper.printMessage("Некорректне тестовые данные");
        }
        if (!id.isBlank()) newTask.setTaskID(id);
        return newTask;
    }

    private Class getClassOfT(String typeTask) {
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

    private int getCountTasks(String url) throws IOException, InterruptedException, TaskException {
        HttpResponse<String> response = responseGetHttpRequest(url);
        return getCountFromResponse(response);
    }

    private Task getCountTaskFromServer(String typeTask, String url) throws IOException, InterruptedException, TaskException {
        HttpResponse<String> beforeTest = responseGetHttpRequest(url);
        JsonElement jsonElement = JsonParser.parseString(beforeTest.body());

        if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
            throw new TaskException(WRONG_ANSWER_FROM_SERVER);
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return (Task) gson.fromJson(jsonObject, getClassOfT(typeTask));
    }

    private int getCountFromResponse(HttpResponse<String> response) throws TaskException {
        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-объект
            throw new TaskException(WRONG_ANSWER_FROM_SERVER);
        }
        JsonArray jsonObjects = jsonElement.getAsJsonArray();
        return jsonObjects.size();
    }

    private HttpResponse<String> responseGetHttpRequest(String urlStr) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> responseDeleteHttpRequest(String urlStr) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}