package org.hse.android;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.Date;

import java.util.List;

@Dao
public interface HseDao {
    @Query("SELECT * FROM group_table")
    LiveData<List<GroupEntity>> getAllGroup();

    @Insert
    void insertGroup(List<GroupEntity> data);

    @Delete
    void delete(GroupEntity data);

    @Query("SELECT * FROM teacher")
    LiveData<List<TeacherEntity>> getAllTeacher();

    @Insert
    void insertTeacher(List<TeacherEntity> data);

    @Delete
    void delete(TeacherEntity data);

    @Query("SELECT * FROM time_table")
    LiveData<List<TimeTableEntity>> getAllTimeTable();

    @Transaction
    @Query("SELECT * FROM time_table WHERE time_start BETWEEN :startDate AND :endDate AND groupId = :groupId")
    LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndGroupId(Date startDate, Date endDate, int groupId);

    @Transaction
    @Query("SELECT * FROM time_table WHERE time_start BETWEEN :startDate AND :endDate AND teacher_id = :teacherId")
    LiveData<List<TimeTableWithTeacherEntity>> getTimeTableByDateAndTeacherId(Date startDate, Date endDate, int teacherId);

    @Insert
    void insertTimeTable(List<TimeTableEntity> data);
}
