package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.Managers;
import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.test.TestManager;
import ru.yandex.practicum.kanban.test.Tester;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.UserMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Managers<TaskManager> managers = null;
        Scanner scanner = new Scanner(System.in);
        int answer;

        Helper.printMessage("1 - InMemoryTaskManager\n");
        Helper.printMessage("2 - FileBackedTasksManager \n");
        Helper.printMessage("0 - выход\n");
        do {
            Helper.printMessage("Что тестируем?: ");
            answer = UserMenu.getUserAnswer(scanner);
            if (answer<0 || answer>2) Helper.printMessage("Некорректная команда. ");
        }while (answer<0 || answer>2);

        final boolean isFileManager = (answer == 2);

        switch (answer){
            case 2: managers = new Managers<>(new FileBackedTasksManager()); break;
            case 1: managers = new Managers<>(new InMemoryTaskManager());break;
            default: return;
        }
        TaskManager taskManager = managers.getDefault();
        Tester test = TestManager.get(taskManager);
        if (test == null) return;

        do {
            UserMenu.printMenu();
            answer = UserMenu.getUserAnswer(scanner);
            if (answer > 0)
                test.runTest(answer);

        } while (answer > 0);
/*
Задание ТЗ: Создайте новый FileBackedTasksManager менеджер из этого же файла.
Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом,
есть в новом менеджере.
 */
        if(isFileManager){
            FileBackedTasksManager tasksManager2 = new FileBackedTasksManager();
            Tester test2 = TestManager.get(tasksManager2);

            test2.runTest(TestCommand.PRINT.getValue());
        }
    }
}