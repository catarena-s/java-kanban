package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.managers.InMemoryTaskManager;
import ru.yandex.practicum.kanban.managers.TaskManager;

/***
 * Изначально, всего лишь хотелось упростить себе тестирования, считывая тестовые данные из файла.
 * И лень было каждый раз перезапускать код.
 * Но что-то пошло ни так, и Остапа понесло ....
 */
public class TestManager {
    private TestManager(){}

    public static TestBackend get(TaskManager manager)
    {
        if (manager instanceof FileBackedTasksManager) {
            return new TestBackend(manager);
        } else if (manager instanceof InMemoryTaskManager) {
            return new TestInMemory( manager);
        }
        return null;
    }


}
