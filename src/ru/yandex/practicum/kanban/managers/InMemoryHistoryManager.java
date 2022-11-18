package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;
    private final CustomLinkedList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new CustomLinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
    public void remove(String id) {
        history.removeTask(id);
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    /*
    Практика так практика — возражений нет. В общем-то, у меня были подозрения, что как-то слишком просто вышло.
    Постаралась учесть все замечания.
    Реализацию CustomLinkedList, пока убрала внутрь InMemoryHistoryManager. Нужно ли от него вообще избавиться?
    В ТЗ: "Отдельный класс для списка создавать не нужно — реализуйте его прямо в классе InMemoryHistoryManager"
    - немного сбивает с толку, потому что изначально говорилось о необходимости своей реализации CustomLinkedList.
     */
    private class CustomLinkedList<T extends Task> {
        private final Map<String, Node<T>> historyMap;
        private Node<T> first;
        private Node<T> last;

        public CustomLinkedList() {
            historyMap = new HashMap<>();
            first = null;
            last = null;
        }

        public int size() {
            return historyMap.size();
        }

        public List<T> getTasks() {
            List<T> list = new ArrayList<>();
            Node<T> current = first;
            while (current != null) {
                list.add(current.task);
                current = current.next;
            }
            return list;
        }

        public void removeTask(String id) {
            if (!historyMap.containsKey(id)) return;
            Node<T> node = historyMap.get(id);
            historyMap.remove(id);
            removeNode(node);
        }

        public void removeFirst() {
            if (first == null) return;
            historyMap.remove(first.getID());
            removeNode(first);
        }

        public void addLast(T task) {
            historyMap.put(task.getTaskID(), linkLast(task));
        }

        public void clear() {
            historyMap.clear();
            first = null;
            last = null;
        }

        private Node<T> linkLast(Task task) {
            String id = task.getTaskID();
            if (historyMap.containsKey(id)) {
                removeNode(historyMap.get(id));
                historyMap.remove(id);
            }

            Node<T> newNode = new Node(task, last, null);
            if (first == null && last == null) {
                first = newNode;
            } else {
                Node<T> tmp = last;
                tmp.next = newNode;
            }
            last = newNode;

            return newNode;
        }

        private void removeNode(Node<T> node) {
            if (node == first && node == last) {
                first = null;
                last = null;
                return;
            }
            Node<T> prev = node.prev;
            Node<T> next = node.next;
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

        private class Node<E extends Task> {
            E task;
            Node<E> prev;
            Node<E> next;

            public Node(E task, Node<E> prev, Node<E> next) {
                this.task = task;
                this.prev = prev;
                this.next = next;
            }

            public String getID() {
                return task.getTaskID();
            }
        }
    }
}
