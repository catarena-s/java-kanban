package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.managers.FileBackedTasksManager;
import ru.yandex.practicum.kanban.model.SimpleTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.tests.TestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.tests.TestHelper.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public static final String LIST_IS_EMPTY = "Список не пустой";


    @BeforeEach
    void setUp(TestInfo info) throws Exception {
        if (info.getTags().contains("NotInit")) {
            return;
        } else if (info.getTags().contains("EmptyFile")) {
            // загружаем пустой файл, если нужно протестироавть на пустом таск-менеджере
            init(2, getPathString(DATA_FILE_NAME_EMPTY));
        } else if (info.getTags().contains("InitData")) {
            init(2, getPathString(DATA_FILE_NAME_EMPTY));
            // инициализация конкретными данными под цели тестирования
            TestHelper.addDataFromFile(taskManager, INIT_TEST_DATA);
        } else
            init(2);
    }
//    @BeforeEach
//    @Override
//    void beforeEachTest(TestInfo info) {
//        if (!info.getTags().contains("NotInit"))
//        Helper.printMessage("%s :", taskManager.getClass().getSimpleName());
//        super.beforeEachTest(info);
//    }

    @AfterEach
    void tearDown(TestInfo info) {
        if (info.getTags().contains("EmptyFile") || info.getTags().contains("InitData")) {
            taskManager.clear();
        }
    }

    @Test
//    @Tag(value = "EmptyFile")
//    @Tag(value = "NotInit")
    @DisplayName("Загружаем пустой список задач.")
    void loadEmptyFile() {
        init(2, getPathString(DATA_FILE_NAME_EMPTY));
        assertEquals(0, taskManager.getAllTasks().size(), LIST_IS_EMPTY);
    }

//    @Test
//    @Tag(value = "NotInit")
//    @DisplayName("ЗагруЭпик без подзадач.")
//    void loadEmptyEpic() throws TaskGetterException, IOException, TaskAddException {
//        init(2, getPathString(DATA_FILE_NAME_EMPTY_EPIC));
//        Epic epic = (Epic) taskManager.getEpic("0007");
//        assertEquals(0, epic.getSubTasks().size(), LIST_IS_EMPTY);
//    }

    @Test
    @Tag(value = "NotInit")
    @DisplayName("Загружаем файл без истории.")
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
    @Tag(value = "EmptyFile")
    @DisplayName("Сохранение - в пустой файл файла.")
    void saveToEmptyFile() throws Exception {
        Task task = new SimpleTask("Task1", "TAsk 1 description");
        Task task2 = new SimpleTask("Task2", "TAsk 2 description");
        taskManager.add(task);
        taskManager.add(task2);

        FileBackedTasksManager fbTasksManager2 = FileBackedTasksManager
                .loadFromFile(getPath(DATA_FILE_NAME_EMPTY));
        int saved = fbTasksManager2.getAllTasks().size();

        assertEquals(2, saved);
    }

    @Test
    @DisplayName("Сохранение")
    void save() throws Exception {

        int before = taskManager.getAll().size();

        Task task = new SimpleTask("Task1", "TAsk 1 description");
        Task task2 = new SimpleTask("Task2", "TAsk 2 description");
        taskManager.add(task);
        taskManager.add(task2);

        FileBackedTasksManager fbTasksManager2 = FileBackedTasksManager
                .loadFromFile(taskManager.getFileName());

        int saved = fbTasksManager2.getAll().size();

        assertEquals(before + 2, saved, "Количество задач в файле должно быть"+(before+2));

        taskManager.removeTask(task.getTaskID());
        taskManager.removeTask(task2.getTaskID());
    }
}