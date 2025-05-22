package org.hse.android;

import static android.content.ContentValues.TAG; // Для логирования ошибок

import static org.hse.android.BasePeopleActivity.URL; // URL для запроса времени

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// ViewModel, связанная с жизненным циклом приложения
public class MainViewModel extends AndroidViewModel {
    private HseRepository repository; // Репозиторий, работающий с базой данных

    // Конструктор, в котором инициализируется репозиторий
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new HseRepository(application);
    }

    // Получение списка групп из репозитория (базы данных)
    public LiveData<List<GroupEntity>> getGroups() {
        return repository.getGroups();
    }

    // Получение списка преподавателей
    public LiveData<List<TeacherEntity>> getTeachers() {
        return repository.getTeachers();
    }

    // Получение расписания по дате и ID группы
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndGroupId(Date startDate, Date endDate, int groupId) {
        return repository.getTimeTableByDateAndGroupId(startDate, endDate, groupId);
    }

    // Получение расписания по дате и ID преподавателя
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndTeacherId(Date startDate, Date endDate, int teacherId) {
        return repository.getTimeTableByDateAndTeacherId(startDate, endDate, teacherId);
    }

    // LiveData для хранения текущего времени, полученного с сервера
    private MutableLiveData<Date> currentTime = new MutableLiveData<>();

    // Метод для предоставления LiveData наружу (например, активности или фрагменту)
    public LiveData<Date> getCurrentTime() {
        return currentTime;
    }

    // Метод для асинхронного получения текущего времени с сервера
    public void fetchTime() {
        OkHttpClient client = new OkHttpClient(); // HTTP-клиент
        Request request = new Request.Builder().url(URL).build(); // Запрос по URL

        // Выполняем асинхронный HTTP-запрос
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Логируем ошибку, если запрос не удался
                Log.e(TAG, "Error fetching time", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Проверка, что тело ответа не null
                if (response.body() != null) {
                    Gson gson = new Gson(); // Объект для парсинга JSON
                    // Преобразуем JSON-ответ в объект TimeResponse
                    TimeResponse timeResponse = gson.fromJson(response.body().string(), TimeResponse.class);
                    // Извлекаем строковое представление времени
                    String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
                    // Создаем форматтер для разбора даты
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    try {
                        // Парсим строку в объект Date
                        Date date = simpleDateFormat.parse(currentTimeVal);
                        // Обновляем значение LiveData (с потока обратного вызова)
                        currentTime.postValue(date);
                    } catch (ParseException e) {
                        // Логируем ошибку, если не удалось распарсить дату
                        Log.e(TAG, "Error parsing time", e);
                    }
                }
            }
        });
    }
}
