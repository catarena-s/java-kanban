package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.exceptions.TaskException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.tests.TestCommand;
import ru.yandex.practicum.kanban.tests.TestManager;
import ru.yandex.practicum.kanban.tests.Tester;
import ru.yandex.practicum.kanban.utils.Converter;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.TaskPrinter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path fileName;

    private FileBackedTasksManager(final HistoryManager historyManager) {
        super(historyManager);
    }

    public static FileBackedTasksManager loadFromFile(final Path file) {
        final FileBackedTasksManager manager = new FileBackedTasksManager(Managers.getDefaultHistory());
        manager.load(file);
        return manager;
    }

    public static void main(final String[] args) {
        final Path file = Paths.get(FileHelper.DATA_FILE_NAME);
        final FileBackedTasksManager f1 = FileBackedTasksManager.loadFromFile(file);
        Helper.printMessage("Тестируем 1-й FileBackedTasksManager:");
        final Tester test = TestManager.get(f1);
        if (test == null) return;
        test.runTest(TestCommand.MIX.getValue());
        Helper.printMessage("");
        Helper.printMessage("Печать содержимого менеджера 1 ");
        TaskPrinter.printAllTaskManagerListLong(f1);
        TaskPrinter.printHistory(f1);

        Helper.printSeparator();
        Helper.printMessage("Тестируем 2-й FileBackedTasksManager:");
        FileBackedTasksManager f2 = FileBackedTasksManager.loadFromFile(file);
        Helper.printMessage("Печать содержимого менеджера 2");
        TaskPrinter.printAllTaskManagerListLong(f2);
        TaskPrinter.printHistory(f2);/**/
    }

    /**
     * загружаем данные из файла таск-менеджер
     */
    private void load(final Path file) {
        try {
            fileName = file;
            final List<String> lines = FileHelper.readFromFile(file);

            if (lines.isEmpty() || lines.size() <= 1) return;
            final String head = lines.get(0).trim().toLowerCase().replace(" ", "");
            if (!Helper.DATA_HEAD.equals(head)) return;
            int index = 1;
            if (lines.get(index).isBlank()) index++;

            while (index < lines.size() && !lines.get(index).isBlank()) {
                loadData(lines.get(index++));
            }
            loadHistory(lines, index);
        } catch (FileNotFoundException e) {
            Helper.printMessage("Ошибка загрузки данных: файл '" + file.toAbsolutePath() + "' не найден.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файл.");
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getDetailMessage());
        } catch (TaskException e) {
            Helper.printMessage(e.getDetailMessage());
        }
    }

    /**
     * Сохраняем таск-менеджер в файл
     */
    private void save() {
        try {
            final StringBuilder builder = new StringBuilder();
            if (!tasksByType.isEmpty()) {
                builder.append(Helper.DATA_HEAD).append(System.lineSeparator())
                        .append(Converter.taskListToString(getAllByType(TaskType.TASK)))
                        .append(Converter.taskListToString(getAllByType(TaskType.EPIC)))
                        .append(Converter.taskListToString(getAllByType(TaskType.SUB_TASK)))
                        .append(System.lineSeparator())
                        .append(Converter.historyToString(historyManager));
            }
            FileHelper.saveToFile(fileName, builder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.");
        }
    }

    /**
     * Загружаем историю в HistotyManager
     */
    private void loadHistory(final List<String> lines, final int index) throws TaskGetterException {
        for (int i = index; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.isBlank()) continue;
            initHistoryManager(line, historyManager);
        }
    }

    /**
     * загружает задачу из  в таск-менеджер
     */
    private void loadData(final String line) throws TaskException {
        final String[] data = line.split(",");
        final Task task = Converter.fromString(String.join(",", data));
        addTaskToTaskManager(task);
        if (TaskType.SUB_TASK.equals(task.getType())) {
            subtaskToEpic((SubTask) task);
        }
    }

    /**
     * Инициализируем historyManager
     */
    private void initHistoryManager(final String line, final HistoryManager historyManager) throws TaskGetterException {
        final List<String> listId = Converter.historyFromString(line);
        for (String id : listId) {
            historyManager.add(getById(id));
        }
    }

    @Override
    public void add(Task task) throws TaskException {
        super.add(task);
        save();
    }

    @Override
    public Task clone(Task task) throws TaskException {
        Task newTask = super.clone(task);
        save();
        return newTask;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> list = super.getPrioritizedTasks();
        save();
        return list;
    }

    @Override
    public List<Task> getAllSubtaskFromEpic(Epic epic) {
        List<Task> list = super.getAllSubtaskFromEpic(epic);
        save();
        return list;
    }

    @Override
    public List<Task> getAll() {
        List<Task> list = super.getAll();
        save();
        return list;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = super.getAllTasks();
        save();
        return list;
    }

    @Override
    public List<Task> getAllEpics() {
        List<Task> list = super.getAllEpics();
        save();
        return list;
    }

    @Override
    public List<Task> getAllSubTasks() {
        List<Task> list = super.getAllSubTasks();
        save();
        return list;
    }

    @Override
    public Task getById(String taskID) throws TaskGetterException {
        Task task = super.getById(taskID);
        save();
        return task;
    }

    @Override
    public Task getTask(String taskID) throws TaskGetterException {
        Task task = super.getTask(taskID);
        save();
        return task;
    }

    @Override
    public Task getEpic(String taskID) throws TaskGetterException {
        Task task = super.getEpic(taskID);
        if (task == null) return null;
        save();
        return task;
    }

    @Override
    public Task getSubtask(String taskID) throws TaskGetterException {
        Task task = super.getSubtask(taskID);
        if (task == null) return null;
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) throws TaskException {
        super.updateTask(task);
        save();
    }

    @Override
    public void clear() {
        super.clear();
        save();
    }

    @Override
    public void removeAllEpics() throws TaskGetterException {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllTasks() throws TaskGetterException {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() throws TaskGetterException {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTask(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeTask(taskID);
        save();
    }

    @Override
    public void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeEpic(taskID);
        save();
    }

    @Override
    public void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeSubtask(taskID);
        save();
    }
}
