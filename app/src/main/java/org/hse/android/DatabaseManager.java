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

// Аннотация Room, указывающая, какие сущности участвуют в базе, и версия схемы
@Database(entities = {GroupEntity.class, TeacherEntity.class, TimeTableEntity.class}, version = 1)
@TypeConverters({Converters.class}) // Используем TypeConverter'ы для нестандартных типов (например, Date)
public abstract class DatabaseManager extends RoomDatabase {
    private static DatabaseManager instance; // Singleton — одна база на всё приложение

    // Абстрактный метод, который будет реализован Room и даст доступ к DAO
    public abstract HseDao hseDao();

    // Метод для получения единственного экземпляра базы данных
    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            // Строим базу с использованием Room API
            instance = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager.class, "DATABASE_NAME")
                    .addCallback(new Callback() { // Коллбэк вызывается при создании базы
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Инициализация происходит в отдельном потоке
                            Executors.newSingleThreadExecutor().execute(() -> {
                                DatabaseManager localInstance = getInstance(context); // ⚠️ создаем локально (обязательно через getInstance, чтобы не было null)
                                initData(context, localInstance.hseDao()); // ✅ безопасно инициализируем данные
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    // Метод для первоначального заполнения базы (можно вызывать вручную при необходимости)
    public static void initData(Context context, HseDao dao) {
        // Создаем группы и добавляем их
        List<GroupEntity> groups = new ArrayList<>();
        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "ПИЯЯ-18-1";
        groups.add(group);

        group = new GroupEntity();
        group.id = 2;
        group.name = "ПИЯЯ-18-2";
        groups.add(group);

        dao.insertGroup(groups); // Вставляем список групп в базу

        // Создаем преподавателей и добавляем их
        List<TeacherEntity> teachers = new ArrayList<>();
        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Петров Петр Петрович";
        teachers.add(teacher);

        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Петров2 Петр2 Петрович2";
        teachers.add(teacher);

        dao.insertTeacher(teachers); // Вставляем список преподавателей

        // Создаем расписание занятий и добавляем
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

        dao.insertTimeTable(timeTables); // Вставляем расписание
    }

    // Преобразует строку в объект Date. Используется для установки времени в расписании
    private static Date dateFromString(String val) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return simpleDateFormat.parse(val); // Пробуем распарсить строку
        } catch (ParseException e) {
            return null; // Если не получилось — возвращаем null
        }
    }
}
