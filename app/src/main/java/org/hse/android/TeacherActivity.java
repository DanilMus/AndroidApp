package org.hse.android;

import java.util.List;

public class TeacherActivity extends BasePeopleActivity {
    private static final String TAG = "TeacherActivity";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_teacher; // Указываем layout для преподавателей
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        groups.add(new Group(1, "Крутых А.И."));
        groups.add(new Group(2, "Бомбезнов К.Л."));
    }
}