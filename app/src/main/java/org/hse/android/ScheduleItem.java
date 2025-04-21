package org.hse.android;

public class ScheduleItem {
    private String start;
    private String end;
    private String type;
    private String name;
    private String place;
    private String teacher;

    public ScheduleItem() {
        this.start = "";
        this.end = "";
        this.type = "";
        this.name = "";
        this.place = "";
        this.teacher = "";
    }

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

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}

class ScheduleItemHeader extends ScheduleItem {
    private String title;

    public ScheduleItemHeader(String title) {
        super("", "", "", "", "", "");
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}