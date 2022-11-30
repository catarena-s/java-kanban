package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.operations.*;
import ru.yandex.practicum.kanban.utils.Helper;

public class TestBackend {
    protected TaskManager taskManager;

    public TestBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void runTest(int answer, boolean printToConsole) {
        TestOperation operation = TestOperation.getOperation(answer);
        if (operation == null) return;
        operation.test.runTest(taskManager, printToConsole);
    }

    public void runTest(int answer) {
        runTest(answer, false);
    }

    enum TestOperation {
        ADD(new AdditionalTest(), 1),
        UPDATE(new UpdateTest(), 2),
        REMOVE(new RemoveTest(), 3),
        MIX(new MixOperationTest(), 4),
        GET(new GetTest(), 5);

        private final Test test;
        private final int value;

        TestOperation(Test test, int value) {
            this.test = test;
            this.value = value;
        }

        public static TestOperation getOperation(int menuNumber) {
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
                default:
                    Helper.printMessage("Такая команда отсутствует. \n");
                    return null;
            }
        }


        public int getValue() {
            return this.value;
        }
    }

}
