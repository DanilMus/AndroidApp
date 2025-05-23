package org.hse.android;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "time_table",
        foreignKeys = {@ForeignKey(entity = GroupEntity.class, parentColumns = "id", childColumns = "groupId"),
                @ForeignKey(entity = TeacherEntity.class, parentColumns = "id", childColumns = "teacher_id", onDelete = ForeignKey.CASCADE)})
public class TimeTableEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "sub_group")
    @NonNull
    public String subGroup = "";

    @ColumnInfo(name = "sub_name")
    @NonNull
    public String subName = "";

    @ColumnInfo(name = "cabinet")
    @NonNull
    public String cabinet = "";

    @ColumnInfo(name = "corp")
    @NonNull
    public String corp = "";

    @ColumnInfo(name = "type")
    public int type = 0;

    @ColumnInfo(name = "time_start")
    public Date timeStart;

    @ColumnInfo(name = "time_end")
    public Date timeEnd;

    @ColumnInfo(name = "groupId", index = true)
    public int groupId;

    @ColumnInfo(name = "teacher_id", index = true)
    public int teacherId;
}
