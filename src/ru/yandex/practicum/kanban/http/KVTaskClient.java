package ru.yandex.practicum.kanban.http;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URI uri;
    private HttpRequest request;
    private String apiToken;

    public KVTaskClient(URI url) {
        uri = url;
        client = HttpClient.newHttpClient();
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri.toString() + "register/"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                Helper.printMessage("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            Helper.printMessage("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        } catch (IllegalArgumentException ex) {
            Helper.printMessage("Введённый вами адрес не соответствует формату URL. " +
                    "Попробуйте, пожалуйста, снова.");
        }
    }


    public void put(String key, String json) throws IOException, InterruptedException, TaskException {
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(uri.toString() + "save/" + key + "?API_TOKEN=" + apiToken))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new TaskException(String.format("Ошибка сохранения данных: %s  code=%d", response.body(), response.statusCode()));
        }
    }

    public String load(String key) {
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri.toString() + "load/" + key + "?API_TOKEN=" + apiToken))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException ex) {
            Helper.printMessage("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException ex) {
            Helper.printMessage("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
        return "";
    }
}
