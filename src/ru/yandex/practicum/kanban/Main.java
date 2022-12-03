package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestManager;
import ru.yandex.practicum.kanban.test.Tester;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.UserMenu;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int answer;

        UserMenu.printChooseTaskManager();
        do {
            Helper.printMessage("Что тестируем?: ");
            answer = UserMenu.getUserAnswer(scanner);
            if (answer<0 || answer>2) Helper.printMessage("Некорректная команда. ");
        }while (answer<0 || answer>2);


        TaskManager taskManager;
        switch (answer){
            case 1: taskManager = Managers.getDefault(); break;
            case 2: taskManager = Managers.loadFromFile(Path.of(FileHelper.DATA_FILE_NAME));break;
            default: return;
        }

        Tester test = TestManager.get(taskManager);
        if (test == null) return;

        do {
            UserMenu.printMainMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0)
                test.runTest(answer);

        } while (answer > 0);

    }

}
