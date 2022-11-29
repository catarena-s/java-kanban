package ru.yandex.practicum.kanban.test;

public interface TestEdit extends Test{
    void initTaskManager();
    void testUpdateTasks();
    void testRemoveTasks();

    void testMixOperation();
}
