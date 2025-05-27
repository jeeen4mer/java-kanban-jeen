package server;

import handler.*;
import manager.Managers;
import manager.TaskManager;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту: " + PORT);
    }

    public void stop(int delay) {
        server.stop(delay);
        try {
            Thread.sleep(100); // Добавляем задержку 100 миллисекунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("HTTP-сервер остановлен");
    }


    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}