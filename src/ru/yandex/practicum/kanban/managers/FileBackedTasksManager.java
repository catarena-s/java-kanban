package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.*;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.utils.FileHelper;
import ru.yandex.practicum.kanban.utils.Helper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    public FileBackedTasksManager() {
        try {
            loadFromFile();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    private void loadFromFile() throws ManagerSaveException {
        Path file = Paths.get(FileHelper.DATA_FILE_NAME);
        try {
            List<String> lines = FileHelper.readFromFile(file);
            if (lines.isEmpty() || lines.size() == 1) return;
            String head = lines.get(0);
            if (!Helper.DATA_HEAD.equals(head)) return;
            lines.remove(0);
            int index = 0;
            int maxId = 0;
            for (String line : lines) {
                if (line.isBlank()) break;

                String[] data = line.split(",");

                int current = Integer.parseInt(data[0].trim());
                if (maxId < Integer.parseInt(data[0])) maxId = current;

                loadData(data);
                index++;
            }
            setLastID(maxId);
            loadHistory(lines, index);

        } catch (FileNotFoundException e) {
            Helper.printMessage("Ошибка загрузки данных: файл '" + file.toAbsolutePath() + "' не найден.\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файл.");
        } catch (TaskGetterException e) {
            Helper.printMessage(e.getDetailMessage());
        }
    }

    private void loadHistory(List<String> lines, int index) throws TaskGetterException {
        for (int i = index; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;

            String[] data = line.split(" ");
            initHistory(data, getHistoryManager());
        }
    }

    private void loadData(String[] data) {
        switch (data[1].trim()) {
            case "TASK": {
                Task task = new Task(data[3].trim(), data[4].trim());
                init(data, task);
                addToMemory(TaskType.TASK, task);
                break;
            }
            case "EPIC": {
                Epic epic = new Epic(data[3].trim(), data[4].trim());
                init(data, epic);
                addToMemory(TaskType.EPIC, epic);
                break;
            }
            case "SUB_TASK": {
                SubTask subTask = new SubTask(data[3].trim(), data[4].trim(), data[5].trim());
                init(data, subTask);
                addToMemory(TaskType.SUB_TASK, subTask);
                try {
                    Epic epic = (Epic) getEpic(subTask.getEpicID());
                    epic.addSubtask(subTask);
                } catch (TaskGetterException e) {
                    Helper.printMessage(e.getMessage());
                }
                break;
            }
            default:
                Helper.printMessage("некорректная запись: %s%n", Arrays.toString(data));
        }
    }

    private void init(String[] data, Task task) {
        task.setTaskID(data[0].trim());
        task.setStatus(TaskStatus.valueOf(data[2].trim()));
    }

    private void initHistory(String[] data, HistoryManager historyManager) throws TaskGetterException {
        for (String id : data) {
            historyManager.add(getById(id));
        }
    }

    private void addToMemory(TaskType taskType, Task task) {
        Map<String, Task> tasks = getTasksByType().getOrDefault(taskType, new HashMap<>());
        tasks.put(task.getTaskID(), task);
        getTasksByType().put(taskType, tasks);
    }

    private void save() throws ManagerSaveException {

        try (FileWriter fw = new FileWriter(FileHelper.DATA_FILE_NAME)) {
            fw.write(Helper.DATA_HEAD + "\n");
            writeTasksToFile(fw, getAllByType(TaskType.TASK));
            writeTasksToFile(fw, getAllByType(TaskType.EPIC));
            writeTasksToFile(fw, getAllByType(TaskType.SUB_TASK));

            fw.write("\n");

            writeHistoryToFile(fw);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.");
        }
    }

    private void writeHistoryToFile(FileWriter fw) throws IOException {
        int index = 0;
        for (Task t : getHistory()) {
            fw.write(t.getTaskID() + " ");
            index++;
            if (index % 15 == 0) fw.write("\n");
        }
    }

    private void writeTasksToFile(FileWriter fw, List<Task> tasks) throws IOException {
        tasks.sort((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTaskID(), t2.getTaskID()));
        for (Task t : tasks) {
            fw.write(t.toCompactString());
        }
    }

    @Override
    public void addTask(Task task) throws TaskAddException {
        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic task) throws TaskAddException {
        super.addEpic(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void addSubtask(SubTask task) throws TaskGetterException, TaskAddException {
        super.addSubtask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public Task clone(Task task) throws TaskGetterException, TaskAddException {
        Task newTask = super.clone(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return newTask;
    }

    @Override
    public List<SubTask> getAllSubtaskByEpic(Epic epic) {
        List<SubTask> list = super.getAllSubtaskByEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Task> getAll() {
        List<Task> list = super.getAll();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = super.getAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Task> getAllEpics() {
        List<Task> list = super.getAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return list;
    }

    @Override
    public List<Task> getAllSubTasks() {
        List<Task> list = super.getAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return list;
    }

    @Override
    public Task getById(String taskID) throws TaskGetterException {
        Task task = super.getById(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return task;
    }

    @Override
    public Task getTask(String taskID) throws TaskGetterException {
        Task task = super.getTask(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return task;
    }

    @Override
    public Task getEpic(String taskID) throws TaskGetterException {
        Task task = super.getEpic(taskID);
        if (task == null) return null;
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return task;
    }

    @Override
    public Task getSubtask(String taskID) throws TaskGetterException {
        Task task = super.getSubtask(taskID);
        if (task == null) return null;
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
        return task;
    }

    @Override
    public void updateTask(Task task) throws TaskGetterException {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void clear() {
        super.clear();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeAllEpics() throws TaskGetterException {
        super.removeAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeAllTasks() throws TaskGetterException {
        super.removeAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeAllSubtasks() throws TaskGetterException {
        super.removeAllSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeTask(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeTask(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeEpic(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeEpic(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeSubtask(String taskID) throws TaskGetterException, TaskRemoveException {
        super.removeSubtask(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }
}
