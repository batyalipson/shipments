package com.shtibel.truckies.generalClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.pushNotifications.BadgeUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shtibel on 19/07/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    Context context;
    String Notifications="notifications";

    private static DBHelper db=null;


    private DBHelper(Context context) {
        super(context,"AppDataBase",null,1);
        this.context=context;
    }

    public static synchronized DBHelper getDB(Context context)
    {
        if (db==null)
            db=new DBHelper(context);
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String notifications="CREATE TABLE "+Notifications+" ( ID INTEGER PRIMARY KEY, MESSAGE_ID INTEGER, TITLE TEXT," +
                " CONTENT TEXT, TYPE TEXT, IS_READ INTEGER ,IS_ON_STATUS_BAR ,DATE INTEGER , USER_ID INTEGER )";

        db.execSQL(notifications);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addNotification(Notification notification){

        SQLiteDatabase dat = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("MESSAGE_ID",notification.getMessageId());
        values.put("TITLE", notification.getTitle());
        values.put("CONTENT",notification.getContent());
        values.put("TYPE",notification.getType());
        values.put("IS_READ",notification.isRead()?1:0);
        values.put("IS_ON_STATUS_BAR",notification.isOnStatusBar()?1:0);
        values.put("DATE",notification.getDate().getTime());
        values.put("USER_ID",notification.getUserId());

        long id = dat.insert(Notifications, null, values);
        dat.close();

        BadgeUtils.setBadge(context, getNotOpenedNotifications(notification.getUserId()));

        return id;
    }
    public void addNotifications(List<Notification>notifications){
        SQLiteDatabase dat = getWritableDatabase();

        for (Notification notification:notifications) {
            ContentValues values = new ContentValues();

            values.put("MESSAGE_ID", notification.getMessageId());
            values.put("TITLE", notification.getTitle());
            values.put("CONTENT", notification.getContent());
            values.put("TYPE", notification.getType());
            values.put("IS_READ", notification.isRead() ? 1 : 0);
            values.put("IS_ON_STATUS_BAR", notification.isOnStatusBar() ? 1 : 0);
            values.put("DATE", notification.getDate().getTime());
            values.put("USER_ID", notification.getUserId());

            long id = dat.insert(Notifications, null, values);
            notification.setId(id);
        }
        dat.close();

        BadgeUtils.setBadge(context, getNotOpenedNotifications(notifications.get(0).getUserId()));
    }

    public List<Notification> getAllNotifications(){
        List<Notification> notificationsList=new ArrayList<>();

        String select="SELECT * FROM "+Notifications+" ORDER BY ID DESC";

        SQLiteDatabase dat=getReadableDatabase();
        Cursor cursor=dat.rawQuery(select, null);
        if (cursor.moveToFirst()){
            do {
               Notification notification = new Notification();

                notification.setId(cursor.getInt(0));
                notification.setMessageId(cursor.getInt(1));
                notification.setTitle(cursor.getString(2));
                notification.setContent(cursor.getString(3));
                notification.setType(cursor.getString(4));
                notification.setIsRead(cursor.getInt(5) == 1);
                notification.setIsOnStatusBar(cursor.getInt(6) == 1);
                notification.setDate(new Date(cursor.getLong(7)));
                notification.setUserId(cursor.getLong(8));

                notificationsList.add(notification);
            }while(cursor.moveToNext());

        }
        return notificationsList;

    }

    public List<Notification> getNotificationsByUserId(long userId){
        List<Notification> notificationsList=new ArrayList<>();

        String select="SELECT * FROM "+Notifications+" WHERE USER_ID = "+userId+" ORDER BY ID DESC";

        SQLiteDatabase dat=getReadableDatabase();
        Cursor cursor=dat.rawQuery(select, null);
        if (cursor.moveToFirst()){
            do {
                Notification notification = new Notification();

                notification.setId(cursor.getInt(0));
                notification.setMessageId(cursor.getInt(1));
                notification.setTitle(cursor.getString(2));
                notification.setContent(cursor.getString(3));
                notification.setType(cursor.getString(4));
                notification.setIsRead(cursor.getInt(5) == 1);
                notification.setIsOnStatusBar(cursor.getInt(6) == 1);
                notification.setDate(new Date(cursor.getLong(7)));
                notification.setUserId(cursor.getLong(8));

                notificationsList.add(notification);
            }while(cursor.moveToNext());

        }
        return notificationsList;

    }

    public void updateNotification(Notification notification) {
//        INTEGER TYPE, INTEGER VALUE1, INTEGER VALUE2
        SQLiteDatabase dat=getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put("MESSAGE_ID",notification.getMessageId());
        values.put("TITLE", notification.getTitle());
        values.put("CONTENT",notification.getContent());
        values.put("TYPE",notification.getType());
        values.put("IS_READ",notification.isRead()?1:0);
        values.put("IS_ON_STATUS_BAR",notification.isOnStatusBar()?1:0);
        values.put("DATE",notification.getDate().getTime());
        values.put("USER_ID",notification.getUserId());

        dat.update(Notifications, values, "ID " + "=" + notification.getId(), null);
        dat.close();

        BadgeUtils.setBadge(context, getNotOpenedNotifications(notification.getUserId()));
    }

    public void deleteNotification(long notificationId,long userId){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Notifications + " WHERE ID=" + notificationId);
        db.close();
        BadgeUtils.setBadge(context, getNotOpenedNotifications(userId));

    }
    public void deleteAllNotifications(long userId){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Notifications + " WHERE USER_ID=" + userId);
        db.close();
        BadgeUtils.setBadge(context, getNotOpenedNotifications(userId));

    }

    public int getNotOpenedNotifications(long userId){
        int count=0;
        String select="SELECT * FROM "+Notifications+" WHERE IS_READ=0 AND USER_ID = "+userId;

        SQLiteDatabase dat=getReadableDatabase();
        final Cursor cursor=dat.rawQuery(select, null);

        count=cursor.getCount();
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
               cursor.close();
//            }
//        },10000);
        return count;
    }

    public int getInStatusBarNotifications(long userId){
        int count=0;
        String select="SELECT * FROM "+Notifications+" WHERE IS_ON_STATUS_BAR=1 AND USER_ID = "+userId;

        SQLiteDatabase dat=getReadableDatabase();
        final Cursor cursor=dat.rawQuery(select, null);

        count=cursor.getCount();
        cursor.close();

        return count;
    }

    public void updateOnStatusBarNotifications(long userId){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + Notifications + " SET IS_ON_STATUS_BAR = " + 0 +" WHERE USER_ID = "+userId);
        db.close();
      //  UPDATE table_name SET numerazione = 2 WHERE numerazione != 2

    }

}
