package org.hse.android;

// Класс ScheduleItem представляет элемент расписания (например, одно занятие)
public class ScheduleItem {
    // Поля для хранения данных о занятии
    private String start; // Время начала занятия (например, "10:00")
    private String end; // Время окончания занятия (например, "11:00")
    private String type; // Тип занятия (например, "Практическое занятие")
    private String name; // Название дисциплины (например, "Анализ данных (анг)")
    private String place; // Место проведения (например, "Ауд. 503, Конюшковский пр-д., д. 3")
    private String teacher; // Имя преподавателя (например, "Преп. Гущин Михаил Юрьевич")

    // Конструктор по умолчанию
    public ScheduleItem() {
        // Инициализируем все поля пустыми строками
        this.start = "";
        this.end = "";
        this.type = "";
        this.name = "";
        this.place = "";
        this.teacher = "";
    }

    // Конструктор с параметрами для создания объекта с заданными значениями
    public ScheduleItem(String start, String end, String type, String name, String place, String teacher) {
        this.start = start; // Устанавливаем время начала
        this.end = end; // Устанавливаем время окончания
        this.type = type; // Устанавливаем тип занятия
        this.name = name; // Устанавливаем название дисциплины
        this.place = place; // Устанавливаем место проведения
        this.teacher = teacher; // Устанавливаем имя преподавателя
    }

    // Геттер для времени начала занятия
    public String getStart() {
        return start; // Возвращаем значение поля start
    }

    // Сеттер для времени начала занятия
    public void setStart(String start) {
        this.start = start; // Устанавливаем новое значение для start
    }

    // Геттер для времени окончания занятия
    public String getEnd() {
        return end; // Возвращаем значение поля end
    }

    // Сеттер для времени окончания занятия
    public void setEnd(String end) {
        this.end = end; // Устанавливаем новое значение для end
    }

    // Геттер для типа занятия
    public String getType() {
        return type; // Возвращаем значение поля type
    }

    // Сеттер для типа занятия
    public void setType(String type) {
        this.type = type; // Устанавливаем новое значение для type
    }

    // Геттер для названия дисциплины
    public String getName() {
        return name; // Возвращаем значение поля name
    }

    // Сеттер для названия дисциплины
    public void setName(String name) {
        this.name = name; // Устанавливаем новое значение для name
    }

    // Геттер для места проведения занятия
    public String getPlace() {
        return place; // Возвращаем значение поля place
    }

    // Сеттер для места проведения занятия
    public void setPlace(String place) {
        this.place = place; // Устанавливаем новое значение для place
    }

    // Геттер для имени преподавателя
    public String getTeacher() {
        return teacher; // Возвращаем значение поля teacher
    }

    // Сеттер для имени преподавателя
    public void setTeacher(String teacher) {
        this.teacher = teacher; // Устанавливаем новое значение для teacher
    }
}

// Класс ScheduleItemHeader — наследник ScheduleItem, используется для представления заголовков дней в расписании
class ScheduleItemHeader extends ScheduleItem {
    // Поле для хранения заголовка (например, "Понедельник, 28 января")
    private String title;

    // Конструктор для создания заголовка
    public ScheduleItemHeader(String title) {
        // Вызываем конструктор родительского класса с пустыми значениями, так как заголовок не использует поля ScheduleItem
        super("", "", "", "", "", "");
        this.title = title; // Устанавливаем значение заголовка
    }

    // Геттер для получения заголовка
    public String getTitle() {
        return title; // Возвращаем значение поля title
    }
}