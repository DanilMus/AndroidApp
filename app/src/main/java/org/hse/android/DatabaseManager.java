package org.hse.android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

@Database(entities = {GroupEntity.class, TeacherEntity.class, TimeTableEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {
    private static DatabaseManager instance;

    public abstract HseDao hseDao();

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager.class, "DATABASE_NAME")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                DatabaseManager localInstance = getInstance(context); // ⚠️ создаем локально
                                initData(context, localInstance.hseDao()); // ✅ безопасно
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    // Сделаем метод публичным, если будем вызывать вручную в Application
    public static void initData(Context context, HseDao dao) {
        List<GroupEntity> groups = new ArrayList<>();
        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "ПИЯЯ-18-1";
        groups.add(group);
        group = new GroupEntity();
        group.id = 2;
        group.name = "ПИЯЯ-18-2";
        groups.add(group);
        dao.insertGroup(groups);

        List<TeacherEntity> teachers = new ArrayList<>();
        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Петров Петр Петрович";
        teachers.add(teacher);
        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Петров2 Петр2 Петрович2";
        teachers.add(teacher);
        dao.insertTeacher(teachers);

        List<TimeTableEntity> timeTables = new ArrayList<>();
        TimeTableEntity timeTable = new TimeTableEntity();
        timeTable.id = 1;
        timeTable.subGroup = "ПИ1";
        timeTable.subName = "Языковая разработка";
        timeTable.cabinet = "КАБИНЕТ 1";
        timeTable.corp = "К1";
        timeTable.type = 1;
        timeTable.timeStart = dateFromString("2025-05-13 10:00");
        timeTable.timeEnd = dateFromString("2025-05-13 11:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 2;
        timeTable.subGroup = "ПИ1";
        timeTable.subName = "Языковая разработка";
        timeTable.cabinet = "КАБИНЕТ 2";
        timeTable.corp = "К1";
        timeTable.type = 0;
        timeTable.timeStart = dateFromString("2025-05-13 13:00");
        timeTable.timeEnd = dateFromString("2025-05-13 15:00");
        timeTable.groupId = 1;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 3;
        timeTable.subGroup = "БИ1";
        timeTable.subName = "Математика";
        timeTable.cabinet = "КАБИНЕТ 3";
        timeTable.corp = "К2";
        timeTable.type = 1;
        timeTable.timeStart = dateFromString("2025-05-14 10:00");
        timeTable.timeEnd = dateFromString("2025-05-14 11:30");
        timeTable.groupId = 2;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        dao.insertTimeTable(timeTables);
    }

    private static Date dateFromString(String val) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return simpleDateFormat.parse(val);
        } catch (ParseException e) {
            return null;
        }
    }
}
