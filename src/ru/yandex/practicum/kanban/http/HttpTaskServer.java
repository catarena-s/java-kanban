package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.GsonAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String WRONG_COMMAND = "Некорректная команда.";
    private final TaskManager tasksManager;
    private final Gson gson;
    private final HttpServer httpServer;

    private enum Endpoint {
        GET_TASKS, GET_PRIORITIZED, GET_TASK, GET_EPIC_SUBTASKS, GET_HISTORY,
        POST_TASK,
        DELETE_TASKS, DELETE_TASK,
        UNKNOWN
    }

    public HttpTaskServer(final int configKey) throws IOException, TaskException {
        //если configKey = 2, то FileBackedTasksManager работать с файлом DATA_FILE_NAME_HTTP
        //для других ключей имя файла игнорируется
        final Managers managers = new Managers(configKey, FileHelper.DATA_FILE_NAME_HTTP);
        tasksManager = managers.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        gson = GsonAdapter.getGsonWithAdapter();
    }

    public void start() {
        httpServer.start();
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(final HttpExchange exchange) throws IOException {
            final Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());
            try {
                switch (endpoint) {
                    case GET_PRIORITIZED: {
                        handleGetPrioritizedList(exchange);
                        break;
                    }
                    case GET_TASK:
                        handleGetTask(exchange);
                        break;
                    case GET_TASKS:
                        handleGetTaskList(exchange);
                        break;
                    case DELETE_TASK:
                        handleDeleteTask(exchange);
                        break;
                    case DELETE_TASKS:
                        handleDeleteTaskList(exchange);
                        break;
                    case POST_TASK:
                        handlePost(exchange);
                        break;
                    case GET_HISTORY:
                        handleHistory(exchange);
                        break;
                    case GET_EPIC_SUBTASKS:
                        handleEpicSubTasks(exchange);
                        break;
                    case UNKNOWN:
                        writeResponse(exchange, "Некорректный метод!", 400);
                }
            } catch (TaskGetterException | TaskRemoveException e) {
                writeResponse(exchange, e.getDetailMessage(), 400);
            }
        }

        private Endpoint getEndpoint(final URI requestPath, final String requestMethod) {
            final String[] pathParts = requestPath.toString().split("/");

            if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
                return Endpoint.GET_PRIORITIZED;
            }
            if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                if (pathParts[2].equals("task") || pathParts[2].equals("epic") || pathParts[2].equals("subtask")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_TASKS;
                        case "POST":
                            return Endpoint.POST_TASK;
                        case "DELETE":
                            return Endpoint.DELETE_TASKS;
                        default:
                            return Endpoint.UNKNOWN;
                    }
                }
                if (pathParts[2].equals("history")) {
                    return Endpoint.GET_HISTORY;
                }
            }
            if (pathParts.length == 4 && pathParts[1].equals("tasks") &&
                    (pathParts[2].equals("task") ||
                            pathParts[2].equals("epic") ||
                            pathParts[2].equals("subtask"))) {
                switch (requestMethod) {
                    case "GET":
                        return Endpoint.GET_TASK;
                    case "DELETE":
                        return Endpoint.DELETE_TASK;
                    default:
                        return Endpoint.UNKNOWN;
                }
            }

            if (pathParts.length == 5 && pathParts[1].equals("tasks") &&
                    pathParts[2].equals("subtask") && requestMethod.equals("GET"))
                return Endpoint.GET_EPIC_SUBTASKS;

            return Endpoint.UNKNOWN;
        }

        private void handleEpicSubTasks(final HttpExchange exchange) throws IOException, TaskGetterException {
            String id;
            id = getId(exchange);
            if (id.isBlank()) {
                writeResponse(exchange, WRONG_COMMAND, 400);
                return;
            }
            Epic epic = (Epic) tasksManager.getEpic(id);
            List<Task> list = tasksManager.getAllSubtaskFromEpic(epic);
            writeResponse(exchange, gson.toJson(list), 200);
        }

        private void handleHistory(final HttpExchange exchange) throws IOException {
            List<Task> list = tasksManager.getHistory();
            writeResponse(exchange, gson.toJson(list), 200);
        }

        private void handleGetPrioritizedList(final HttpExchange exchange) throws IOException {
            List<Task> list = tasksManager.getPrioritizedTasks();
            writeResponse(exchange, gson.toJson(list), 200);
        }

        private void handlePost(final HttpExchange exchange) throws IOException {
            String taskType = getTaskType(exchange);

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            if (body.isBlank()) return;

            try {
                Task task;
                switch (taskType) {
                    case "task":
                        task = gson.fromJson(body, Task.class);
                        break;
                    case "epic":
                        task = gson.fromJson(body, Epic.class);
                        break;
                    case "subtask":
                        task = gson.fromJson(body, SubTask.class);
                        break;
                    default:
                        writeResponse(exchange, WRONG_COMMAND, 400);
                        return;
                }
                if (task.getTaskID().isBlank()) {
                    tasksManager.add(task);
                    writeResponse(exchange, taskType + " добавлен(а) c id=" + task.getTaskID(), 201);
                } else {
                    tasksManager.updateTask(task);
                    writeResponse(exchange, taskType + " обнавлен(а)", 201);
                }

            } catch (TaskException e) {
                writeResponse(exchange, e.getDetailMessage(), 400);
            } catch (Exception e) {
                writeResponse(exchange, "Получен некорректный JSON", 400);
            }
        }

        private void handleDeleteTask(final HttpExchange exchange) throws TaskGetterException, TaskRemoveException, IOException {
            String taskType = getTaskType(exchange);
            String id = getId(exchange);
            if (id.isBlank()) {
                writeResponse(exchange, WRONG_COMMAND, 400);
                return;
            }
            String response;
            switch (taskType) {
                case "task":
                    tasksManager.removeTask(id);
                    response = String.format("Задача id=%s удалена", id);
                    break;
                case "epic":
                    tasksManager.removeEpic(id);
                    response = String.format("Эпик id=%s удален", id);
                    break;
                case "subtask":
                    tasksManager.removeSubtask(id);
                    response = String.format("Подзадача id=%s удалена", id);
                    break;
                default:
                    writeResponse(exchange, WRONG_COMMAND, 400);
                    return;
            }
            writeResponse(exchange, response, 200);
        }

        private void handleDeleteTaskList(final HttpExchange exchange) throws TaskGetterException, IOException {
            String taskType = getTaskType(exchange);
            String response;
            switch (taskType) {
                case "task":
                    tasksManager.removeAllTasks();
                    response = "Задачи удалены";
                    break;
                case "epic":
                    tasksManager.removeAllEpics();
                    response = "Эпики удалены";
                    break;
                case "subtask":
                    tasksManager.removeAllSubtasks();
                    response = "Подзадачи удалены";
                    break;
                default:
                    writeResponse(exchange, WRONG_COMMAND, 400);
                    return;
            }
            writeResponse(exchange, response, 200);
        }

        private void handleGetTask(final HttpExchange exchange) throws TaskGetterException, IOException {
            String taskType = getTaskType(exchange);
            String id = getId(exchange);
            if (id.isBlank()) {
                writeResponse(exchange, WRONG_COMMAND, 400);
                return;
            }
            Task task = null;
            switch (taskType) {
                case "task":
                    task = tasksManager.getTask(id);
                    break;
                case "epic":
                    task = tasksManager.getEpic(id);
                    break;
                case "subtask":
                    task = tasksManager.getSubtask(id);
                    break;
                default:
                    writeResponse(exchange, WRONG_COMMAND, 400);
            }
            if (task != null)
                writeResponse(exchange, gson.toJson(task), 200);

        }

        private void handleGetTaskList(final HttpExchange exchange) throws IOException {
            String taskType = getTaskType(exchange);
            List<Task> tasks = null;
            switch (taskType) {
                case "task":
                    tasks = tasksManager.getAllTasks();
                    break;
                case "epic":
                    tasks = tasksManager.getAllEpics();
                    break;
                case "subtask":
                    tasks = tasksManager.getAllSubTasks();
                    break;
                default:
                    writeResponse(exchange, WRONG_COMMAND, 400);
            }
            if (tasks != null) {
                writeResponse(exchange, gson.toJson(tasks), 200);
            }
        }

        private String getTaskType(final HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            final String[] paths = path.split("/");
            return paths[2];
        }

        private String getId(final HttpExchange exchange) {
            final String rawQuery = exchange.getRequestURI().getRawQuery();
            if (rawQuery == null || !rawQuery.contains("id=")) {
                return "";
            }
            final String[] query = rawQuery.split("=");
            return query[1];
        }

        private void writeResponse(final HttpExchange exchange,
                                   final String responseString,
                                   final int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

    }

}
