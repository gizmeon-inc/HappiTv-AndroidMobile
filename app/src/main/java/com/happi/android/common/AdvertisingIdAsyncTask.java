package com.happi.android.common;

import android.os.AsyncTask;
import android.util.Log;

public class AdvertisingIdAsyncTask extends AsyncTask<Void,Void,Void> {


    @Override
    protected Void doInBackground(Void... voids) {

        AdvertisingIdClient.AdInfo adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo
                    (HappiApplication.getCurrentContext());
            String advertisingId_fromThread = adInfo.getId();
            SharedPreferenceUtility.setAdvertisingId(advertisingId_fromThread);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Advertising_id", "exception : " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
