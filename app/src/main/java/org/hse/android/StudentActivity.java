package org.hse.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Наследуется от кастомного BasePeopleActivity, в котором реализован общий функционал
public class StudentActivity extends BasePeopleActivity {
    private static final String TAG = "StudentActivity";

    private MainViewModel mainViewModel;
    private Group selectedGroup;       // Выбранная пользователем группа
    private boolean isGroupSelected = false;
    private boolean isTimeLoaded = false;

    // Подключаем соответствующий layout
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_student;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем ViewModel, с которой будем работать
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Подписка на LiveData с текущим временем
        mainViewModel.getCurrentTime().observe(this, date -> {
            if (date != null) {
                currentTime = date;
                Log.d(TAG, "Time loaded: " + currentTime);
                isTimeLoaded = true;
                // Если уже выбрана группа, то можно отобразить расписание
                if (isGroupSelected) {
                    showTime(currentTime);
                }
            }
        });

        // Получаем текущее время (возможно, из интернета)
        mainViewModel.fetchTime();

        // Загружаем список групп из базы данных
        initGroupListFromDatabase();
    }

    // Инициализация списка групп и обработка выбора
    private void initGroupListFromDatabase() {
        final Spinner spinner = findViewById(R.id.groupList); // Получаем Spinner из layout
        final List<Group> groups = new ArrayList<>();

        // Наблюдаем за изменениями в базе групп
        mainViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> groupEntities) {
                if (groupEntities != null) {
                    groups.clear();
                    // Преобразуем сущности в простые объекты Group
                    for (GroupEntity entity : groupEntities) {
                        groups.add(new Group(entity.id, entity.name));
                    }

                    // Создаем адаптер и прикрепляем его к Spinner
                    ArrayAdapter<Group> adapter = new ArrayAdapter<>(StudentActivity.this, android.R.layout.simple_spinner_item, groups);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // Обработка выбора группы пользователем
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedGroup = adapter.getItem(position);
                            isGroupSelected = true;
                            Log.d(TAG, "Selected group: " + selectedGroup.getName());

                            // Если время уже загружено — отобразим данные
                            if (isTimeLoaded) {
                                showTime(currentTime);
                            } else {
                                Log.d(TAG, "Waiting for time to load...");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedGroup = null;
                            isGroupSelected = false;
                        }
                    });
                }
            }
        });
    }

    // Переопределенный метод для отображения расписания на дату
    @Override
    protected void showTime(Date dateTime) {
        super.showTime(dateTime);
        if (selectedGroup == null || dateTime == null) return;

        // Устанавливаем начало и конец дня
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date endDate = cal.getTime();

        // Загружаем расписание из базы на этот день и по выбранной группе
        mainViewModel.getTimeTableByDateAndGroupId(startDate, endDate, selectedGroup.getId()).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                if (list != null) {
                    // Проверяем, есть ли пара на текущий день
                    for (TimeTableWithTeacherEntity entity : list) {
                        if (isSameDay(entity.timeTableEntity.timeStart, dateTime)) {
                            initDataFromTimeTable(entity);
                            return;
                        }
                    }
                    // Если ничего не найдено — очищаем поля
                    initDataFromTimeTable(null);
                } else {
                    Log.w(TAG, "No timetable returned from DB.");
                }
            }
        });
    }

    // Проверка, что две даты — это один день
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Отображение данных о паре (или очистка при её отсутствии)
    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableTeacherEntity) {
        if (timeTableTeacherEntity == null) {
            status.setText(R.string.no_pair);
            subject.setText(R.string.subject);
            cabinet.setText(R.string.cabinet);
            corp.setText(R.string.corp);
            teacher.setText(R.string.teacher);
            return;
        }

        status.setText(R.string.in_progress);
        TimeTableEntity timeTableEntity = timeTableTeacherEntity.timeTableEntity;
        subject.setText(timeTableEntity.subName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableTeacherEntity.teacherEntity.fio);
    }

    // Метод из базового класса, оставлен без изменений
    @Override
    protected void initData() {
        super.initData();
    }

    // Метод не используется, но переопределен для совместимости с базовым классом
    @Override
    protected void initGroupList(List<Group> groups) {
        // Не используется — оставлено для совместимости
    }
}
