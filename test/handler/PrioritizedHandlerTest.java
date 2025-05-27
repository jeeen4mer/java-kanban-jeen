package handler;

import org.junit.jupiter.api.Test;
import model.Task;
import server.TestBase;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest extends TestBase {
    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description");
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.now().plusHours(1));

        Task task2 = new Task("Task2", "Description");
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.now());

        taskManager.addTaskToList(task1);
        taskManager.addTaskToList(task2);

        HttpResponse<String> response = sendRequest("/prioritized", "GET", "");
        assertEquals(200, response.statusCode());

        // Проверяем порядок задач (task2 должен быть первым, так как его startTime раньше)
        int index1 = response.body().indexOf("Task1");
        int index2 = response.body().indexOf("Task2");
        assertTrue(index2 < index1);
    }

    @Test
    void testEmptyPrioritizedList() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/prioritized", "GET", "");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void testPostMethodNotAllowed() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/prioritized", "POST", "{}");
        assertEquals(400, response.statusCode());
    }

    @Test
    void testDeleteMethodNotAllowed() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/prioritized", "DELETE", "");
        assertEquals(400, response.statusCode());
    }
}