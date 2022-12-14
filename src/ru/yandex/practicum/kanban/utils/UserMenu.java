package ru.yandex.practicum.kanban.utils;

import java.util.Scanner;

public class UserMenu {
    private UserMenu() {
    }

    public static final String NUMBER_FORMAT_EXCEPTION = "Введено некорректное значение.";

    public static void printMainMenu() {
        Helper.printMessage("--- Веберите что протестировать: ----------------------------\n"
                + "1 - Добавление задач\n"
                + "2 - Обновление задач.\n"
                + "3 - Удаление задач.\n"
                + "4 - Сешанное тестирование(удаление, добавление, обновление в разной последовательности) \n"
                + "5 - Получение задач \n"
                + "6 - Печать всех задач \n"
                + "0 - Завершить тестирование.\n"
                + "---------------------------------------------------");
    }

    public static void printChooseTaskManager() {
        Helper.printMessage("1 - InMemoryTaskManager");
        Helper.printMessage("2 - FileBackedTasksManager");
        Helper.printMessage("0 - выход");
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
                Helper.printMessage(NUMBER_FORMAT_EXCEPTION);
                isCorrectAnswer = false;
            }
        } while (!isCorrectAnswer);
        return userInput;
    }
}


