package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;

        historyList.remove(task);

        if (historyList.size() >= MAX_SIZE) {
            historyList.remove(0);
        }

        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void remove(int id) {
        historyList.removeIf(task -> task.getId() == id);
    }

    @Override
    public void clearHistoryList() {
        historyList.clear();
    }
}