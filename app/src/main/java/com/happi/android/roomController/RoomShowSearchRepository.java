package com.happi.android.roomController;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class RoomShowSearchRepository {

    private String DB_NAME = "show";
    private ShowSearchDatabase showSearchDatabase;

    public RoomShowSearchRepository(Context context) {
        showSearchDatabase = Room.databaseBuilder(context, ShowSearchDatabase.class, DB_NAME).build();
    }

    public void insertTask(String title) {

        RoomShowSearchModel roomShowSearchModel = new RoomShowSearchModel();
        roomShowSearchModel.setSearchKeyword(title);

        insertTask(roomShowSearchModel);
    }

    public void insertTask(final RoomShowSearchModel roomShowSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                showSearchDatabase.showSearchAccess().insertTask(roomShowSearchModel);
                return null;
            }
        }.execute();
    }

    public void updateTask(final RoomShowSearchModel roomShowSearchModel) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                showSearchDatabase.showSearchAccess().updateTask(roomShowSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<RoomShowSearchModel> roomShowSearchModel = getTask(id);
        if (roomShowSearchModel != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    showSearchDatabase.showSearchAccess().deleteTask(roomShowSearchModel.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final RoomShowSearchModel roomShowSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                showSearchDatabase.showSearchAccess().deleteTask(roomShowSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteAllData() {

        showSearchDatabase.showSearchAccess().deleteAllData();
    }

    public LiveData<RoomShowSearchModel> getTask(int id) {

        return showSearchDatabase.showSearchAccess().getTask(id);
    }

    public LiveData<List<RoomShowSearchModel>> getTasks() {

        return showSearchDatabase.showSearchAccess().fetchAllTasks();
    }
}
