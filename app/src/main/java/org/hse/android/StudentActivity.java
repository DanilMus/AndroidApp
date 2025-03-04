package org.hse.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {
    private static final String TAG = "StudentActivity";
    // Поля для элементов интерфейса
    private TextView time;
    private TextView status;
    private TextView subject;
    private TextView cabinet;
    private TextView corp;
    private TextView teacher;
    private Date currentTime; // Добавляем поле для хранения текущей даты

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Инициализация Spinner
        final Spinner spinner = findViewById(R.id.groupList);

        List<Group> groups = new ArrayList<>();
        initGroupList(groups);

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position);
                Log.d(TAG, "selectedItem: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });

        // Инициализация остальных элементов
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        cabinet = findViewById(R.id.cabinet);
        corp = findViewById(R.id.corp);
        teacher = findViewById(R.id.teacher);

        initTime();
        initData();

        // Инициализация кнопок
        Button buttonDay = findViewById(R.id.button_day);
        Button buttonWeek = findViewById(R.id.button_week);

        buttonDay.setOnClickListener(v -> {
            Log.d(TAG, "Button Day clicked");
            // Здесь добавь логику для расписания на день
        });

        buttonWeek.setOnClickListener(v -> {
            Log.d(TAG, "Button Week clicked");
            // Здесь добавь логику для расписания на неделю
        });
    }

    private void initGroupList(List<Group> groups) {
        // Массивы данных для формирования групп
        String[] educationPrograms = {"ПИ", "БИ"};  // Программная инженерия, Бизнес-информатика и т.д.
        int[] admissionYears = {22, 23, 24};    // Годы поступления
        int[] groupNumbers = {1, 2, 3};         // Номера групп

        int id = 1;
        // Создаем группы по шаблону "образовательная программа-год поступления-номер группы"
        for (String program : educationPrograms) {
            for (int year : admissionYears) {
                for (int groupNum : groupNumbers) {
                    // Формируем название группы по шаблону
                    String groupName = program + "-" + year + "-" + groupNum;
                    groups.add(new Group(id++, groupName));
                }
            }
        }
    }

    private void initTime() {
        currentTime = new Date();
        // Формат: "HH:mm, День недели" (например, "12:00, Понедельник")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("ru")); // "EEEE" — полный день недели
        String formattedTime = timeFormat.format(currentTime);
        String dayOfWeek = dayFormat.format(currentTime);
        // Приводим первую букву дня недели к заглавной
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);
        // Объединяем время и день недели
        time.setText(formattedTime + ", " + dayOfWeek);
    }

    private void initData() {
        status.setText("Нет пар");

        // Проверка на null перед использованием
        if (subject != null) subject.setText("Дисциплина");
        if (cabinet != null) cabinet.setText("Кабинет");
        if (corp != null) corp.setText("Корпус");
        if (teacher != null) teacher.setText("Преподаватель");
    }

    // Внутренний класс Group
    public static class Group {
        private final int id;
        private final String name;

        public Group(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}