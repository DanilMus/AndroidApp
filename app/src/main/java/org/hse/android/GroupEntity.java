package org.hse.android;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_table")
public class GroupEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    @NonNull
    public String name = "";
}
