package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestBackend;
import ru.yandex.practicum.kanban.test.TestManager;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Managers<TaskManager> managers = new Managers<>(new InMemoryTaskManager());
        TaskManager taskManager = managers.getDefault();
        TestBackend t = TestManager.get(taskManager);

        int answer;
        Scanner scanner = new Scanner(System.in);
        do {
            UserMenu.printMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0)
                t.runTest(answer);

        } while (answer > 0);
    }
}
