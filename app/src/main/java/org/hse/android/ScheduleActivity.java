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

        // Инициализация адаптера
        adapter = new ItemAdapter(this::onScheduleItemClick);
        recyclerView.setAdapter(adapter);

        // Устанавливаем заголовок
        if (title != null) {
            String titleText = (type == BasePeopleActivity.ScheduleType.DAY ? "Day Schedule" : "Week Schedule") +
                    " for " + (mode == BasePeopleActivity.ScheduleMode.STUDENT ? "Student" : "Teacher") +
                    " (ID: " + id + ")";
            title.setText(titleText);
        }

        initData();
    }

    private void initData() {
        List<ScheduleItem> list = new ArrayList<>();

        ScheduleItemHeader header = new ScheduleItemHeader("Понедельник, 28 января");
        list.add(header);

        ScheduleItem item = new ScheduleItem();
        item.setStart("10:00");
        item.setEnd("11:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Конюшковский пр-д., д. 3");
        item.setTeacher("Преп. Гущин Михаил Юрьевич");
        list.add(item);

        item = new ScheduleItem();
        item.setStart("12:00");
        item.setEnd("13:00");
        item.setType("Практическое занятие");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Конюшковский пр-д., д. 3");
        item.setTeacher("Преп. Гущин Михаил Юрьевич");
        list.add(item);

        adapter.setDataList(list);
    }

    private void onScheduleItemClick(ScheduleItem data) {
        // Обработчик клика (пока пустой, можно добавить логику позже)
    }
}