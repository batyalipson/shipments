package com.shtibel.truckies.servicesAndBroadCasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.alertDialog.AlertDialogActivity;

/**
 * Created by Shtibel on 12/07/2016.
 */
public class GPSCheck extends BroadcastReceiver {
    private static boolean firstConnectStop = true;
    private static boolean firstConnectOpen=true;
//    public static final String CHANGE_GPS_STATUS="CHANGE_GPS_STATUS";

    @Override
    public void onReceive(final Context context, Intent intent) {

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            firstConnectStop=true;
            if (firstConnectOpen){
                Intent locationIntent = new Intent(context, LocationService.class);
                locationIntent.setPackage(StartApplication.packageName);
                context.startService(locationIntent);
                firstConnectOpen=false;
            }
//            Toast.makeText(context, "GPS on now", Toast.LENGTH_LONG).show();
        }
        else
        {
            firstConnectOpen=true;
            Log.e("GPSCheck",intent.getAction());
            if (firstConnectStop) {
                Intent i = new Intent(context, AlertDialogActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                firstConnectStop=false;
            }

//            Toast.makeText(context, "Please switch on the GPS", Toast.LENGTH_LONG).show();
        }
    }


//    public boolean isAppForground(Context mContext) {
//
//        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
//                return false;
//            }
//        }
//
//        return true;
//    }

}
