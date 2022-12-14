package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.exceptions.TaskAddException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.exceptions.TaskRemoveException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.SubTask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.test.TestManager;
import ru.yandex.practicum.kanban.test.Tester;
import ru.yandex.practicum.kanban.utils.Converter;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;
import ru.yandex.practicum.kanban.utils.Printer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path fileName;

    private FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public static FileBackedTasksManager loadFromFile(Path file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(Managers.getDefaultHistory());
        manager.load(file);
        return manager;
    }

    public static void main(String[] args) {

        Path file = Paths.get(FileHelper.DATA_FILE_NAME);
        FileBackedTasksManager f1 = FileBackedTasksManager.loadFromFile(file);
        Helper.printMessage("Тестируем 1-й FileBackedTasksManager:");
        Tester test = TestManager.get(f1);
        if (test == null) return;
        test.runTest(TestCommand.MIX.getValue());
        Helper.printMessage("");
        Helper.printMessage("Печать содержимого менеджера 1 ");
        Printer.printAllTaskManagerList(f1);
        Printer.printHistory(f1);

        Helper.printSeparator();
        Helper.printMessage("Тестируем 2-й FileBackedTasksManager:");
        FileBackedTasksManager f2 = FileBackedTasksManager.loadFromFile(file);
        Helper.printMessage("Печать содержимого менеджера 2");
        Printer.printAllTaskManagerList(f2);
        Printer.printHistory(f2);/**/
    }

    /**
     * загружаем данные из файла таск-менеджер
     */
    private void load(Path file) {
        try {
            fileName = file;
            List<String> lines = FileHelper.readFromFile(file);

            if (lines.isEmpty() || lines.size() <= 1) return;
            String head = lines.get(0).trim().toLowerCase().replace(" ", "");
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
        }
    }

    /**
     * Сохраняем таск-менеджер в файл
     */
    private void save() {
        try {
            String builder = new String("");
            if (!tasksByType.isEmpty()) {
                builder = Helper.DATA_HEAD + System.lineSeparator() +
                        Converter.taskListToString(getAllByType(TaskType.TASK)) +
                        System.lineSeparator() +
                        Converter.taskListToString(getAllByType(TaskType.EPIC)) +
                        System.lineSeparator() +
                        Converter.taskListToString(getAllByType(TaskType.SUB_TASK)) +
                        System.lineSeparator() +
                        System.lineSeparator() +
                        Converter.historyToString(historyManager);
            }

            FileHelper.saveToFile(fileName, builder);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.");
        }
    }

    /**
     * Загружаем историю в HistotyManager
     */
    private void loadHistory(List<String> lines, int index) throws TaskGetterException {
        for (int i = index; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            initHistoryManager(line, historyManager);
        }
    }

    /**
     * загружает задачу из  в таск-менеджер
     */
    private void loadData(String line) throws TaskGetterException {
        String[] data = line.split(",");
        Task task = Converter.fromString(String.join(",", data));
        addTaskToTaskManager(task);
        if (TaskType.SUB_TASK.equals(task.getType())) {
            subtaskToEpic((SubTask) task);
        }
    }

    /**
     * Инициализируем historyManager
     */
    private void initHistoryManager(String line, HistoryManager historyManager) throws TaskGetterException {
        List<String> listId = Converter.historyFromString(line);
        for (String id : listId) {
            historyManager.add(getById(id));
        }
    }

    @Override
    public void add(Task task) throws TaskGetterException, TaskAddException {
        super.add(task);
        save();
    }

    @Override
    public Task clone(Task task) throws TaskGetterException, TaskAddException {
        Task newTask = super.clone(task);
        save();
        return newTask;
    }

    @Override
    public List<Task> getAllSubtaskByEpic(Epic epic) {
        List<Task> list = super.getAllSubtaskByEpic(epic);
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
    public void updateTask(Task task) throws TaskGetterException {
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
