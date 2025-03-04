package org.hse.android;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public abstract class BasePeopleActivity extends AppCompatActivity {
    protected static String TAG = "BaseScheduleActivity"; // Будет переопределяться в дочерних классах

    // Поля для элементов интерфейса
    protected TextView time;
    protected TextView status;
    protected TextView subject;
    protected TextView cabinet;
    protected TextView corp;
    protected TextView teacher;
    protected Date currentTime;

    // Абстрактный метод для получения ID layout
    protected abstract int getLayoutResId();

    // Абстрактный метод для инициализации списка групп
    protected abstract void initGroupList(List<Group> groups);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId()); // Используем метод для получения layout

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
            // Здесь можно добавить логику для расписания на день
        });

        buttonWeek.setOnClickListener(v -> {
            Log.d(TAG, "Button Week clicked");
            // Здесь можно добавить логику для расписания на неделю
        });
    }

    protected void initTime() {
        currentTime = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("ru"));
        String formattedTime = timeFormat.format(currentTime);
        String dayOfWeek = dayFormat.format(currentTime);
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        // Устанавливаем текст с разными цветами
        String text = getString(R.string.current_time) + formattedTime + ", " + dayOfWeek;
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 0, getString(R.string.current_time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Красим "Сейчас"

        time.setText(spannable);
    }


    protected void initData() {
        status.setText("Нет пар");

        if (subject != null) subject.setText(R.string.subject);
        if (cabinet != null) cabinet.setText(R.string.cabinet);
        if (corp != null) corp.setText(R.string.corp);
        if (teacher != null) teacher.setText(R.string.teacher);
    }
}