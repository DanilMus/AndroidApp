package org.hse.android;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Класс активности для отображения расписания (на день или неделю) для студента или преподавателя
public class ScheduleActivity extends AppCompatActivity {
    // Поля для хранения параметров, переданных из BasePeopleActivity
    private BasePeopleActivity.ScheduleType type; // Тип расписания (DAY или WEEK)
    private BasePeopleActivity.ScheduleMode mode; // Режим отображения (STUDENT или TEACHER)
    private int id; // ID группы или преподавателя
    private String name; // Имя группы или преподавателя
    private Date currentTime; // Текущее время, полученное с сервера

    // Поля для элементов интерфейса
    private TextView title; // TextView для отображения заголовка расписания
    private RecyclerView recyclerView; // RecyclerView для отображения списка занятий
    private ItemAdapter adapter; // Адаптер для RecyclerView

    // Метод жизненного цикла onCreate, вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule); // Устанавливаем layout для активности

        // --- Извлечение параметров из Intent ---
        // Получаем данные, переданные из BasePeopleActivity через Intent
        type = (BasePeopleActivity.ScheduleType) getIntent().getSerializableExtra("ARG_TYPE"); // Тип расписания
        mode = (BasePeopleActivity.ScheduleMode) getIntent().getSerializableExtra("ARG_MODE"); // Режим отображения
        id = getIntent().getIntExtra("ARG_ID", -1); // ID группы/преподавателя, -1 — значение по умолчанию
        name = getIntent().getStringExtra("ARG_NAME"); // Имя группы/преподавателя
        currentTime = (Date) getIntent().getSerializableExtra("ARG_TIME"); // Текущее время

        // --- Инициализация UI ---
        title = findViewById(R.id.title); // Находим TextView для заголовка
        TextView dateView = findViewById(R.id.date); // Находим TextView для отображения даты
        recyclerView = findViewById(R.id.listView); // Находим RecyclerView для списка занятий

        // --- Настройка RecyclerView ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Устанавливаем вертикальный LinearLayoutManager
        // Добавляем разделитель между элементами списка
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // --- Инициализация адаптера для RecyclerView ---
        adapter = new ItemAdapter(this::onScheduleItemClick); // Создаём адаптер с обработчиком кликов
        recyclerView.setAdapter(adapter); // Привязываем адаптер к RecyclerView

        // --- Установка заголовка расписания ---
        if (title != null) {
            // Формируем текст заголовка: "Day Schedule for Student ПИ-22-1 (ID: 1)" или аналогичный
            String titleText = (type == BasePeopleActivity.ScheduleType.DAY ?
                    getString(R.string.day_schedule) : getString(R.string.week_schedule)) + // "Day Schedule" или "Week Schedule"
                    getString(R.string.for_text) + // " for "
                    (mode == BasePeopleActivity.ScheduleMode.STUDENT ?
                            getString(R.string.student_text) : getString(R.string.teacher_text)) + // "Student" или "Teacher"
                    " " + name + // Имя группы/преподавателя
                    getString(R.string.id_prefix) + id + getString(R.string.id_suffix); // " (ID: " + id + ")"
            title.setText(titleText); // Устанавливаем текст в TextView
        }

        // --- Установка отформатированной даты ---
        if (dateView != null) {
            String formattedTime = "";
            if (currentTime != null) {
                // Форматируем дату в нужный формат: "Апрель 21, Понедельник"
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.forLanguageTag("ru")); // Полное название месяца
                SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Число месяца
                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("ru")); // Полное название дня недели

                String month = monthFormat.format(currentTime); // Получаем месяц (например, "апрель")
                String day = dayFormat.format(currentTime); // Получаем число (например, "21")
                String dayOfWeek = dayOfWeekFormat.format(currentTime); // Получаем день недели (например, "понедельник")

                // Приводим первую букву месяца и дня недели к заглавной
                month = month.substring(0, 1).toUpperCase() + month.substring(1); // "апрель" -> "Апрель"
                dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1); // "понедельник" -> "Понедельник"

                // Формируем строку: "Апрель 21, Понедельник"
                formattedTime = month + " " + day + ", " + dayOfWeek;
            } else {
                // Если время не удалось получить, отображаем сообщение из strings.xml
                formattedTime = getString(R.string.undefined_name);
            }
            dateView.setText(formattedTime); // Устанавливаем отформатированное время в TextView
        }

        // Инициализируем тестовые данные для списка расписания
        initData();
    }

    // Метод для инициализации тестовых данных (заполняет список расписания)
    private void initData() {
        List<ScheduleItem> list = new ArrayList<>(); // Создаём пустой список для элементов расписания

        // --- Добавляем заголовок дня (например, "Понедельник, 28 января") ---
        ScheduleItemHeader header = new ScheduleItemHeader(getString(R.string.schedule_header_monday)); // Заголовок из strings.xml
        list.add(header); // Добавляем заголовок в список

        // --- Добавляем первое занятие ---
        ScheduleItem item = new ScheduleItem(); // Создаём новый элемент расписания
        item.setStart(getString(R.string.start_time_10_00)); // Устанавливаем время начала: "10:00"
        item.setEnd(getString(R.string.end_time_11_00)); // Устанавливаем время окончания: "11:00"
        item.setType(getString(R.string.practical_lesson)); // Тип занятия: "Практическое занятие"
        item.setName(getString(R.string.data_analysis_en)); // Название: "Анализ данных (анг)"
        item.setPlace(getString(R.string.room_503)); // Место: "Ауд. 503, Конюшковский пр-д., д. 3"
        item.setTeacher(getString(R.string.teacher_gushchin)); // Преподаватель: "Преп. Гущин Михаил Юрьевич"
        list.add(item); // Добавляем занятие в список

        // --- Добавляем второе занятие ---
        item = new ScheduleItem(); // Создаём ещё один элемент расписания
        item.setStart(getString(R.string.start_time_12_00)); // Устанавливаем время начала: "12:00"
        item.setEnd(getString(R.string.end_time_13_00)); // Устанавливаем время окончания: "13:00"
        item.setType(getString(R.string.practical_lesson)); // Тип занятия: "Практическое занятие"
        item.setName(getString(R.string.data_analysis_en)); // Название: "Анализ данных (анг)"
        item.setPlace(getString(R.string.room_503)); // Место: "Ауд. 503, Конюшковский пр-д., д. 3"
        item.setTeacher(getString(R.string.teacher_gushchin)); // Преподаватель: "Преп. Гущин Михаил Юрьевич"
        list.add(item); // Добавляем занятие в список

        // Передаём список данных в адаптер для отображения
        adapter.setDataList(list);
    }

    // Метод-обработчик кликов по элементам списка (пока пустой)
    private void onScheduleItemClick(ScheduleItem data) {
        // Здесь можно добавить логику обработки клика по элементу расписания
        // Например, показать детали занятия или открыть новую активность
    }
}