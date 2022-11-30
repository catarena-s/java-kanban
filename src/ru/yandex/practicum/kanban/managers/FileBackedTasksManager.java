package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.exceptions.TaskGetterException;
import ru.yandex.practicum.kanban.model.*;
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
        Path file = Paths.get(Helper.DATA_FILE_NAME);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Helper.DATA_FILE_NAME))) {
            String head = bufferedReader.readLine();
            if (Helper.DATA_HEAD.equals(head)) {
                String line;
                int maxId = 0;
                while (bufferedReader.ready()) {
                    line = bufferedReader.readLine();
                    if (line.isBlank()) break;

                    String[] data = line.split(",");

                    int current = Integer.parseInt(data[0].trim());
                    if (maxId < Integer.parseInt(data[0])) maxId = current;

                    loadData(data);
                }
                setLastID(maxId);
                loadHistory(bufferedReader);
            }

        } catch (FileNotFoundException e) {
            Helper.printMessage("Ошибка загрузки данных: файл " + file.getFileName() + " не найден.\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения в файл.");
        }
    }

    private void loadHistory(BufferedReader bufferedReader) throws IOException {
        String line;
        while (bufferedReader.ready()) {
            line = bufferedReader.readLine();
            if (line.isBlank()) continue;

            String[] data = line.split(" ");
            initHistory(data);
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

    private void initHistory(String[] data) {
        HistoryManager historyManager = getHistoryManager();
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

        try (FileWriter fw = new FileWriter(Helper.DATA_FILE_NAME)) {
            fw.write(Helper.DATA_HEAD + "\n");
            writeTasksToFile(fw, getAllTasks());
            writeTasksToFile(fw, getAllEpics());
            writeTasksToFile(fw, getAllSubTasks());

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
        Collections.sort(tasks, (t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTaskID(), t2.getTaskID()));
        for (Task t : tasks) {
            fw.write(t.toCompactString());
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void addEpic(Epic task) {
        super.addEpic(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void addSubtask(SubTask task) throws TaskGetterException {
        super.addSubtask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void clone(Task task) throws TaskGetterException {
        super.clone(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
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
    public Task getById(String taskID) {
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
    public void removeTask(String taskID) throws TaskGetterException {
        super.removeTask(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeEpic(String taskID) throws TaskGetterException {
        super.removeEpic(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }

    @Override
    public void removeSubtask(String taskID) throws TaskGetterException {
        super.removeSubtask(taskID);
        try {
            save();
        } catch (ManagerSaveException e) {
            Helper.printMessage(e.getMessage());
        }
    }
}
