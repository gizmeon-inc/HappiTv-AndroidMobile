package com.happi.android.roomController;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RoomShowSearchModel.class}, version = 1, exportSchema = false)
public abstract class ShowSearchDatabase extends RoomDatabase {

    public abstract ShowSearchAccess showSearchAccess();
}