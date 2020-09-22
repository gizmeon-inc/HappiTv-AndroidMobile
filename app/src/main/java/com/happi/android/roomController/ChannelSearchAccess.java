package com.happi.android.roomController;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChannelSearchAccess {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertTask(RoomChannelSearchModel roomChannelSearchModel);

    @Query("SELECT DISTINCT searchKeyword, id FROM ChannelSearch ORDER BY id DESC")
    LiveData<List<RoomChannelSearchModel>> fetchAllTasks();

    @Query("SELECT * FROM ChannelSearch WHERE id =:taskId")
    LiveData<RoomChannelSearchModel> getTask(int taskId);

    @Query("DELETE FROM ChannelSearch")
    void deleteAllData();

    @Update
    void updateTask(RoomChannelSearchModel roomChannelSearchModel);

    @Delete
    void deleteTask(RoomChannelSearchModel roomChannelSearchModel);
}
