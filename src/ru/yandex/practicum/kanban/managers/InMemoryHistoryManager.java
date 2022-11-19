package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final Map<String, Node> history;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        first = null;
        last = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.task);
            current = current.next;
        }
        return list;
    }

    @Override
    public void remove(String id) {
        if (!history.containsKey(id)) return;
        Node node = history.get(id);
        history.remove(id);
        removeNode(node);
    }

    @Override
    public void add(Task task) {
        if(history.containsKey(task.getTaskID())) {
            remove(task.getTaskID());
        }
        if (size() == HISTORY_SIZE) {
            removeFirst();
        }
        addLast(task);
    }

    public int size() {
        return history.size();
    }

    public void removeFirst() {
        if (first == null) return;
        history.remove(first.getID());
        removeNode(first);
    }

    public void addLast(Task task) {
        if (history.containsKey(task.getTaskID())) {
            remove(task.getTaskID());
        }
        history.put(task.getTaskID(), linkLast(task));
    }

    @Override
    public void clear() {
        history.clear();
        first = null;
        last = null;
    }

    private Node linkLast(Task task) {
        String id = task.getTaskID();
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }

        Node newNode = new Node(task, last, null);
        if (first == null && last == null) {
            first = newNode;
        } else {
            Node tmp = last;
            tmp.next = newNode;
        }
        last = newNode;

        return newNode;
    }

    private void removeNode(Node node) {
        if (node == first && node == last) {
            first = null;
            last = null;
            return;
        }
        Node prev = node.prev;
        Node next = node.next;
        if (prev == null) {
            first = next;
            first.prev = null;
            return;
        }
        if (next == null) {
            last = prev;
            last.next = null;
            return;
        }

        prev.next = next;
        next.prev = prev;

    }

    private class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public String getID() {
            return task.getTaskID();
        }
    }
}