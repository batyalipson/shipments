package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 26/06/2016.
 */
public class SendTokenGcmAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    Utils utils=new Utils();
    Context context;
    String token;
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateDeviceData";

    public SendTokenGcmAsyncTask(Context context){
        this.context=context;
    }
    public SendTokenGcmAsyncTask(Context context,String token){
        this.context=context;
        this.token=token;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

//        InstanceID instanceID = InstanceID.getInstance(context);

//        token = null;
//        try {
//
//            token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId),
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        ResultEntity resultEntity=null;
        String token = FirebaseInstanceId.getInstance().getToken();

        //String url= "http://truckies.shtibel.com/tr-api/?action=updateDeviceData";
        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String gcmId="&app_gcm_id="+token;
        String device="&app_device_type=android "+getDeviceDetails();
        //String uniqueDeviceNumber="&unique_device_number="+utils.getUniqueNumber(context);

        String appVersion="&app_version="+ BuildConfig.VERSION_NAME;

            resultEntity= JsonParser.requestGetOkHttp(URL + userId + gcmId + device + appVersion);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        Log.e("resultEntity gcm", resultEntity.getResult());
        super.onPostExecute(resultEntity);
    }

    private String getDeviceDetails(){
        String details="";

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;

        details+="manufacturer "+manufacturer+" model "+model+" version "+version+" versionRelease "+versionRelease;

        return details;
    }

//    private String getUniqueNumber(Context context){
//        String android_id = Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//
//        return android_id;
//
//    }
}
