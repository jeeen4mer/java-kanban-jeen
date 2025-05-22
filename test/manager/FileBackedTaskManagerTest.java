package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv").toFile();
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasksList().isEmpty(), "Файл должен быть пуст");
    }

    @Test
    void testSaveAndLoadSingleTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasksList();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void testSaveAndLoadSingleEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicToList(epic);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasksList();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(epic, tasks.get(0));
    }

    @Test
    void testSaveAndLoadSubTaskWithEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicToList(epic);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1");
        subTask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subTask);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasksList();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(epic));
        assertTrue(tasks.contains(subTask));

        Epic loadedEpic = (Epic) loadedManager.getTaskById(epic.getId());
        assertTrue(loadedEpic.getSubTaskIds().contains(subTask.getId()));
    }

    @Test
    void testUpdateTask_SavesToFile() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskById(task.getId());

        assertNotNull(loadedTask);
        assertEquals(TaskStatus.DONE, loadedTask.getTaskStatus());
    }

    @Test
    void testDeleteTask_SavesToFile() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        taskManager.deleteById(task.getId());

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNull(loadedManager.getTaskById(task.getId()), "Задача должна быть удалена");
    }

    @Test
    void testDeleteAllTasks_ClearsFile() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);
        taskManager.deleteAllTasks();

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasksList().isEmpty(), "Файл должен быть очищен");
    }

    @Test
    void testLoadFromFile_WithInvalidData_ShouldNotFail() {
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("id,type,name,status,description,epic\ninvalid,data,here,NEW,some,123");
        } catch (IOException e) {
            fail("Не удалось записать тестовые данные в файл: " + e.getMessage());
        }

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasksList();

        assertTrue(tasks.isEmpty(), "Менеджер не должен загружать некорректные данные");
    }

    @Test
    void testHistoryPreservedAfterSaveAndLoad() throws IOException {
        File tempFile = Files.createTempFile("tasks", ".csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTaskToList(task);

        manager.getTaskById(task.getId());

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> history = loadedManager.getHistory();
        assertNotNull(history);
        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }
}