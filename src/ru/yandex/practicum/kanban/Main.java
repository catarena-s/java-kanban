package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
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
    public static void main(String[] args) throws IOException, TaskException {
        Scanner scanner = new Scanner(System.in);
        int answer;

        UserMenu.printChooseTaskManager();
        do {
            Helper.printMessage("Что тестируем?: ");
            answer = UserMenu.getUserAnswer(scanner);
            if (answer < 0 || answer > 3)
                Helper.printMessage(Colors.CYAN_BOLD_ITALIC, "Некорректная команда.");
        } while (answer < 0 || answer > 3);
        if (answer == 0) return;
        if (answer == 3) {
            new KVServer().start();
        }

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
    }

}
