package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.tests.commands.*;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public enum TestCommand {
    ADD(new TestAddCommand(), 1),
    CLONE(new TestCloneCommand(), 7),
    UPDATE(new TestUpdateCommand(), 2),
    REMOVE(new TestRemoveCommand(), 3),
    MIX(new TestMixCommand(), 4),
    PRINT(null, 6),
    GET(new TestGetCommand(), 5);


    private final TestRunner test;
    private final int value;

    TestCommand(TestRunner test, int value) {
        this.test = test;
        this.value = value;
    }

    public static TestCommand getCommand(int menuNumber) {
        switch (menuNumber) {
            case 1:
                return ADD;
            case 2:
                return UPDATE;
            case 3:
                return REMOVE;
            case 4:
                return MIX;
            case 5:
                return GET;
            case 6:
                return PRINT;
            default:
                Helper.printMessage(Colors.CYAN, "Такая команда отсутствует. ");
                return null;
        }
    }

    public int getValue() {
        return this.value;
    }

    public TestRunner getTest() {
        return test;
    }
}
