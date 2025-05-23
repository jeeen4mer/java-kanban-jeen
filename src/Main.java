import manager.Managers;
import manager.TaskManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager tasksManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Сделать сценарий действий в Main");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Задача 2", "Сварить кофейку");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(45));

        tasksManager.addTaskToList(task1);
        tasksManager.addTaskToList(task2);

        Epic driverLicenseTask = new Epic("Эпик 1", "Сдать на права");
        tasksManager.addEpicToList(driverLicenseTask);

        SubTask driverLicenseSubtask1 = new SubTask("Подзадача 1", "Поступить в автошколу");
        driverLicenseSubtask1.setRelationEpicId(driverLicenseTask.getId());
        driverLicenseSubtask1.setStartTime(LocalDateTime.now().minusDays(1));
        driverLicenseSubtask1.setDuration(Duration.ofMinutes(60));

        SubTask driverLicenseSubtask2 = new SubTask("Подзадача 2", "Подготовиться к экзамену");
        driverLicenseSubtask2.setRelationEpicId(driverLicenseTask.getId());
        driverLicenseSubtask2.setStartTime(LocalDateTime.now().plusHours(2));
        driverLicenseSubtask2.setDuration(Duration.ofMinutes(90));

        SubTask driverLicenseSubtask3 = new SubTask("Подзадача 3", "Сдать экзамен");
        driverLicenseSubtask3.setRelationEpicId(driverLicenseTask.getId());
        driverLicenseSubtask3.setStartTime(LocalDateTime.now().plusHours(3));
        driverLicenseSubtask3.setDuration(Duration.ofMinutes(120));

        tasksManager.addSubTaskToList(driverLicenseSubtask1);
        tasksManager.addSubTaskToList(driverLicenseSubtask2);
        tasksManager.addSubTaskToList(driverLicenseSubtask3);

        Epic circusTrick = new Epic("Эпик 2", "Научиться жонглировать");
        tasksManager.addEpicToList(circusTrick);

        System.out.println();
        System.out.println("Список всех задач");
        System.out.println("---------------------------------------------------");
        System.out.println(tasksManager.getAllTasksList());

        System.out.println();
        System.out.println("Информация о времени Эпика 'Сдать на права'");
        System.out.println("---------------------------------------------------");
        System.out.println("Время начала: " + driverLicenseTask.getStartTime());
        System.out.println("Продолжительность: " + driverLicenseTask.getDuration().toMinutes() + " мин.");
        System.out.println("Время окончания: " + driverLicenseTask.getEndTime());

        System.out.println();
        System.out.println("Список задач по приоритету");
        System.out.println("---------------------------------------------------");
        List<Task> prioritizedTasks = tasksManager.getPrioritizedTasks();
        for (Task t : prioritizedTasks) {
            System.out.printf("%s | %s%n", t.getName(), t.getStartTime());
        }

        System.out.println();
        System.out.println("Проверка пересечения времён");
        System.out.println("---------------------------------------------------");

        SubTask conflictingSubtask = new SubTask("Конфликтная подзадача", "Пересекается по времени");
        conflictingSubtask.setStartTime(LocalDateTime.now().plusMinutes(15));
        conflictingSubtask.setDuration(Duration.ofMinutes(30));

        if (tasksManager.isOverlapping(conflictingSubtask)) {
            System.out.println("Ошибка: Задача пересекается с другими!");
        } else {
            System.out.println("Новых пересечений нет.");
            tasksManager.addTaskToList(conflictingSubtask);
        }

        System.out.println();
        System.out.println("Проверка работы истории просмотров");
        System.out.println("---------------------------------------------------");
        System.out.println("Скрытый вызов задачи 1");
        tasksManager.getTaskById(1);
        System.out.println("Проверка записи в истории:");
        System.out.println(tasksManager.getHistory());
        System.out.println("***************************");
        System.out.println("Скрытый вызов задачи 2");
        tasksManager.getTaskById(2);
        System.out.println("Проверка записи в истории:");
        System.out.println(tasksManager.getHistory());
        System.out.println("***************************");
        System.out.println("Скрытый вызов задачи 3");
        tasksManager.getTaskById(3);
        System.out.println("Проверка записи в истории:");
        System.out.println(tasksManager.getHistory());
        System.out.println("***************************");

        System.out.println();
        System.out.println("Удаление записи с id1 из истории");
        System.out.println("---------------------------------------------------");
        tasksManager.removeTaskFromHistoryList(1);
        System.out.println(tasksManager.getHistory());

        System.out.println();
        System.out.println("Удаление эпика удалит его и его подзадачи из истории");
        System.out.println("---------------------------------------------------");
        System.out.println("Скрытый вызов подзадачи 4");
        tasksManager.getTaskById(4);
        tasksManager.getTaskById(5);
        tasksManager.getTaskById(6);

        System.out.println("История до удаления эпика");
        System.out.println(tasksManager.getHistory());

        tasksManager.deleteById(3);

        System.out.println("История после удаления эпика");
        System.out.println(tasksManager.getHistory());
    }
}