package org.hse.android;

import androidx.annotation.NonNull;

public class Group {
    private final Integer id;
    private String name;

    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
