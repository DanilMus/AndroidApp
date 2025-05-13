package org.hse.android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private BasePeopleActivity.ScheduleType type;
    private BasePeopleActivity.ScheduleMode mode;
    private int id;
    private String name;
    private Date currentTime;
    private TextView title;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        type = (BasePeopleActivity.ScheduleType) getIntent().getSerializableExtra("ARG_TYPE");
        mode = (BasePeopleActivity.ScheduleMode) getIntent().getSerializableExtra("ARG_MODE");
        id = getIntent().getIntExtra("ARG_ID", -1);
        name = getIntent().getStringExtra("ARG_NAME");
        currentTime = (Date) getIntent().getSerializableExtra("ARG_TIME");

        title = findViewById(R.id.title);
        TextView dateView = findViewById(R.id.date);
        recyclerView = findViewById(R.id.listView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new ItemAdapter(this::onScheduleItemClick);
        recyclerView.setAdapter(adapter);

        if (title != null) {
            String titleText = (type == BasePeopleActivity.ScheduleType.DAY ?
                    getString(R.string.day_schedule) : getString(R.string.week_schedule)) +
                    " " + getString(R.string.for_text) + " " +
                    (mode == BasePeopleActivity.ScheduleMode.STUDENT ?
                            getString(R.string.student_text) : getString(R.string.teacher_text)) +
                    " " + name +
                    " " + getString(R.string.id_prefix) + id + getString(R.string.id_suffix);
            title.setText(titleText);
        }

        if (dateView != null) {
            String formattedTime;
            if (currentTime != null) {
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.forLanguageTag("ru"));
                SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("ru"));

                String month = monthFormat.format(currentTime);
                String day = dayFormat.format(currentTime);
                String dayOfWeek = dayOfWeekFormat.format(currentTime);

                // Приводим первую букву каждого слова к заглавной
                month = month.substring(0, 1).toUpperCase() + month.substring(1);
                dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

                formattedTime = month + " " + day + ", " + dayOfWeek;
            } else {
                formattedTime = getString(R.string.undefined_name);
            }
            dateView.setText(formattedTime);
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        filterItem(currentTime);
    }

    private void filterItem(Date dateTime) {
        if (dateTime == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        Date endDate;

        if (type == BasePeopleActivity.ScheduleType.DAY) {
            endDate = startDate;
        } else {
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            cal.add(Calendar.DAY_OF_WEEK, -1);
            endDate = cal.getTime();
        }

        Observer<List<TimeTableWithTeacherEntity>> observer = new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                List<Object> displayList = new ArrayList<>();
                if (list != null && !list.isEmpty()) {
                    Date currentDay = null;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    SimpleDateFormat headerFormat = new SimpleDateFormat("EEEE, dd MMMM", Locale.forLanguageTag("ru"));

                    for (TimeTableWithTeacherEntity entity : list) {
                        TimeTableEntity timeTable = entity.timeTableEntity;
                        Date lessonDate = timeTable.timeStart;

                        if (currentDay == null || !isSameDay(currentDay, lessonDate)) {
                            currentDay = lessonDate;
                            String headerText = headerFormat.format(currentDay);
                            headerText = headerText.substring(0, 1).toUpperCase() + headerText.substring(1);
                            displayList.add(new ScheduleItemHeader(headerText));
                        }

                        ScheduleItem item = new ScheduleItem();
                        item.setStart(timeFormat.format(timeTable.timeStart));
                        item.setEnd(timeFormat.format(timeTable.timeEnd));
                        item.setType(String.valueOf(timeTable.type));
                        item.setName(timeTable.subName);
                        item.setPlace(timeTable.cabinet + ", " + timeTable.corp);
                        item.setTeacher(entity.teacherEntity.fio);
                        displayList.add(item);
                    }
                } else {
                    displayList.add(new ScheduleItemHeader(getString(R.string.no_schedule)));
                }
                adapter.setDataList(displayList);
            }
        };

        if (mode == BasePeopleActivity.ScheduleMode.STUDENT) {
            mainViewModel.getTimeTableByDateAndGroupId(startDate, endDate, id).observe(this, observer);
        } else {
            mainViewModel.getTimeTableByDateAndTeacherId(startDate, endDate, id).observe(this, observer);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void onScheduleItemClick(ScheduleItem data) {
        // Логика обработки клика по элементу расписания
    }
}