import manager.Managers;
import manager.TaskManager;
import model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager tasksManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Сделать сценарий действий в Main");
        Task task2 = new Task("Задача 2", "Сварить кофейку");

        tasksManager.addTaskToList(task1);
        tasksManager.addTaskToList(task2);

        Epic driverLicenseTask = new Epic("Эпик 1", "Сдать на права");
        SubTask driverLicenseSubtask1 = new SubTask("Подзадача 1", "Поступить в автошколу");
        SubTask driverLicenseSubtask2 = new SubTask("Подзадача 2", "Подготовиться к экзамену");
        SubTask driverLicenseSubtask3 = new SubTask("Подзадача 3", "Сдать экзамен");

        driverLicenseTask.addSubTaskIdToEpic(driverLicenseSubtask1);
        driverLicenseTask.addSubTaskIdToEpic(driverLicenseSubtask2);
        driverLicenseTask.addSubTaskIdToEpic(driverLicenseSubtask3);
        tasksManager.addSubTaskToList(driverLicenseSubtask1);
        tasksManager.addSubTaskToList(driverLicenseSubtask2);
        tasksManager.addSubTaskToList(driverLicenseSubtask3);

        Epic circusTrick = new Epic("Эпик 2", "Научиться жонглировать");

        tasksManager.addEpicToList(driverLicenseTask);
        tasksManager.addEpicToList(circusTrick);

        System.out.println();
        System.out.println("Список всех задач задач");
        System.out.println("---------------------------------------------------");
        System.out.println(tasksManager.getAllTasksList());

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
        System.out.println("Повторный вызов задачи 1");
        tasksManager.getTaskById(1);
        System.out.println("Проверка записи в истории:");
        System.out.println(tasksManager.getHistory());
        System.out.println("***************************");
        System.out.println("Повторный вызов задачи 2");
        tasksManager.getTaskById(2);
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
        System.out.println("Скрытый вызов 3 подзадач Эпика");
        tasksManager.getTaskById(4);
        tasksManager.getTaskById(5);
        tasksManager.getTaskById(6);
        System.out.println("История до удаления эпика");
        System.out.println(tasksManager.getHistory());
        System.out.println("История после удаления эпика");
        tasksManager.deleteById(3);
        System.out.println(tasksManager.getHistory());
    }
}
