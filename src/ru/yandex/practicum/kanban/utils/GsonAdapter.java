package ru.yandex.practicum.kanban.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GsonAdapter {

    private GsonAdapter() {
    }

    public static Gson getGsonWithAdapter() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                .registerTypeAdapter(Task.class, new TaskDeserializer("taskType"))
                .create();
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            jsonWriter.value(localDate.format(Helper.formatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), Helper.formatter);
        }
    }


    private static class TaskDeserializer implements JsonDeserializer<Task> {
        private final String typeElementName;
        private final Gson gson;
        private final Map<String, Class<? extends Task>> typeRegistry;

        public TaskDeserializer(final String typeElementName) {
            this.typeElementName = typeElementName;
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            this.typeRegistry = new HashMap<>();
            registerBarnType("TASK", Task.class);
            registerBarnType("SUB_TASK", SubTask.class);
            registerBarnType("EPIC", Epic.class);
        }

        public void registerBarnType(final String typeName, final Class<? extends Task> taskType) {
            typeRegistry.put(typeName, taskType);
        }

        public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject taskObject = json.getAsJsonObject();
            JsonElement typeElement = taskObject.get(typeElementName);

            Class<? extends Task> type = typeRegistry.get(typeElement.getAsString());
            return gson.fromJson(taskObject, type);
        }
    }
}
