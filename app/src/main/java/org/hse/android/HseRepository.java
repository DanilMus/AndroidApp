package org.hse.android;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.Date;
import java.util.List;

public class HseRepository {
    private DatabaseManager databaseManager;
    private HseDao dao;

    public HseRepository(Context context) {
        databaseManager = DatabaseManager.getInstance(context);
        dao = databaseManager.hseDao();
    }

    public LiveData<List<GroupEntity>> getGroups() {
        return dao.getAllGroup();
    }

    public LiveData<List<TeacherEntity>> getTeachers() {
        return dao.getAllTeacher();
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndGroupId(Date startDate, Date endDate, int groupId) {
        return dao.getTimeTableByDateAndGroupId(startDate, endDate, groupId);
    }

    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndTeacherId(Date startDate, Date endDate, int teacherId) {
        return dao.getTimeTableByDateAndTeacherId(startDate, endDate, teacherId);
    }
}