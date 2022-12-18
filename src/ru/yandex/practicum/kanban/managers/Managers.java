package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.utils.FileHelper;

import java.nio.file.Path;

public class Managers {
    private final TaskManager taskManager;
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public Managers(final int config, final String... args) {
        switch (config) {
            case 1:
                taskManager = new InMemoryTaskManager(historyManager);
                break;
            case 2:
                taskManager = FileBackedTasksManager.loadFromFile(Path.of(args.length == 0 ? FileHelper.DATA_FILE_NAME : args[0]));
                break;
            default:
                taskManager = null;
        }
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}