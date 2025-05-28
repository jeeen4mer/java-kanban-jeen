package handler;

import org.junit.jupiter.api.Test;
import model.Task;
import server.TestBase;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends TestBase {
    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        taskManager.addTaskToList(task);
        taskManager.getTaskById(task.getId()); // Добавляем в историю

        HttpResponse<String> response = sendRequest("/history", "GET", "");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }

    @Test
    void testEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/history", "GET", "");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body().trim());
    }

    @Test
    void testPostMethodNotAllowed() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/history", "POST", "{}");
        assertEquals(400, response.statusCode());
    }

    @Test
    void testDeleteMethodNotAllowed() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("/history", "DELETE", "");
        assertEquals(400, response.statusCode());
    }
}