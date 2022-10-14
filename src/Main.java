import ru.yandex.practicum.kanban.TaskManager;
import ru.yandex.practicum.kanban.tasks.Epic;
import ru.yandex.practicum.kanban.tasks.SubTask;
import ru.yandex.practicum.kanban.tasks.Task;
import ru.yandex.practicum.kanban.util.TaskStatus;
import ru.yandex.practicum.kanban.util.TaskType;

import java.util.List;

public class Main {
    private static final String MSG_SEPARATOR = "---------------------------------------------------------------------";
    private static final String MSG_SEPARATOR_TASK_LIST = "---Печать %s---\n";
    private static final String MSG_UPDATE_TASK_BY_ID = ">>Обновляем задачу ID = %d - %s\n";
    private static final String MSG_GET_TASK_BY_ID = ">>Получаем %s ID = %d\n";
    private static final String MSG_DELETE_BY_ID = ">>Удаляем задачу ID = ";

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        initTaskManager(taskManager);

        System.out.printf(MSG_SEPARATOR_TASK_LIST, "всего списка");
        printAllTaskManagerList(taskManager);

        System.out.println(MSG_SEPARATOR);
        testGetAllTasks(taskManager);

        System.out.println(MSG_SEPARATOR);
        testGetTasksByID(taskManager);

        System.out.println(MSG_SEPARATOR);
        testUpdateTasks(taskManager);

