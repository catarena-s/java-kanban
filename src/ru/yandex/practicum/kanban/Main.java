package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.tests.TestManager;
import ru.yandex.practicum.kanban.tests.Tester;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.UserMenu;

import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int answer = 0;
        KVServer kvServer = null;

        UserMenu.printChooseTaskManager();
        do {
            Helper.printMessage("Что тестируем?: ");
            answer = UserMenu.getUserAnswer(scanner);
            if (answer < 0 || answer > 3)
                Helper.printMessage(Colors.CYAN_BOLD_ITALIC, "Некорректная команда.");
        } while (answer < 0 || answer > 3);
        if (answer == 0) return;
        if (answer == 3) {
            kvServer = new KVServer();
            kvServer.start();
        }
//        Managers managers = new Managers(answer);
//        TaskManager taskManager = managers.getDefault();

//        Helper.printMessage("Input token");
//        long token  = scanner.nextLong();
//        KVTaskClient kvTaskClient = new KVTaskClient(new URI("http://localhost:8078/"));
//        Task task1 = new SimpleTask("na1", "na");
//        Task task2 = new SimpleTask("na2", "na2");
//        taskManager.add(task1);
//        taskManager.add(task2);
//        Gson gson = new Gson();
//
//        kvTaskClient.put("task", gson.toJson(task1));
//        String res = kvTaskClient.load("task");
//        kvTaskClient.put("task", gson.toJson(task2));
//        res = kvTaskClient.load("task");
//
//        KVTaskClient kvTaskClient2 = new KVTaskClient(new URI("http://localhost:8078/"));
//        String res2 = kvTaskClient2.load("task");

        Managers managers = new Managers(answer);
        TaskManager taskManager = managers.getDefault();
        Tester test = TestManager.get(taskManager);

        if (test == null) {
            Helper.printMessage(Colors.CYAN, "Ошибка получения TestManager.");
            return;
        }

        do {
            UserMenu.printMainMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0) {
                try {
                    test.runTest(answer);
                } catch (ManagerSaveException e) {
                    Helper.printMessage(e.getMessage());
                }
            }

        } while (answer > 0);

        if (kvServer != null) kvServer.stop();
    }

}
