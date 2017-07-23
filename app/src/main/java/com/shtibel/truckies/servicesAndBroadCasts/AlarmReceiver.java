package com.shtibel.truckies.servicesAndBroadCasts;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.login.LoginActivity;
import com.shtibel.truckies.asyncTasks.CheckChangesAsyncTask;
import com.shtibel.truckies.asyncTasks.LogOutAsyncTask;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shtibel on 02/01/2017.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String APPLICATION_DETAILS_CHANGED="APPLICATION_DETAILS_CHANGED";

    @Override
    public void onReceive(final Context context, Intent intent) {
       // Log.d("AlarmReceiver","AlarmReceiver");

        if (SharedPreferenceManager.getInstance(context).getUserId()!=-1) {
            CheckChangesAsyncTask checkChangesAsyncTask = new CheckChangesAsyncTask(context, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        boolean isDetailsChanged = jsonObject.getBoolean("err");
                        if (isDetailsChanged) {

                            SharedPreferenceManager.getInstance(context).saveIsLogin(false);
                            LogOutAsyncTask logOutAsyncTask = new LogOutAsyncTask(context, new IResultsInterface() {
                                @Override
                                public void onCompleteWithResult(ResultEntity result) {
                                    DBHelper.getDB(context).deleteAllNotifications(SharedPreferenceManager.getInstance(context).getUserId());
                                    SharedPreferenceManager.getInstance(context).clearUserData();
                                    Intent locationIntent = new Intent(context, LocationService.class);
                                    locationIntent.setPackage(StartApplication.packageName);
                                    context.stopService(locationIntent);

                                    Intent intent = new Intent();
                                    intent.setAction(APPLICATION_DETAILS_CHANGED);
                                    context.sendBroadcast(intent);

                                    //TODO send notification
                                    sendNotification(context);
                                }

                                @Override
                                public void onErrorWithResult(ResultEntity resultEntity) {
                                    SharedPreferenceManager.getInstance(context).saveIsLogin(true);
                                }
                            });
                            logOutAsyncTask.execute();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            checkChangesAsyncTask.execute();
        }
        else {
            Intent i = new Intent();
            i.setAction(APPLICATION_DETAILS_CHANGED);
            context.sendBroadcast(i);
        }

    }

    private void sendNotification(Context context) {
            // Intent intent = new Intent();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = new long[]{200, 200, 200};
        if (SharedPreferenceManager.getInstance(context).isMute())
        {
            defaultSoundUri=null;
            vibrate=null;
        }

        Intent contentIntent = new Intent(context, LoginActivity.class);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntentContent = PendingIntent.getActivity(context, 0,
                contentIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(null)
                .setContentTitle(context.getString(R.string.user_details_changed_title))
                .setContentText(context.getString(R.string.user_details_changed_message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntentContent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

}

//                        final Intent intent = new Intent();
//                        intent.setClass(context, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        SharedPreferenceManager.getInstance(context).saveIsLogin(false);
//                        LogOutAsyncTask logOutAsyncTask=new LogOutAsyncTask(context, new IResultsInterface() {
//                            @Override
//                            public void onCompleteWithResult(ResultEntity result) {
//                                DBHelper.getDB(context).deleteAllNotifications(SharedPreferenceManager.getInstance(context).getUserId());
//                                SharedPreferenceManager.getInstance(context).clearUserData();
//                                Intent locationIntent = new Intent(context, LocationService.class);
//                                locationIntent.setPackage("com.shtibel.truckies");
//                                context.stopService(locationIntent);
//                                context.startActivity(intent);
//                                if (context instanceof Activity)
//                                    ((Activity)context).finish();
//                            }
//
//                            @Override
//                            public void onErrorWithResult(ResultEntity resultEntity) {
//                                SharedPreferenceManager.getInstance(context).saveIsLogin(true);
//                            }
//                        });
//                        logOutAsyncTask.execute();