        System.out.println(MSG_SEPARATOR);
        testRemoveTasks(taskManager);
    }

    private static void testGetTasksByID(TaskManager taskManager) {
        int currentID;
        List<Task> allTasks;

        currentID = 1;
        printTaskByID(currentID, taskManager);
        currentID = 3;
        printTaskByID(currentID, taskManager);
        currentID = 5;
        printTaskByID(currentID, taskManager);
        currentID = 15;
        printTaskByID(currentID, taskManager);

        currentID = 3;
        System.out.printf(MSG_GET_TASK_BY_ID, "подзадачи эпика", currentID);
        Epic epic = (Epic) taskManager.getTaskById(currentID);
        allTasks = taskManager.getAllSubtaskByEpic(epic);
        if (!allTasks.isEmpty()) {
            for (Task t : allTasks) {
                System.out.println(t.toString());
            }
        }
    }

    private static void testGetAllTasks(TaskManager taskManager) {
        List<Task> allTasks;

        System.out.printf(MSG_SEPARATOR_TASK_LIST, TaskType.TASK);

        allTasks = taskManager.getAllTasks(TaskType.TASK);
        if (!allTasks.isEmpty()) {
            for (Task t : allTasks) {
                System.out.println(t.toString());
            }
        }

        System.out.println(MSG_SEPARATOR);
        System.out.printf(MSG_SEPARATOR_TASK_LIST, TaskType.EPIC);
        allTasks = taskManager.getAllTasks(TaskType.EPIC);
        if (!allTasks.isEmpty()) {
            for (Task t : allTasks) {
                System.out.println(t.toString());
            }
        }
        System.out.println(MSG_SEPARATOR);
        System.out.printf(MSG_SEPARATOR_TASK_LIST, TaskType.SUB_TASK);
        allTasks = taskManager.getAllTasks(TaskType.SUB_TASK);
        if (!allTasks.isEmpty()) {
            for (Task t : allTasks) {
                System.out.println(t.toString());
            }
        }
    }

    private static void testUpdateTasks(TaskManager taskManager) {
        int currentID = 2;
        Task task = taskManager.getTaskById(2);
        System.out.printf(MSG_UPDATE_TASK_BY_ID, currentID, task);
        task.setName("newName" + currentID);
        task.setDescription("new Description " + currentID);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task, TaskType.TASK);
        System.out.println(" -> " + taskManager.getTaskById(currentID));
        System.out.println();

        currentID = 3;
        Epic updatedEpic = (Epic) taskManager.getTaskById(currentID);
        System.out.printf(MSG_UPDATE_TASK_BY_ID, currentID, updatedEpic);
        updatedEpic.setName("newEpic" + currentID);
        updatedEpic.setDescription("new Description " + currentID);
        updatedEpic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedEpic, TaskType.EPIC);
        System.out.println(" -> " + taskManager.getTaskById(currentID));
        System.out.println();

        currentID = 5;
        SubTask subTask = (SubTask) taskManager.getTaskById(currentID);
        System.out.printf(MSG_UPDATE_TASK_BY_ID, currentID, subTask);
        subTask.setName("new subTaskName" + currentID);
        subTask.setDescription("new subTask Description " + currentID);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask, TaskType.SUB_TASK);
        subTask = (SubTask) taskManager.getTaskById(currentID);
        System.out.println(" -> " + subTask);
        Epic epic = (Epic) taskManager.getTaskById(subTask.getEpicID());
        System.out.println(" -> " + epic);
        System.out.println();
    }

    private static void testRemoveTasks(TaskManager taskManager) {
        int currentID;
        currentID = 3;
        System.out.println(MSG_DELETE_BY_ID + currentID);
        taskManager.removeTaskByID(currentID);
        printAllTaskManagerList(taskManager);
        System.out.println();

        currentID = 8;
        System.out.println(MSG_DELETE_BY_ID + currentID);
        taskManager.removeTaskByID(currentID, TaskType.SUB_TASK);
        printAllTaskManagerList(taskManager);
        System.out.println();

        System.out.println(">>Удаляем все" + TaskType.EPIC);
        taskManager.removeAllTasks(TaskType.EPIC);
        printAllTaskManagerList(taskManager);
        System.out.println();

        System.out.println(">>Удаляем все задачи");
        taskManager.removeAllTasks();
        System.out.println("Все задачи удалены!");
    }

    private static void printTaskByID(int currentID, TaskManager taskManager) {
        System.out.printf(MSG_GET_TASK_BY_ID, "задачу", currentID);
        Task task = taskManager.getTaskById(currentID);
        if (task == null) {
            System.out.println("Задачи с ID = " + currentID + " не существует");
        } else {
            System.out.println(task);
        }
        System.out.println();
    }


    private static void printAllTaskManagerList(TaskManager taskManager) {
        List<Task> allTasks = taskManager.getAllTasks();
        for (Task t : allTasks) {
            System.out.println(t.toString());
        }
    }

    private static void initTaskManager(TaskManager taskManager) {
        int lastID = taskManager.getLastID();
        Task task = new Task(++lastID, "Задача " + lastID, "Описание задачи " + lastID);
        taskManager.addTask(task, TaskType.TASK);
        task = new Task(++lastID, "Задача " + lastID, "Описание задачи " + lastID);
        taskManager.addTask(task, TaskType.TASK);

        Epic epic = new Epic(++lastID, "Эпик " + lastID, "Описание эпика " + lastID);
        taskManager.addTask(epic, TaskType.EPIC);

        SubTask subTask = new SubTask(++lastID, "Подзадача " + lastID, "Описание подзадачи " + lastID, epic);
        epic.addSubtask(lastID, subTask);
        taskManager.addTask(subTask, TaskType.SUB_TASK);

        subTask = new SubTask(++lastID, "Подзадача " + lastID, "Описание подзадачи " + lastID, epic);
        epic.addSubtask(lastID, subTask);
        taskManager.addTask(subTask, TaskType.SUB_TASK);

        epic = new Epic(++lastID, "Эпик " + lastID, "Описание эпика " + lastID);
        taskManager.addTask(epic, TaskType.EPIC);

        subTask = new SubTask(++lastID, "Подзадача " + lastID, "Описание подзадачи " + lastID, epic);
        epic.addSubtask(lastID, subTask);
        taskManager.addTask(subTask, TaskType.SUB_TASK);

        System.out.println(">>Заполнили TaskManager тестовыми данными");
    }
}
