package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        List<Task> history = taskManager.getHistory();
        sendSuccess(exchange, gson.toJson(history));
    }

    @Override
    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        sendBadRequest(exchange, "Метод POST недоступен для истории просмотров");
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        sendBadRequest(exchange, "Метод DELETE недоступен для истории просмотров");
    }
}