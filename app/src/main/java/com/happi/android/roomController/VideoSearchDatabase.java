package com.happi.android.roomController;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RoomVideoSearchModel.class}, version = 1, exportSchema = false)
public abstract class VideoSearchDatabase extends RoomDatabase {

    public abstract VideoSearchAccess videoSearchAccess();
}
