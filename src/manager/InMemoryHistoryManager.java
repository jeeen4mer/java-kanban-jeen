package manager;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        historyList.remove(task);
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
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