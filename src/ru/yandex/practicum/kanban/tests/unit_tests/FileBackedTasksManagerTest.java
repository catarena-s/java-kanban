package ru.yandex.practicum.kanban.tests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.utils.TestHelper;
import ru.yandex.practicum.kanban.utils.FileHelper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.tests.utils.TestHelper.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public static final String LIST_IS_EMPTY = "Список не пустой";

    @BeforeEach
    void setUp(TestInfo info) throws TaskGetterException, IOException, TaskAddException {
        if (info.getTags().contains("NotInit")) {
            return;
        } else if (info.getTags().contains("EmptyFile")) {
            init(2, getPathString(DATA_FILE_NAME_EMPTY));
        } else if (info.getTags().contains("InitData")) {
            init(2, getPathString(DATA_FILE_NAME_EMPTY));
            TestHelper.initFromFile(taskManager, INIT_TEST_DATA);
        } else
            init(2, FileHelper.DATA_FILE_NAME);
    }

    @AfterEach
    void tearDown(TestInfo info) {
        if (info.getTags().contains("EmptyFile") || info.getTags().contains("InitData")) {
            taskManager.clear();
        }
    }

    /**
     * a. Пустой список задач.
     * b. Эпик без подзадач.
     * c. Пустой список истории.
     */
    @Test
    @Tag(value = "EmptyFile")
    @DisplayName("Пустой список задач.")
    void loadEmptyFile() {
        assertEquals(0, taskManager.getAllTasks().size(), LIST_IS_EMPTY);
    }

    @Test
    @Tag(value = "NotInit")
    @DisplayName("Эпик без подзадач.")
    void loadEmptyEpic() throws TaskGetterException, IOException, TaskAddException {
        init(2, getPathString(DATA_FILE_NAME_EMPTY_EPIC));
        Epic epic = (Epic) taskManager.getEpic("0007");
        assertEquals(0, epic.getSubTasks().size(), LIST_IS_EMPTY);
    }

    @Test
    @Tag(value = "NotInit")
    void loadEmptyWithEmptyHistory() {
        init(2, getPathString(DATA_FILE_NAME_EMPTY_HISTORY));
        assertEquals(0, taskManager.getHistory().size(), LIST_IS_EMPTY);
    }

    /**
     * a. Пустой список задач.
     * b. Эпик без подзадач.
     * c. Пустой список истории.
     */
    @Test
    @Tag(value = "NotInit")
    void save() throws TaskGetterException, TaskAddException {
        FileBackedTasksManager fbTasksManager1 = FileBackedTasksManager
                .loadFromFile(getPath(DATA_FILE_NAME_EMPTY));
        Task task = new SimpleTask();
        fbTasksManager1.add(task);
        fbTasksManager1.clear();
        FileBackedTasksManager fbTasksManager2 = FileBackedTasksManager
                .loadFromFile(getPath(DATA_FILE_NAME_EMPTY));
        int saved = fbTasksManager2.getAllTasks().size();
        assertEquals(0, saved);
    }
}