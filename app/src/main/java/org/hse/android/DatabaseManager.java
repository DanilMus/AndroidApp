package org.hse.android;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.lifecycle.LiveData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


// DatabaseManager

@Database(entities = {GroupEntity.class, TeacherEntity.class, TimeTableEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {
    private static DatabaseManager instance;
    public abstract HseDao hseDao();

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DatabaseManager.class, "DATABASE_NAME")
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        initData(context);
                                    }
                                };
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void initData(Context context) {
        List<GroupEntity> groups = new ArrayList<>();
        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "ПИЯЯ-18-1";
        groups.add(group);
        group = new GroupEntity();
        group.id = 2;
        group.name = "ПИЯЯ-18-2";
        groups.add(group);
        DatabaseManager.getInstance(context).hseDao().insertGroup(groups);

        List<TeacherEntity> teachers = new ArrayList<>();
        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Петров Петр Петрович";
        teachers.add(teacher);
        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Петров2 Петр2 Петрович2";
        teachers.add(teacher);
        DatabaseManager.getInstance(context).hseDao().insertTeacher(teachers);

        List<TimeTableEntity> timeTables = new ArrayList<>();
        TimeTableEntity timeTable = new TimeTableEntity();
        timeTable.id = 1;
        timeTable.cabinet = "КАБИНЕТ 1";
        timeTable.subGroup = "ПИ";
        timeTable.subName = "Языковая разработка";
        timeTable.corp = "К1";
        timeTable.type = 0;
        timeTable.timeStart = dateFromString("2021-02-01 10:00");
        timeTable.timeEnd = dateFromString("2021-02-01 11:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 2;
        timeTable.cabinet = "КАБИНЕТ 2";
        timeTable.subGroup = "ПИ";
        timeTable.subName = "Языковая разработка";
        timeTable.corp = "К1";
        timeTable.type = 0;
        timeTable.timeStart = dateFromString("2021-02-01 13:00");
        timeTable.timeEnd = dateFromString("2021-02-01 15:00");
        timeTable.groupId = 1;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        DatabaseManager.getInstance(context).hseDao().insertTimeTable(timeTables);
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