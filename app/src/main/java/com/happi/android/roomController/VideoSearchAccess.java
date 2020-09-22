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
public interface VideoSearchAccess {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertTask(RoomVideoSearchModel roomVideoSearchModel);

    @Query("SELECT DISTINCT searchKeyword, id FROM VideoSearch ORDER BY id DESC")
    LiveData<List<RoomVideoSearchModel>> fetchAllTasks();

    @Query("SELECT * FROM VideoSearch WHERE id =:taskId")
    LiveData<RoomVideoSearchModel> getTask(int taskId);

    @Query("DELETE FROM VideoSearch")
    void deleteAllData();

    @Update
    void updateTask(RoomVideoSearchModel roomVideoSearchModel);

    @Delete
    void deleteTask(RoomVideoSearchModel roomVideoSearchModel);
}