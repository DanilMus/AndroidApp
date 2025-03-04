package org.hse.android;

import java.util.List;

public class StudentActivity extends BasePeopleActivity {
    private static final String TAG = "StudentActivity";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_student; // Указываем layout для студентов
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        // Массивы данных для формирования групп
        String[] educationPrograms = {"ПИ", "БИ"};  // Программная инженерия, Бизнес-информатика и т.д.
        int[] admissionYears = {22, 23, 24};    // Годы поступления
        int[] groupNumbers = {1, 2, 3};         // Номера групп

        int id = 1;
        // Создаем группы по шаблону "образовательная программа-год поступления-номер группы"
        for (String program : educationPrograms) {
            for (int year : admissionYears) {
                for (int groupNum : groupNumbers) {
                    String groupName = program + "-" + year + "-" + groupNum;
                    groups.add(new Group(id++, groupName));
                }
            }
        }
    }
}