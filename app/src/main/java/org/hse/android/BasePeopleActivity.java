package org.hse.android;

import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Абстрактный базовый класс для активностей, связанных с расписанием (StudentActivity и TeacherActivity)
public abstract class BasePeopleActivity extends AppCompatActivity {
    // Константы
    private static final String TAG = "BaseScheduleActivity"; // Тег для логирования
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977"; // URL для получения времени с сервера

    // Поля для элементов интерфейса (TextView для отображения времени, статуса пары и т.д.)
    protected TextView time; // Отображает текущее время и день недели
    protected TextView status; // Статус пары (идёт/не идёт)
    protected TextView subject; // Название дисциплины
    protected TextView cabinet; // Номер кабинета
    protected TextView corp; // Корпус
    protected TextView teacher; // Имя преподавателя
    protected Date currentTime; // Хранит текущее время, полученное с сервера

    // Клиент для работы с сетью (используется для запроса времени с сервера)
    private OkHttpClient client = new OkHttpClient();

    // Перечисления для типов расписания и режимов отображения
    protected enum ScheduleType {
        DAY, // Расписание на день
        WEEK // Расписание на неделю
    }

    protected enum ScheduleMode {
        STUDENT, // Режим для студентов
        TEACHER // Режим для преподавателей
    }

    // Абстрактный метод для получения ID layout (реализуется в наследниках: StudentActivity, TeacherActivity)
    protected abstract int getLayoutResId();

    // Абстрактный метод для инициализации списка групп или преподавателей (реализуется в наследниках)
    protected abstract void initGroupList(List<Group> groups);

    // Метод жизненного цикла onCreate, вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId()); // Устанавливаем layout для активности (зависит от наследника)

        // --- Инициализация Spinner для выбора группы или преподавателя ---
        final Spinner spinner = findViewById(R.id.groupList); // Находим Spinner по ID
        List<Group> groups = new ArrayList<>(); // Создаём пустой список групп/преподавателей
        initGroupList(groups); // Заполняем список (логика определяется в наследниках)

        // Создаём адаптер для Spinner, чтобы отобразить список групп/преподавателей
        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Устанавливаем стиль выпадающего списка
        spinner.setAdapter(adapter); // Привязываем адаптер к Spinner

