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

public class TeacherActivity extends BasePeopleActivity {
    private static final String TAG = "TeacherActivity";

    // ViewModel, через который получаем данные
    private MainViewModel mainViewModel;

    // Выбранный преподаватель
    private Group selectedTeacher;

    // Флаги для контроля состояния загрузки
    private boolean isTeacherSelected = false;
    private boolean isTimeLoaded = false;

    @Override
    protected int getLayoutResId() {
        // Указываем layout-файл для этой Activity
        return R.layout.activity_teacher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем ViewModel для работы с данными
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Подписываемся на LiveData текущего времени
        mainViewModel.getCurrentTime().observe(this, date -> {
            if (date != null) {
                // Сохраняем текущее время и устанавливаем флаг
                currentTime = date;
                isTimeLoaded = true;
                Log.d(TAG, "Time loaded: " + currentTime);

                // Если преподаватель уже выбран — отображаем расписание
                if (isTeacherSelected) {
                    showTime(currentTime);
                }
            }
        });

        // Запрашиваем время с сервера
        mainViewModel.fetchTime();

        // Инициализируем список преподавателей из базы данных
        initTeacherListFromDatabase();
    }

    // Получение списка преподавателей и установка обработчика выбора
    private void initTeacherListFromDatabase() {
        final Spinner spinner = findViewById(R.id.groupList);
        final List<Group> teachers = new ArrayList<>();

        // Подписываемся на LiveData со списком преподавателей
        mainViewModel.getTeachers().observe(this, new Observer<List<TeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TeacherEntity> teacherEntities) {
                if (teacherEntities != null) {
                    teachers.clear();
                    // Конвертируем TeacherEntity в Group (имя + id)
                    for (TeacherEntity entity : teacherEntities) {
                        teachers.add(new Group(entity.id, entity.fio));
                    }

                    // Настраиваем адаптер для выпадающего списка
                    ArrayAdapter<Group> adapter = new ArrayAdapter<>(TeacherActivity.this, android.R.layout.simple_spinner_item, teachers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // Устанавливаем слушатель выбора преподавателя
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedTeacher = adapter.getItem(position);
                            isTeacherSelected = true;
                            Log.d(TAG, "Selected teacher: " + selectedTeacher.getName());

                            // Если время уже загружено, показываем расписание
                            if (isTimeLoaded) {
                                showTime(currentTime);
                            } else {
                                Log.d(TAG, "Waiting for time to load...");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedTeacher = null;
                            isTeacherSelected = false;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void showTime(Date dateTime) {
        super.showTime(dateTime);
        if (selectedTeacher == null || dateTime == null) return;

        // Устанавливаем границы текущего дня: [00:00, 23:59]
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date endDate = cal.getTime();

        // Получаем расписание для выбранного преподавателя и даты
        mainViewModel.getTimeTableByDateAndTeacherId(startDate, endDate, selectedTeacher.getId()).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                if (list != null) {
                    for (TimeTableWithTeacherEntity entity : list) {
                        // Проверяем, есть ли пара в этот день
                        if (isSameDay(entity.timeTableEntity.timeStart, dateTime)) {
                            initDataFromTimeTable(entity);
                            return;
                        }
                    }
                    // Если ничего не найдено
                    initDataFromTimeTable(null);
                } else {
                    Log.w(TAG, "No timetable returned for teacher");
                }
            }
        });
    }

    // Проверка, совпадают ли даты по дню
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Отображение информации о паре, либо заглушки, если пары нет
    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableTeacherEntity) {
        if (timeTableTeacherEntity == null) {
            // Если пары нет
            status.setText(R.string.no_pair);
            subject.setText(R.string.subject);
            cabinet.setText(R.string.cabinet);
            corp.setText(R.string.corp);
            teacher.setText(R.string.teacher);
            return;
        }

        // Если пара есть — заполняем поля
        status.setText(R.string.in_progress);
        TimeTableEntity timeTableEntity = timeTableTeacherEntity.timeTableEntity;
        subject.setText(timeTableEntity.subName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableTeacherEntity.teacherEntity.fio);
    }

    // Переопределён, но не используется
    @Override
    protected void initData() {
        super.initData();
    }

    // Метод обязателен по контракту, но не нужен для преподавателей
    @Override
    protected void initGroupList(List<Group> groups) {
        // Не используется
    }
}

