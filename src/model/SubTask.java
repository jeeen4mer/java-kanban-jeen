package model;

public class SubTask extends Task {

    //Конструктор для создания новых объектов, id получаем в InMemoryTasksManager
    public SubTask(String name, String description) {
        super(name, description);
    }

    //Конструктор для загрузки данных из файла. id задается из файла
    public SubTask(String name, String description, int id) {
        super(name, description, id);
    }

    //Конструктор для id
    public SubTask(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public String toString() {
        return "{id=" + getId() + ", name='" + getName() + "', description='" + getDescription() + "', status="
                + getTaskStatus() + "', relatedEpic id=" + getRelationEpicId() + "}\n";
    }
}