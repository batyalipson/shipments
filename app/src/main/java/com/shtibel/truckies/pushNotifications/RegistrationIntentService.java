package com.shtibel.truckies.pushNotifications;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 26/06/2016.
 */
public class RegistrationIntentService  extends IntentService {

//    public RegistrationIntentService(String name) {
//        super(name);
//    }

    public RegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        InstanceID instanceID = InstanceID.getInstance(this);
//
//        String token = null;
//        try {
//            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


        String token = FirebaseInstanceId.getInstance().getToken();

        String url= StartApplication.START_URL+"/tr-api/?action=updateDeviceData";
        String userId="&user_id="+ SharedPreferenceManager.getInstance(this).getUserId();
        String gcmId="&app_gcm_id="+token;
        String device="&app_device_type=android "+getDeviceDetails();
        //String uniqueDeviceNumber="&unique_device_number="+getUniqueNumber();

        String appVersion="&app_version="+ BuildConfig.VERSION_NAME;;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(url + userId + gcmId + device + appVersion);
        Log.e("resultEntity gcm",resultEntity.getResult());

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    private String getUniqueNumber(){
         String android_id = Settings.Secure.getString(this.getContentResolver(),
                 Settings.Secure.ANDROID_ID);

        return android_id;

    }

}
