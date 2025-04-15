package org.hse.android;

public class ScheduleItem {
    private String start;
    private String end;
    private String type;
    private String name;
    private String place;
    private String teacher;

    public ScheduleItem(String start, String end, String type, String name, String place, String teacher) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.name = name;
        this.place = place;
        this.teacher = teacher;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getTeacher() {
        return teacher;
    }
}

class ScheduleItemHeader extends ScheduleItem {
    private String title;

    public ScheduleItemHeader(String title) {
        super("", "", "", "", "", ""); // Пустые значения для полей базового класса
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}