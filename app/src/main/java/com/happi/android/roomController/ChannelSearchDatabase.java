package com.happi.android.roomController;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RoomChannelSearchModel.class}, version = 1, exportSchema = false)
public abstract class ChannelSearchDatabase extends RoomDatabase {

    public abstract ChannelSearchAccess channelSearchAccess();
}

