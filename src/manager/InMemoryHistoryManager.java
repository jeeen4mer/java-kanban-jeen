package manager;

import model.Task;
import model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;

    private Map<Integer, Node> nodesMap = new HashMap<>();

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void clearHistoryList() {
        nodesMap.clear();
        head = null;
        tail = null;
    }

    @Override
    public void remove(int id) {
        if (nodesMap.containsKey(id)) {
            Node<Task> nodeToDelete = nodesMap.get(id);
            Node<Task> prevNode = nodeToDelete.getPrev();
            Node<Task> nextNode = nodeToDelete.getNext();

            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
                nextNode.setPrevNull();
            }

            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
                prevNode.setNextNull();
            }

            nodesMap.remove(id);
        }
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (nodesMap.size() == 0) {
            head = newNode;
            tail = newNode;
            nodesMap.put(task.getId(), newNode);
        } else {
            if (nodesMap.containsKey(task.getId())) {
                remove(task.getId());
            }
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }

        nodesMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> resultArray = new ArrayList<>();
        if (nodesMap.size() > 0) {
            resultArray.add(head.getTask());
            Node<Task> nextNode = head.getNext();
            while (nextNode != null) {
                resultArray.add(nextNode.getTask());
                nextNode = nextNode.getNext();
            }
        }
        return resultArray;
    }
}
