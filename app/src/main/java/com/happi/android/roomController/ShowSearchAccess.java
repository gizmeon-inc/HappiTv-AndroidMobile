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
public interface ShowSearchAccess {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertTask(RoomShowSearchModel roomShowSearchModel);

    @Query("SELECT DISTINCT searchKeyword, id FROM ShowSearch ORDER BY id DESC")
    LiveData<List<RoomShowSearchModel>> fetchAllTasks();

    @Query("SELECT * FROM ShowSearch WHERE id =:taskId")
    LiveData<RoomShowSearchModel> getTask(int taskId);

    @Query("DELETE FROM ShowSearch")
    void deleteAllData();

    @Update
    void updateTask(RoomShowSearchModel roomShowSearchModel);

    @Delete
    void deleteTask(RoomShowSearchModel roomShowSearchModel);
}
