package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestEdit;

import java.util.Scanner;

public class UserMenu {
    public static final String NUMBER_FORMAT_EXCEPTION = "Введено некорректное значение.";

    public static void printMenu(){
        Helper.printMessage("--- Веберите что протестировать: ----------------------------\n"
                + "1 - Добавление задач\n"
                + "2 - Обновление задач.\n"
                + "3 - Удаление задач.\n"
                + "4 - Сешанное тестирование(удаление, добавление, обновление в разной последовательности) \n"
                + "0 - Завершить тестирование.\n"
                + "---------------------------------------------------\n");
    }

    public static int getUserAnswer(Scanner scanner) {
        int userInput = -1;
        boolean isCorrectAnswer;
        do {
            String answer = scanner.nextLine().trim();
            try {
                userInput = Integer.parseInt(answer);
                isCorrectAnswer = true;
            } catch (NumberFormatException ex) {
                System.out.println(NUMBER_FORMAT_EXCEPTION);
                isCorrectAnswer = false;
            }
        } while (!isCorrectAnswer);
        return userInput;
    }

    public static void run(int menuNumber, TaskManager taskManager){
        switch (menuNumber){
            case 1 : {
                tester.initTaskManager();
                break;
            }
            case 2: {
                tester.testUpdateTasks();
                break;
            }
            case 3: {
                tester.testRemoveTasks();
                break;
            }
            case 4: {
                tester.testMixOperation();
                break;
            }
            case 0: return;
            default: Helper.printMessage("Некорректная команда.");
        }
    }
    static TestEdit tester;
    public static void setTester(TestEdit currentTester) {
        tester = currentTester;
    }
}
