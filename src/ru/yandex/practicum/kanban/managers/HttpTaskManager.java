package ru.yandex.practicum.kanban.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.KVTaskClient;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.utils.GsonAdapter;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private Gson gson;
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(String file) {
        super(file);
    }

    @Override
    protected void load() {
        gson = GsonAdapter.getGsonWithAdapter();
        try {
            kvTaskClient = new KVTaskClient(new URI(pathName));
        } catch (URISyntaxException e) {
            Helper.printMessage(e.getMessage());
            return;
        }
        try {
            loadTasksFromServer(TaskType.TASK.toString());
            loadTasksFromServer(TaskType.EPIC.toString());
            loadTasksFromServer(TaskType.SUB_TASK.toString());
            loadHistoryFromServer();
        } catch (TaskException e) {
            Helper.printMessage(e.getDetailMessage());
        }
    }

    private void loadHistoryFromServer() {
        final List<Task> taskList = getTaskList("history");
        if (taskList.isEmpty()) return;
        for (Task task : taskList)
            historyManager.add(task);
    }

    private void loadTasksFromServer(final String key) throws TaskException {
        final List<Task> taskList = getTaskList(key);
        if (taskList.isEmpty()) return;
        for (Task task : taskList)
            addTaskToTaskManager(task);
    }

    private List<Task> getTaskList(final String key) {
        final String res = kvTaskClient.load(key);
        if (res == null || res.isBlank() || res.equals("[]")) return Collections.emptyList();
        return gson.fromJson(res, new TypeToken<List<Task>>() {}.getType());
    }

    @Override
    protected void save() {
        try {
            saveTasksToServer(TaskType.TASK);
            saveTasksToServer(TaskType.EPIC);
            saveTasksToServer(TaskType.SUB_TASK);
            saveToServer("history", getHistory());
        } catch (TaskException e) {
            Helper.printMessage(e.getDetailMessage());
        } catch (IOException | InterruptedException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private void saveTasksToServer(final TaskType type) throws IOException, InterruptedException, TaskException {
        final List<Task> list = getAllByType(type);
        saveToServer(type.toString(),list);
    }

    private void saveToServer(String key, List<Task> taskList) throws TaskException, IOException, InterruptedException {
        kvTaskClient.put(key, gson.toJson(taskList));
    }

}
