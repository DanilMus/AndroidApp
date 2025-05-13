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

public class StudentActivity extends BasePeopleActivity {
    private static final String TAG = "StudentActivity";
    private MainViewModel mainViewModel;
    private Group selectedGroup;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_student;
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
        initGroupListFromDatabase();
    }

    private void initGroupListFromDatabase() {
        final Spinner spinner = findViewById(R.id.groupList);
        final List<Group> groups = new ArrayList<>();

        mainViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> groupEntities) {
                if (groupEntities != null) {
                    groups.clear();
                    for (GroupEntity entity : groupEntities) {
                        groups.add(new Group(entity.id, entity.name));
                    }
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(StudentActivity.this, android.R.layout.simple_spinner_item, groups);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedGroup = (Group) adapter.getItem(position);
                            Log.d(TAG, "Selected group: " + selectedGroup.getName());
                            showTime(currentTime); // Автоматический перезапуск
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedGroup = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void showTime(Date dateTime) {
        super.showTime(dateTime);
        if (selectedGroup == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date endDate = cal.getTime();

        mainViewModel.getTimeTableByDateAndGroupId(startDate, endDate, selectedGroup.getId()).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
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