        // Устанавливаем слушатель для обработки выбора элемента в Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position); // Получаем выбранный элемент
                // Логируем выбор для отладки, используя строку из strings.xml
                Log.d(TAG, getString(R.string.log_selected_item) + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если ничего не выбрано
            }
        });

        // --- Инициализация остальных элементов интерфейса ---
        time = findViewById(R.id.time); // TextView для отображения времени
        status = findViewById(R.id.status); // TextView для статуса пары
        subject = findViewById(R.id.subject); // TextView для названия дисциплины
        cabinet = findViewById(R.id.cabinet); // TextView для номера кабинета
        corp = findViewById(R.id.corp); // TextView для корпуса
        teacher = findViewById(R.id.teacher); // TextView для имени преподавателя

        // --- Инициализация времени и данных ---
        getTime(); // Запрашиваем текущее время с сервера
        initData(); // Устанавливаем начальные значения для TextView (например, статус пары)

        // --- Инициализация кнопок для перехода к расписанию ---
        Button buttonDay = findViewById(R.id.button_day); // Кнопка "Расписание на день"
        Button buttonWeek = findViewById(R.id.button_week); // Кнопка "Расписание на неделю"

        // Устанавливаем слушатели для кнопок: при нажатии открывается ScheduleActivity
        buttonDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY)); // Переход к расписанию на день
        buttonWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK)); // Переход к расписанию на неделю
    }

    // Метод для обработки нажатия на кнопку "Day" или "Week"
    private void showSchedule(ScheduleType type) {
        Spinner spinner = findViewById(R.id.groupList); // Находим Spinner
        Object selectedItem = spinner.getSelectedItem(); // Получаем выбранный элемент (группу или преподавателя)

        // Проверяем, является ли выбранный элемент объектом Group
        if (!(selectedItem instanceof Group)) {
            return; // Если нет, ничего не делаем
        }

        // Приводим выбранный элемент к типу Group
        Group selectedGroup = (Group) selectedItem;
        // Определяем режим (Student или Teacher) на основе текущей активности
        ScheduleMode mode = this instanceof StudentActivity ? ScheduleMode.STUDENT : ScheduleMode.TEACHER;
        // Переходим к ScheduleActivity, передавая режим, тип расписания, ID группы и имя группы
        showScheduleImpl(mode, type, selectedGroup.getId(), selectedGroup.getName());
    }

    // Метод для запуска ScheduleActivity с передачей параметров
    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, int groupId, String groupName) {
        Intent intent = new Intent(this, ScheduleActivity.class); // Создаём Intent для перехода
        intent.putExtra("ARG_ID", groupId); // Передаём ID группы
        intent.putExtra("ARG_TYPE", type); // Передаём тип расписания (DAY или WEEK)
        intent.putExtra("ARG_MODE", mode); // Передаём режим (STUDENT или TEACHER)
        intent.putExtra("ARG_NAME", groupName); // Передаём имя группы/преподавателя
        intent.putExtra("ARG_TIME", currentTime); // Передаём текущее время
        startActivity(intent); // Запускаем ScheduleActivity
    }

    // Метод для получения текущего времени с сервера
    protected void getTime() {
       // mainViewModel.fetchTime();
       /* Request request = new Request.Builder().url(URL).build(); // Создаём HTTP-запрос
        Call call = client.newCall(request); // Создаём вызов через OkHttp

        // Асинхронно отправляем запрос
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Логируем ошибку, если запрос не удался, используя строку из strings.xml
                Log.e(TAG, getString(R.string.log_get_time_error), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response); // Обрабатываем ответ сервера
            }
        });*/
    }

    // Метод для парсинга ответа сервера с временем
    /*private void parseResponse(Response response) throws IOException {
        Gson gson = new Gson(); // Создаём объект Gson для парсинга JSON
        ResponseBody body = response.body(); // Получаем тело ответа

        // Проверяем, есть ли тело ответа
        if (body == null) {
            return; // Если тело пустое, ничего не делаем
        }

        String string = body.string(); // Преобразуем тело ответа в строку
        Log.d(TAG, string); // Логируем ответ для отладки

        // Парсим JSON в объект TimeResponse
        TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
        String currentTimeVal = timeResponse.getTimeZone().getCurrentTime(); // Извлекаем строку с временем
        // Формат времени: "yyyy-MM-dd HH:mm:ss.SSS"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        try {
            currentTime = simpleDateFormat.parse(currentTimeVal); // Парсим строку в объект Date
        } catch (Exception e) {
            // Логируем ошибку парсинга, используя строку из strings.xml
            Log.e(TAG, getString(R.string.log_parse_error), e);
            return;
        }

        // Обновляем UI в главном потоке
        runOnUiThread(() -> {
            try {
                showTime(currentTime); // Отображаем время в UI
            } catch (Exception e) {
                // Логируем ошибку отображения, используя строку из strings.xml
                Log.e(TAG, getString(R.string.log_show_time_error), e);
            }
        });
    }*/

    // Метод для отображения времени в TextView
    protected void showTime(Date dateTime) {
        // Проверяем, что время не null
        if (dateTime == null) {
            return;
        }

        currentTime = dateTime; // Сохраняем время в поле класса
        // Форматируем время в формат "HH:mm" (например, "14:30")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru"));
        // Форматируем день недели в полное название (например, "Понедельник")
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("ru"));
        String formattedTime = timeFormat.format(currentTime); // Получаем время в формате "HH:mm"
        String dayOfWeek = dayFormat.format(currentTime); // Получаем день недели
        // Приводим первую букву дня недели к заглавной (например, "понедельник" -> "Понедельник")
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        // Формируем строку для отображения: "Сейчас: 14:30, Понедельник"
        String text = getString(R.string.current_time) + formattedTime + ", " + dayOfWeek;
        // Создаём SpannableString для стилизации текста
        SpannableString spannable = new SpannableString(text);
        // Применяем цвет к части текста "Сейчас:" (используем colorPrimary)
        spannable.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 0, getString(R.string.current_time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        time.setText(spannable); // Устанавливаем отформатированный текст в TextView
    }

    // Метод для инициализации начальных данных в TextView
    protected void initData() {
        status.setText(R.string.status_pair); // Устанавливаем статус пары (например, "Идет пара / Нет пар")

        // Устанавливаем заглушки для остальных полей (если они есть в layout)
        if (subject != null) subject.setText(R.string.subject); // Название дисциплины
        if (cabinet != null) cabinet.setText(R.string.cabinet); // Номер кабинета
        if (corp != null) corp.setText(R.string.corp); // Корпус
        if (teacher != null) teacher.setText(R.string.teacher); // Имя преподавателя
    }
}

// Вспомогательный класс для парсинга JSON-ответа от сервера с временем
class TimeResponse {
    @SerializedName("time_zone") // Аннотация Gson для маппинга поля JSON
    private TimeZone timeZone; // Вложенный объект с данными о времени

    // Геттер для получения объекта TimeZone
    public TimeZone getTimeZone() {
        return timeZone;
    }

    // Сеттер для установки объекта TimeZone
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}

// Вспомогательный класс для хранения времени из JSON-ответа
class TimeZone {
    @SerializedName("current_time") // Аннотация Gson для маппинга поля JSON
    private String currentTime; // Строка с текущим временем (например, "2025-04-21 14:30:00.000")

    // Геттер для получения строки с временем
    public String getCurrentTime() {
        return currentTime;
    }

    // Сеттер для установки строки с временем
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}