package ru.yandex.practicum.kanban.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.http.KVTaskClient;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(String file) {
        super(file);

    }

    @Override
    protected void load() {
        try {
            kvTaskClient = new KVTaskClient(new URI(pathName));
        } catch (URISyntaxException e) {
            Helper.printMessage(e.getMessage());
            return;
        }
        try {
            loadTasksFromServer(Task.class, TaskType.TASK.toString());
            loadTasksFromServer(Epic.class, TaskType.EPIC.toString());
            loadTasksFromServer(SubTask.class, TaskType.SUB_TASK.toString());
        } catch (TaskException e) {
            Helper.printMessage(e.getDetailMessage());
        }

        final String res = kvTaskClient.load("history");
        if (res != null && !res.isBlank()) {
            JsonElement jsonElement = JsonParser.parseString(res);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int j = 0; j < jsonArray.size(); j++) {
                Task t = gson.fromJson(jsonArray.get(j).getAsJsonObject(), Task.class);
                historyManager.add(t);
            }
        }
    }

    private void loadTasksFromServer(Class classGson, String key) throws TaskException {
        String res = kvTaskClient.load(key);
        if (res == null || res.isBlank()) return;
        JsonElement jsonElement = JsonParser.parseString(res);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (int j = 0; j < jsonArray.size(); j++) {
            Task t = gson.fromJson(jsonArray.get(j).getAsJsonObject(), (Type) classGson);
            addTaskToTaskManager(t);
        }
    }

    @Override
    protected void save() {
        try {
            saveTasksToServer(TaskType.TASK);
            saveTasksToServer(TaskType.EPIC);
            saveTasksToServer(TaskType.SUB_TASK);
            kvTaskClient.put("history", gson.toJson(historyManager.getHistory()));
        } catch (TaskException e) {
            Helper.printMessage(e.getDetailMessage());
        } catch (IOException | InterruptedException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private void saveTasksToServer(TaskType type) throws IOException, InterruptedException, TaskException {
        List<Task> list = getAllByType(type);
        kvTaskClient.put(type.toString(), gson.toJson(list));
    }

}
