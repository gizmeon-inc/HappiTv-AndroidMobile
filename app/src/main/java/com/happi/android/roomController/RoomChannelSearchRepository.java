package com.happi.android.roomController;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class RoomChannelSearchRepository {

    private String DB_NAME = "channel";
    private ChannelSearchDatabase channelSearchDatabase;

    public RoomChannelSearchRepository(Context context) {
        channelSearchDatabase = Room.databaseBuilder(context, ChannelSearchDatabase.class, DB_NAME).build();
    }

    public void insertTask(String title) {

        RoomChannelSearchModel roomChannelSearchModel = new RoomChannelSearchModel();
        roomChannelSearchModel.setSearchKeyword(title);

        insertTask(roomChannelSearchModel);
    }

    public void insertTask(final RoomChannelSearchModel roomChannelSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                channelSearchDatabase.channelSearchAccess().insertTask(roomChannelSearchModel);
                return null;
            }
        }.execute();
    }

    public void updateTask(final RoomChannelSearchModel roomChannelSearchModel) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                channelSearchDatabase.channelSearchAccess().updateTask(roomChannelSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<RoomChannelSearchModel> roomChannelSearchModel = getTask(id);
        if (roomChannelSearchModel != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    channelSearchDatabase.channelSearchAccess().deleteTask(roomChannelSearchModel.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final RoomChannelSearchModel roomChannelSearchModel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                channelSearchDatabase.channelSearchAccess().deleteTask(roomChannelSearchModel);
                return null;
            }
        }.execute();
    }

    public void deleteAllData() {

        channelSearchDatabase.channelSearchAccess().deleteAllData();
    }

    public LiveData<RoomChannelSearchModel> getTask(int id) {

        return channelSearchDatabase.channelSearchAccess().getTask(id);
    }

    public LiveData<List<RoomChannelSearchModel>> getTasks() {

        return channelSearchDatabase.channelSearchAccess().fetchAllTasks();
    }
}
