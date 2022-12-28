package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.utils.FileHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Managers {
    private final TaskManager taskManager;
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public Managers(final int config, final String... args)  {
        switch (config) {
            case 1:
                taskManager = new InMemoryTaskManager();
                break;
            case 2:
                taskManager =new FileBackedTasksManager(args.length == 0 ? FileHelper.DATA_FILE_NAME : args[0]);
                        //FileBackedTasksManager.loadFromFile(Path.of(args.length == 0 ? FileHelper.DATA_FILE_NAME : args[0]));
                break;
            case 3:
//                try {
                    taskManager = new HttpTaskManager("http://localhost:8078/");
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
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