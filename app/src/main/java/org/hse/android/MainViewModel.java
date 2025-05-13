package org.hse.android;

import static android.content.ContentValues.TAG;

import static org.hse.android.BasePeopleActivity.URL;

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

public class MainViewModel extends AndroidViewModel {
    private HseRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new HseRepository(application);
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return repository.getGroups();
    }

    public LiveData<List<TeacherEntity>> getTeachers() {
        return repository.getTeachers();
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndGroupId(Date startDate, Date endDate, int groupId) {
        return repository.getTimeTableByDateAndGroupId(startDate, endDate, groupId);
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndTeacherId(Date startDate, Date endDate, int teacherId) {
        return repository.getTimeTableByDateAndTeacherId(startDate, endDate, teacherId);
    }

    // Новый метод для получения времени через LiveData
    private MutableLiveData<Date> currentTime = new MutableLiveData<>();

    public LiveData<Date> getCurrentTime() {
        return currentTime;
    }

    public void fetchTime() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching time", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    Gson gson = new Gson();
                    TimeResponse timeResponse = gson.fromJson(response.body().string(), TimeResponse.class);
                    String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    try {
                        Date date = simpleDateFormat.parse(currentTimeVal);
                        currentTime.postValue(date);
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing time", e);
                    }
                }
            }
        });
    }
}