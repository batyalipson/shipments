package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 23/06/2016.
 */
public class SendCurrentLocationAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=locationMove";
    /*/?action=locationMove&user_id=12&lat=112312&lng=asdasd&driver_application_status_id=2
    Available = 2
    Uavailable = 4*/

    LatLng currentLatLng;
    int statusId;
    Context context;
    IResultsInterface iResultsInterface;
    long userId;
    //String deviceId;
    //String deviceType;

    public SendCurrentLocationAsyncTask(LatLng currentLatLng, int statusId, Context context, IResultsInterface iResultsInterface){
        this.currentLatLng=currentLatLng;
        this.statusId=statusId;
        this.context=context;
        this.iResultsInterface=
                iResultsInterface;
    }
    @Override
    protected ResultEntity doInBackground(Void... voids) {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        userId= SharedPreferenceManager.getInstance(context).getUserId();
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL +
                "&user_id=" + userId +
                "&lat=" + currentLatLng.latitude +
                "&lng=" + currentLatLng.longitude +
                "&driver_application_status_id=" + statusId +
                "&app_device_id=" + androidId +
                "&app_device_type=android");
      /*  Log.e("resultEntity",URL+
                "&user_id="+userId+
                "&lat="+currentLatLng.latitude+
                "&lng="+currentLatLng.longitude+
                "&driver_application_status_id="+statusId+
                "&app_device_id="+androidId+
                "&app_device_type=android");*/

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        iResultsInterface.onCompleteWithResult(resultEntity);

        Log.e("send location success", resultEntity.getResult());
    }
}
