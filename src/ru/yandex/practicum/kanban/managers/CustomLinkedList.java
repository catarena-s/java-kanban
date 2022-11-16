package ru.yandex.practicum.kanban.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.*;

public class CustomLinkedList<T extends Task> extends LinkedList {
    private final Map<String, Node> map = new HashMap<>();

    public List<T> getTasks() {
        return new ArrayList<>(this);
    }

    public boolean removeTask(String id) {
        Node node = map.get(id);
        map.remove(id);
        return super.remove(node.o);
    }

    @Override
    public void addLast(Object o) {
        super.addLast(o);
        Node node = linkLast(o);
        map.put(((Task) o).getTaskID(), node);
    }

    @Override
    public void clear() {
        super.clear();
        map.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomLinkedList<?> that = (CustomLinkedList<?>) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), map);
    }

    private Node linkLast(Object o) {
        Node node = new Node(o);
        String id = node.getID();
        if (map.containsKey(id)) {
            Node lastNode = map.get(id);
            removeNode(lastNode);
        }
        return node;
    }

    private void removeNode(Node node) {
        remove(node.o);
        String id = node.getID();
        map.remove(id);
    }

    private class Node {
        Object o;

        public Node(Object o) {
            this.o = o;
        }

        public String getID() {
            return ((Task) o).getTaskID();
        }
    }
}
