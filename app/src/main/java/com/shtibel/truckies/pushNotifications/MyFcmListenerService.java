package com.shtibel.truckies.pushNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by Shtibel on 26/06/2016.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    public static final String CGM_BROADCAST="gcm_broadcast_receiver";
    List<Notification> notifications=new ArrayList<>();
    boolean isFirstNotification=true;
    Handler handler=new Handler();

    @Override
    public void onMessageReceived(RemoteMessage message){
//        String from = message.getFrom();
//        Map data = message.getData();
//        super.onMessageReceived(from, data);
        Map data = message.getData();
        Log.d("notifications", data.toString());
        //Bundle notification=((Bundle) data.get("notification"));
      //  if (notification!=null) {
        Notification notificationDetails=new Notification();

        String title = data.get("title").toString();
        String type = data.get("type").toString();
        String body = Html.fromHtml(data.get("message").toString()).toString();
        String id = data.get("id").toString();

        notificationDetails.setMessageId(Long.parseLong(id));
        notificationDetails.setTitle(title);
        notificationDetails.setContent(body);
        notificationDetails.setType(type);
        notificationDetails.setIsRead(false);
        notificationDetails.setIsOnStatusBar(true);
        notificationDetails.setDate(new Date(System.currentTimeMillis()));
        notificationDetails.setUserId(SharedPreferenceManager.getInstance(this).getUserId());

        if (!notificationDetails.getType().equals("offer")||SharedPreferenceManager.getInstance(this).getCanSeeOffers()==1) {
            notifications.add(notificationDetails);

            if (isFirstNotification) {
                isFirstNotification = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFirstNotification = true;
//                   long notificationId= DBHelper.getDB(this).addNotification(notificationDetails);
//                   notificationDetails.setId(notificationId);
                        String idS = "";
                        DBHelper.getDB(MyFcmListenerService.this).addNotifications(notifications);
                        for (Notification notification : notifications) {
                            sendMessageNotification(notification);
                            idS += notification.getMessageId() + "/";
                        }
                        sendGCMBroadCast(idS);
                        notifications.clear();
                    }
                }, 5 * 1000);
            }
        }

    }

    private void sendMessageNotification(Notification notification) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder=null;

        if (DBHelper.getDB(this).getInStatusBarNotifications(SharedPreferenceManager.getInstance(this).getUserId())>1){
            notificationBuilder=moreOneNotifications(notification);
        }
        else {
            notificationBuilder = oneNotification(notification);
        }
        //int id = 1;// roomIntId != null ? roomIntId : atomic.incrementAndGet();
        notificationManager.notify((int)notification.getId(), notificationBuilder.build());

    }

    private  NotificationCompat.Builder oneNotification(Notification notification){
     // Intent intent = new Intent();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = new long[]{200, 200, 200};
        if (SharedPreferenceManager.getInstance(this).isMute())
        {
            defaultSoundUri=null;
            vibrate=null;
        }

        Intent contentIntent = new Intent(this, NotificationReceiver.class);
        contentIntent.setAction(NotificationReceiver.NOTIFICATION_OPEN);
        contentIntent.putExtra("notification", notification);
        PendingIntent pendingIntentContent = PendingIntent.getBroadcast(this, 0,
                contentIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        Intent deleteIntent=new Intent(this, NotificationReceiver.class);
        deleteIntent.setAction(NotificationReceiver.NOTIFICATION_DELETE);
        deleteIntent.putExtra("notification", notification);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, 0,
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(null)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntentContent)
                .setDeleteIntent(pendingIntentDelete);
        return notificationBuilder;
    }

    private  NotificationCompat.Builder moreOneNotifications(Notification notification){
        // Intent intent = new Intent();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancelAll();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = new long[]{200, 200, 200};
        if (SharedPreferenceManager.getInstance(this).isMute())
        {
            defaultSoundUri=null;
            vibrate=null;
        }

        Intent contentIntent = new Intent(this, NotificationReceiver.class);
        contentIntent.setAction(NotificationReceiver.GENERAL_NOTIFICATION_OPEN);
        contentIntent.putExtra("notification", notification);
        PendingIntent pendingIntentContent = PendingIntent.getBroadcast(this, 0,
                contentIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        Intent deleteIntent=new Intent(this, NotificationReceiver.class);
        deleteIntent.setAction(NotificationReceiver.GENERAL_NOTIFICATION_DELETE);
        deleteIntent.putExtra("notification", notification);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, 0,
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        int numberOfNotReadNotifications=DBHelper.getDB(this)
                .getInStatusBarNotifications(SharedPreferenceManager.getInstance(this).getUserId());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(null)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(numberOfNotReadNotifications+" "+getString(R.string.notification_content))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntentContent)
                .setDeleteIntent(pendingIntentDelete);
        return notificationBuilder;
    }

    private void sendGCMBroadCast(String idS) {
        Intent intent = new Intent();
        intent.putExtra("idS",idS);
        intent.setAction(CGM_BROADCAST);
        this.sendBroadcast(intent);

    }

}
