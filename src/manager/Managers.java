package manager;

import java.io.File;

public class Managers {
    private static final String FILE_PATH = "tasks.csv";

    public static TaskManager getDefault() {
        File file = new File(FILE_PATH);
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}