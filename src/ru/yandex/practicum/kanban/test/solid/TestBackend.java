package ru.yandex.practicum.kanban.test.solid;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.solid.operations.*;

public class TestBackend {
    protected TaskManager taskManager;

    public TestBackend(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void runTest(int answer) {
        TestOPerationType menu = TestOPerationType.getOperation(answer);
        if (menu == null) return;
        menu.test.runTest(taskManager);
    }


    enum TestOPerationType {
        Add(new AdditionalTest()),
        Update(new UpdateTest()),
        Remuve(new RemoveTest()),
        MIX(new MixOperationTest()),
        GET(new GetTest());

        private Test1 test;

        TestOPerationType(Test1 test) {
            this.test = test;
        }

        public static TestOPerationType getOperation(int menuNumber) {
            switch (menuNumber) {
                case 1:
                    return Add;
                case 2:
                    return Update;
                case 3:
                    return Remuve;
                case 4:
                    return MIX;
                case 5:
                    return GET;
                default:
                    return null;
            }
        }

        private Test1 get() {
            return this.test;
        }
    }

}
