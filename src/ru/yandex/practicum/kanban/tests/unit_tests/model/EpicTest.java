package ru.yandex.practicum.kanban.tests.unit_tests.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.TestHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.kanban.utils.Helper.formatter;

class EpicTest extends TaskTest<Epic> {


    @BeforeEach
    void setUp(TestInfo info) {
        task = new Epic();
        if (info.getTags().contains("Init")) {
            task.builder()
                    .taskId("0001")
                    .name("Epic1")
                    .description("Description");
            SubTask subTask = new SubTask();
            subTask.builder().taskId("0002")
                    .epic(task.getTaskID())
                    .status(TaskStatus.IN_PROGRESS)
                    .duration(10)
                    .startTime("11-02-2022 02:02");
            task.addSubtask(subTask);
        }
    }

    @ParameterizedTest(name = "Инициализация задачи id=''{0}'' name=''{1}'' description=''{2}''")
    @CsvSource(value = {
            "0001, Epic name, Epic desription",
            "'','' ,'' ",
            "'','' , Epicdescription",
            "0002,'' ,'' "})
    @DisplayName("Инициализация")
    void init(String id, String name, String description) {
        super.init(id, name, description);
    }


    @Test
    @Tag(value = "Init")
    @DisplayName("Получить тип задачи")
    void testGetType() {
        super.getType(TaskType.EPIC);
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить время окончания")
    void testGetEndTime() {
        super.getEndTime("11-02-2022 02:12");
        // добавляем эпику ещё одну подзадачу
        SubTask subTask = new SubTask();
        subTask.builder().taskId("0003")
                .epic(task.getTaskID())
                .status(TaskStatus.IN_PROGRESS)
                .duration(20)
                .startTime("12-02-2022 02:02");
        task.addSubtask(subTask);

        super.getEndTime("12-02-2022 02:22");
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить duration")
    void testGetDuration() {
        Helper.printMessage(task.toActualStringFoTest());
        SubTask subTask = new SubTask();
        subTask.builder().taskId("0003")
                .epic(task.getTaskID())
                .status(TaskStatus.IN_PROGRESS)
                .duration(20)
                .startTime("12-02-2022 02:02");
        task.addSubtask(subTask);

        Helper.printMessage(task.toActualStringFoTest());
        assertEquals(30, task.getDuration());

        Helper.printMessage("%n>> Удаляем подзадачу id=%s%n", subTask.getTaskID());
        task.getSubTasks().remove(subTask);
        task.refreshEpic();
        Helper.printMessage(TestHelper.AFTER_TEST_MSG);
        TaskPrinter.printEpicInfo(task);
        assertEquals(10, task.getDuration());
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить время начала")
    void testGetStartTime() {
        Helper.printMessage("Before:");
        TaskPrinter.printEpicInfo(task);

        SubTask subTask = new SubTask();
        subTask.builder().taskId("0003")
                .epic(task.getTaskID())
                .status(TaskStatus.IN_PROGRESS)
                .duration(20)
                .startTime("09-02-2022 02:02");
        task.addSubtask(subTask);
        Helper.printMessage("%n>> Добавляем подзадачу id=%s%n", subTask.getTaskID());
        Helper.printMessage("After:");
        TaskPrinter.printEpicInfo(task);
        assertEquals("09-02-2022 02:02", task.getStartTime().format(formatter));

        Helper.printMessage("%n>> Удаляем подзадачу id=%s%n", subTask.getTaskID());
        task.getSubTasks().remove(subTask);
        task.refreshEpic();
        Helper.printMessage("After:");
        TaskPrinter.printEpicInfo(task);
        assertEquals("11-02-2022 02:02", task.getStartTime().format(formatter));
        Helper.printDotsSeparator();
    }

    @Test
    @Tag(value = "Init")
    @DisplayName("Получить статус")
    void testGetStatus() {
        super.getStatus(TaskStatus.IN_PROGRESS);
    }
}