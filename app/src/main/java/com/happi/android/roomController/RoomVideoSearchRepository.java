package com.happi.android.roomController;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class RoomVideoSearchRepository {

    private String DB_NAME = "video";
    private VideoSearchDatabase videoSearchDatabase;

    public RoomVideoSearchRepository(Context context) {
        videoSearchDatabase = Room.databaseBuilder(context, VideoSearchDatabase.class, DB_NAME).build();
    }

    public void insertTask(String title) {

        RoomVideoSearchModel roomVideoSearchModel = new RoomVideoSearchModel();
        roomVideoSearchModel.setSearchKeyword(title);

        insertTask(roomVideoSearchModel);
    }

    public void insertTask(final RoomVideoSearchModel roomVideoSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                videoSearchDatabase.videoSearchAccess().insertTask(roomVideoSearchModel);
                return null;
            }
        }.execute();
    }

    public void updateTask(final RoomVideoSearchModel roomVideoSearchModel) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                videoSearchDatabase.videoSearchAccess().updateTask(roomVideoSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<RoomVideoSearchModel> roomVideoSearchModel = getTask(id);
        if (roomVideoSearchModel != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    videoSearchDatabase.videoSearchAccess().deleteTask(roomVideoSearchModel.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final RoomVideoSearchModel roomVideoSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                videoSearchDatabase.videoSearchAccess().deleteTask(roomVideoSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteAllData() {

        videoSearchDatabase.videoSearchAccess().deleteAllData();
    }

    public LiveData<RoomVideoSearchModel> getTask(int id) {

        return videoSearchDatabase.videoSearchAccess().getTask(id);
    }

    public LiveData<List<RoomVideoSearchModel>> getTasks() {

        return videoSearchDatabase.videoSearchAccess().fetchAllTasks();
    }
}
