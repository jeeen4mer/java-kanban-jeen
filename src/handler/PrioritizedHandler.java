package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendSuccess(exchange, gson.toJson(prioritizedTasks));
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        sendBadRequest(exchange, "Метод POST недоступен для приоритетных задач");
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        sendBadRequest(exchange, "Метод DELETE недоступен для приоритетных задач");
    }
}