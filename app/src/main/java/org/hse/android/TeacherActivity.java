package org.hse.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeacherActivity extends BasePeopleActivity {
    private static final String TAG = "TeacherActivity";
    private MainViewModel mainViewModel;
    private Group selectedTeacher;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_teacher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getCurrentTime().observe(this, date -> {
            if (date != null) {
                currentTime = date;
                showTime(currentTime);
            }
        });
        mainViewModel.fetchTime();
        initTeacherListFromDatabase();
    }

    private void initTeacherListFromDatabase() {
        final Spinner spinner = findViewById(R.id.groupList);
        final List<Group> teachers = new ArrayList<>();

        mainViewModel.getTeachers().observe(this, new Observer<List<TeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TeacherEntity> teacherEntities) {
                if (teacherEntities != null) {
                    teachers.clear();
                    for (TeacherEntity entity : teacherEntities) {
                        teachers.add(new Group(entity.id, entity.fio));
                    }
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(TeacherActivity.this, android.R.layout.simple_spinner_item, teachers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedTeacher = (Group) adapter.getItem(position);
                            Log.d(TAG, "Selected teacher: " + selectedTeacher.getName());
                            showTime(currentTime); // Автоматический перезапуск
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedTeacher = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void showTime(Date dateTime) {
        super.showTime(dateTime);
        if (selectedTeacher == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date endDate = cal.getTime();

        mainViewModel.getTimeTableByDateAndTeacherId(startDate, endDate, selectedTeacher.getId()).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                if (list != null) {
                    for (TimeTableWithTeacherEntity entity : list) {
                        if (isSameDay(entity.timeTableEntity.timeStart, dateTime)) {
                            initDataFromTimeTable(entity);
                            return;
                        }
                    }
                    initDataFromTimeTable(null);
                }
            }
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableTeacherEntity) {
        if (timeTableTeacherEntity == null) {
            status.setText(R.string.no_pair);
            subject.setText(R.string.subject);
            cabinet.setText(R.string.cabinet);
            corp.setText(R.string.corp);
            teacher.setText(R.string.teacher);
            return;
        }
        status.setText(R.string.in_progress);
        TimeTableEntity timeTableEntity = timeTableTeacherEntity.timeTableEntity;
        subject.setText(timeTableEntity.subName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableTeacherEntity.teacherEntity.fio);
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        // Этот метод больше не используется, так как данные загружаются из базы
        // Оставляем пустую реализацию для совместимости с BasePeopleActivity
    }
}