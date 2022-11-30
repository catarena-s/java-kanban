package ru.yandex.practicum.kanban.test.solid.operations;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.solid.Test1;

import java.util.function.Function;

public abstract class Tester implements Test1 {
    protected TaskManager taskManager;

    public Tester(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Tester() {
    }
}
