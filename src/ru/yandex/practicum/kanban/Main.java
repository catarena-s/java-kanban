package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestManager;
import ru.yandex.practicum.kanban.test.Tester;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.UserMenu;

import java.time.Clock;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int answer;

        UserMenu.printChooseTaskManager();
        do {
            Helper.printMessage("Что тестируем?: ");
            answer = UserMenu.getUserAnswer(scanner);
            if (answer < 0 || answer > 2)
                Helper.printMessage(Helper.getColoredString("Некорректная команда. \n",Colors.CYAN));
        } while (answer < 0 || answer > 2);
        if (answer == 0) return;

        Managers managers = new Managers(1);
        TaskManager taskManager = managers.getDefault();
        Tester test = TestManager.get(taskManager);

        if (test == null) {
            Helper.printMessageColored(Colors.CYAN,"Ошибка получения TestManager.\n");
            return;
        }

        do {
            UserMenu.printMainMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0)
                try {
                    test.runTest(answer);
                } catch (ManagerSaveException e) {
                    Helper.printMessage(e.getMessage());
                }

        } while (answer > 0);

    }

}
