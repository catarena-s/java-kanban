package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.test.commands.*;
import ru.yandex.practicum.kanban.utils.Helper;

public enum TestCommand {
    ADD(new TestAddCommand(), 1),
    CLONE(new TestCloneCommand(), 7),
    UPDATE(new TestUpdateCommand(), 2),
    REMOVE(new TestRemoveCommand(), 3),
    MIX(new TestMixCommand(), 4),
    PRINT(null, 6),
    GET(new TestGetCommand(), 5);


    private final Test test;
    private final int value;

    TestCommand(Test test, int value) {
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
                Helper.printMessage("Такая команда отсутствует. \n");
                return null;
        }
    }

    public int getValue() {
        return this.value;
    }

    public Test getTest() {
        return test;
    }
}
