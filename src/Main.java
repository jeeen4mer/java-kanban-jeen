public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task buyProducts = new Task("Купить продукты", "Вместе с печеньем");
        Task buyProductsCreated = taskManager.addTask(buyProducts);
        System.out.println(buyProductsCreated);

        Task buyProductsToUpdate = new Task(buyProducts.getId(), "Не забыть купить продукты", "Можно без печенья",
                Status.IN_PROGRESS);
        Task buyProductsUpdated = taskManager.updateTask(buyProductsToUpdate);
        System.out.println(buyProductsUpdated);


        Epic cleanHouse = new Epic("Прибраться в доме", "Выбросить ненужные вещи");
        taskManager.addEpic(cleanHouse);
        System.out.println(cleanHouse);
        Subtask cleanHouseSubtask1 = new Subtask("Протереть пыль", "В том числе за диваном",
                cleanHouse.getId());
        Subtask cleanHouseSubtask2 = new Subtask("Пропылесосить", "Новым пылесосом",
                cleanHouse.getId());
        taskManager.addSubtask(cleanHouseSubtask1);
        taskManager.addSubtask(cleanHouseSubtask2);
        System.out.println(cleanHouse);
        cleanHouseSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(cleanHouseSubtask2);
        System.out.println(cleanHouse);
    }
}