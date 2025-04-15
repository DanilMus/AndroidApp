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

public abstract class BasePeopleActivity extends AppCompatActivity {
    private static final String TAG = "BaseScheduleActivity";
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";

    // Поля для элементов интерфейса
    protected TextView time;
    protected TextView status;
    protected TextView subject;
    protected TextView cabinet;
    protected TextView corp;
    protected TextView teacher;
    protected Date currentTime;

    // Клиент для работы с сетью
    private OkHttpClient client = new OkHttpClient();

    // Перечисления из скриншота
    protected enum ScheduleType {
        DAY,
        WEEK
    }

    protected enum ScheduleMode {
        STUDENT,
        TEACHER
    }

    // Абстрактный метод для получения ID layout
    protected abstract int getLayoutResId();

    // Абстрактный метод для инициализации списка групп
    protected abstract void initGroupList(List<Group> groups);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

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

        // Инициализация времени и данных
        getTime(); // Запрашиваем время с сервера
        initData();

        // Инициализация кнопок
        Button buttonDay = findViewById(R.id.button_day);
        Button buttonWeek = findViewById(R.id.button_week);

        buttonDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY));
        buttonWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK));
    }

    private void showSchedule(ScheduleType type) {
        Spinner spinner = findViewById(R.id.groupList);
        Object selectedItem = spinner.getSelectedItem();

        if (!(selectedItem instanceof Group)) {
            return;
        }

        // Определяем режим (STUDENT или TEACHER) на основе текущей активности
        ScheduleMode mode = this instanceof StudentActivity ? ScheduleMode.STUDENT : ScheduleMode.TEACHER;
        showScheduleImpl(mode, type, (Group) selectedItem);
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, Group group) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("ARG_ID", group.getId());
        intent.putExtra("ARG_TYPE", type);
        intent.putExtra("ARG_MODE", mode);
        startActivity(intent);
    }

    protected void getTime() {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getTime", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response);
            }
        });
    }

    private void parseResponse(Response response) throws IOException {
        Gson gson = new Gson();
        ResponseBody body = response.body();

        if (body == null) {
            return;
        }

        String string = body.string();
        Log.d(TAG, string);

        TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
        String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        try {
            currentTime = simpleDateFormat.parse(currentTimeVal);
        } catch (Exception e) {
            Log.e(TAG, "parse", e);
            return;
        }

        runOnUiThread(() -> {
            try {
                showTime(currentTime);
            } catch (Exception e) {
                Log.e(TAG, "showTime", e);
            }
        });
    }

    protected void showTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }

        currentTime = dateTime;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru"));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("ru"));
        String formattedTime = timeFormat.format(currentTime);
        String dayOfWeek = dayFormat.format(currentTime);
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        String text = getString(R.string.current_time) + formattedTime + ", " + dayOfWeek;
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 0, getString(R.string.current_time).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        time.setText(spannable);
    }

    protected void initData() {
        status.setText(R.string.status_pair);

        if (subject != null) subject.setText(R.string.subject);
        if (cabinet != null) cabinet.setText(R.string.cabinet);
        if (corp != null) corp.setText(R.string.corp);
        if (teacher != null) teacher.setText(R.string.teacher);
    }
}

// Классы для парсинга JSON-ответа
class TimeResponse {
    @SerializedName("time_zone")
    private TimeZone timeZone;

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}

class TimeZone {
    @SerializedName("current_time")
    private String currentTime;

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}