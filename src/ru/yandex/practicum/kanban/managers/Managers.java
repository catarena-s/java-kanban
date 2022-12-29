package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.utils.FileHelper;

public class Managers {
    private final TaskManager taskManager;
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public Managers(final int configKey, final String... args) throws TaskException {
        switch (configKey) {
            case 1:
                taskManager = new InMemoryTaskManager();
                break;
            case 2:
                taskManager = new FileBackedTasksManager(args.length == 0 ? FileHelper.DATA_FILE_NAME : args[0]);
                break;
            case 3:
                taskManager = new HttpTaskManager("http://localhost:8078/");
                break;
            default:
                throw new TaskException("Менеджер не был создан. Передан не корректный configKey.");
        }
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}