package org.hse.android;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private BasePeopleActivity.ScheduleType type;
    private BasePeopleActivity.ScheduleMode mode;
    private int id;

    private TextView title;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Извлекаем параметры из Intent
        type = (BasePeopleActivity.ScheduleType) getIntent().getSerializableExtra("ARG_TYPE");
        mode = (BasePeopleActivity.ScheduleMode) getIntent().getSerializableExtra("ARG_MODE");
        id = getIntent().getIntExtra("ARG_ID", -1);

        // Инициализация UI
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.listView);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Инициализация адаптера с пустым списком и обработчиком кликов
        List<ScheduleItem> dataList = new ArrayList<>();
        adapter = new ItemAdapter(dataList, position -> {
            // Обработчик клика (пока пустой, можно добавить логику позже)
        });
        recyclerView.setAdapter(adapter);

        // Устанавливаем заголовок
        if (title != null) {
            String titleText = (type == BasePeopleActivity.ScheduleType.DAY ? "Day Schedule" : "Week Schedule") +
                    " for " + (mode == BasePeopleActivity.ScheduleMode.STUDENT ? "Student" : "Teacher") +
                    " (ID: " + id + ")";
            title.setText(titleText);
        }

        // Здесь можно добавить логику загрузки данных в dataList и обновления адаптера
        // Например: adapter.updateData(newList);
    }
}

