package ru.yandex.practicum.kanban.utils;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.solid.operations.AdditionalTest;
import ru.yandex.practicum.kanban.test.solid.operations.RemoveTest;
import ru.yandex.practicum.kanban.test.Test;
import ru.yandex.practicum.kanban.test.solid.Test1;
import ru.yandex.practicum.kanban.test.solid.operations.UpdateTest;

import java.util.Scanner;

public class UserMenu {
    private UserMenu() {
    }

    public static final String NUMBER_FORMAT_EXCEPTION = "Введено некорректное значение.";

    public static void printMenu() {
        Helper.printMessage("--- Веберите что протестировать: ----------------------------\n"
                + "1 - Добавление задач\n"
                + "2 - Обновление задач.\n"
                + "3 - Удаление задач.\n"
                + "4 - Сешанное тестирование(удаление, добавление, обновление в разной последовательности) \n"
                + "5 - Получение задач \n"
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
                Helper.printMessage(NUMBER_FORMAT_EXCEPTION);
                isCorrectAnswer = false;
            }
        } while (!isCorrectAnswer);
        return userInput;
    }

    public static void run(int menuNumber, TaskManager manager) {
        Menu menu = Menu.getMenu(menuNumber);
        if(menu == null) return;
        menu.test.runTest(manager);
   /*     switch (menuNumber){
            case 1 : {
                Menu.Add.test.runTest(true);
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
                tester.testMixOperations();
                break;
            }
            case 5: {
                tester.testGetOperations();
                break;
            }
            case 0: return;
            default: Helper.printMessage("Некорректная команда.");
        }*/
    }

    static Test tester;

    public static void setTester(Test currentTester) {
        tester = (Test) currentTester;
    }

    enum Menu {
        Add(1, new AdditionalTest()), Update(2, new UpdateTest()), Remuve(3,new RemoveTest());

        private Test1 test;
        private int number;

        Menu(int i, Test1 test) {
            this.test = test;
            this.number = i;
        }

        public static Menu getMenu(int menuNumber) {
            switch (menuNumber) {
                case 1:
                    return Add;
                case 2:
                    return Update;
                case 3:
                    return Remuve;
                default:
                    return null;
            }
        }

        private Test1 get() {
            return this.test;
        }
    }

}


