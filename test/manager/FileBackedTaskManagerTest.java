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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager taskManager;

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
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasksList();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task loadedTask = tasks.get(0);
        assertNotNull(loadedTask.getStartTime());
        assertNotNull(loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }

    @Test
    void testSaveAndLoadSingleEpic() throws IOException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicToList(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");
        subTask1.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subTask1);

        taskManager.save();

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = (Epic) loadedManager.getTaskById(epic.getId());

        assertNotNull(loadedEpic, "Эпик должен существовать после загрузки");
        assertEquals(epic.getName(), loadedEpic.getName(), "Имя эпика должно совпадать");
        assertEquals(epic.getDescription(), loadedEpic.getDescription(), "Описание эпика должно совпадать");
    }

    @Test
    void testSaveAndLoadSubTaskWithEpic() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicToList(epic);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1");
        subTask.setRelationEpicId(epic.getId());
        taskManager.addSubTaskToList(subTask);

        // Убедимся, что данные сохранились
        taskManager.save();

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = (Epic) loadedManager.getTaskById(epic.getId());
        SubTask loadedSubTask = (SubTask) loadedManager.getTaskById(subTask.getId());

        assertNotNull(loadedEpic, "Эпик должен существовать после загрузки");
        assertNotNull(loadedSubTask, "Подзадача должна существовать после загрузки");
        assertEquals(epic.getId(), loadedSubTask.getRelationEpicId());
        assertTrue(loadedEpic.getSubTaskIds().contains(subTask.getId()));
    }


    @Test
    void testUpdateTask_SavesToFile() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTaskToList(task);

        TaskStatus originalStatus = task.getTaskStatus();
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        // Отладочный код
        try {
            List<String> fileContent = Files.readAllLines(tempFile.toPath());
            System.out.println("File content:");
            fileContent.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskById(task.getId());

        assertNotNull(loadedTask);
        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getTaskStatus());
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
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task);
        taskManager.getTaskById(task.getId());

        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> history = loadedManager.getHistory();

        assertNotNull(history);
        assertFalse(history.isEmpty());
        assertEquals(task, history.get(0));
    }

    @Test
    void testGetPrioritizedTasks_ReturnsSortedByStartTime() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStartTime(LocalDateTime.now().plusHours(2));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(45));

        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertNotNull(prioritized);
        assertEquals(2, prioritized.size());
        assertEquals(task2.getStartTime(), prioritized.get(0).getStartTime());
        assertEquals(task1.getStartTime(), prioritized.get(1).getStartTime());
    }

    @Test
    void testIsOverlapping_DetectsConflict() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(30));

        assertTrue(taskManager.isOverlapping(task2), "Задачи должны пересекаться");
    }

    @Test
    void testIsOverlapping_NoOverlap() {
        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task1);

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(30));
        taskManager.addTaskToList(task2);

        assertFalse(taskManager.isOverlapping(task2), "Задачи НЕ должны пересекаться");
    }

    @Test
    void testGetPrioritizedTasks_IgnoresNullStartTime() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Описание 1");
        task1.setStartTime(now);
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Задача 2", "Описание 2");
        task2.setDuration(Duration.ofMinutes(30));

        taskManager.addTaskToList(task2);
        taskManager.addTaskToList(task1);

        // Отладочный код
        System.out.println("Все задачи:");
        taskManager.getAllTasksList().forEach(t ->
                System.out.println("- " + t.getName() + " (startTime: " + t.getStartTime() + ")")
        );

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        System.out.println("\nПриоритизированные задачи:");
        prioritized.forEach(t ->
                System.out.println("- " + t.getName() + " (startTime: " + t.getStartTime() + ")")
        );

        assertNotNull(prioritized);
        assertEquals(2, prioritized.size());
        assertEquals(task1, prioritized.get(0));
    }



    @Test
    void testEpicCalculatesTimeCorrectly() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpicToList(epic);

        LocalDateTime startTime = LocalDateTime.now().withNano(0); // Убираем наносекунды для более точного сравнения

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");
        subTask1.setRelationEpicId(epic.getId());
        subTask1.setStartTime(startTime);
        subTask1.setDuration(Duration.ofMinutes(60));

        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2");
        subTask2.setRelationEpicId(epic.getId());
        subTask2.setStartTime(startTime.plusMinutes(70));
        subTask2.setDuration(Duration.ofMinutes(30));

        taskManager.addSubTaskToList(subTask1);
        taskManager.addSubTaskToList(subTask2);

        Epic updatedEpic = (Epic) taskManager.getTaskById(epic.getId());

        assertEquals(startTime, updatedEpic.getStartTime());
        assertEquals(startTime.plusMinutes(100), updatedEpic.getEndTime());
        assertEquals(Duration.ofMinutes(90), updatedEpic.getDuration());
    }

}