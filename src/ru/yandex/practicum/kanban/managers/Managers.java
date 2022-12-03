package ru.yandex.practicum.kanban.managers;

import java.nio.file.Path;

public class Managers{
    private static final TaskManager taskManager;
    private static final HistoryManager historyManager;
    static {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(getDefaultHistory());
    }

    private Managers() {
    }

/* Изначально не внимательно прочитала задание, поэтому этот метод был не по ТЗ  */
    public static FileBackedTasksManager loadFromFile(Path file){
        FileBackedTasksManager manager = new FileBackedTasksManager(getDefaultHistory());
        manager.load(file);
        return manager;
    }
    public static TaskManager getDefault(){
        return taskManager;
    }
    public static HistoryManager getDefaultHistory(){
        return historyManager;
    }
}