package com.shtibel.truckies.servicesAndBroadCasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shtibel.truckies.StartApplication;

/**
 * Created by Shtibel on 12/07/2016.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent localIntent = new Intent(context, LocationService.class);
        localIntent.setPackage(StartApplication.packageName);
        Log.e("AutoStart","AutoStart");
        context.startService(localIntent);
    }
}
