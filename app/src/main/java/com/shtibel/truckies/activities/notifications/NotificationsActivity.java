package com.shtibel.truckies.activities.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shtibel.truckies.R;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.pushNotifications.MyFcmListenerService;
import com.shtibel.truckies.pushNotifications.NotificationReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Notification> notifications;
    Menu menu;
    PopupMenu.OnMenuItemClickListener onMenuItemClickListener;
    TextView nothingYetText;
    Utils utils=new Utils();
    UserDetailsChangedReceiver userDetailsChangedReceiver;
    //ActionMenuView.OnMenuItemClickListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter(MyFcmListenerService.CGM_BROADCAST));
        registerReceiver(this.updateNotificationsReceiver,
                new IntentFilter(NotificationReceiver.UPDATE_NOTIFICATIONS_TABLE));
        initNotificationsView();
        initMenuListener();
    }

    private void initNotificationsView() {

        notifications= DBHelper.getDB(this).
                getNotificationsByUserId(SharedPreferenceManager.getInstance(this).getUserId());

        nothingYetText= (TextView) findViewById(R.id.notifications_nothing_yet);
        if (notifications.size()>0)
            nothingYetText.setVisibility(View.GONE);
        else
            nothingYetText.setVisibility(View.VISIBLE);

        recyclerView= (RecyclerView) findViewById(R.id.notifications_recycler_view);
        notificationAdapter=new NotificationAdapter(notifications,this,nothingYetText);
        recyclerView.setAdapter(notificationAdapter);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationBroadcastReceiver);
        unregisterReceiver(updateNotificationsReceiver);
    }

    public void close(View view){
        finish();
    }

    BroadcastReceiver notificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifications= DBHelper.getDB(NotificationsActivity.this).
                    getNotificationsByUserId(SharedPreferenceManager.getInstance(context).getUserId());;
            notificationAdapter.refreshList(notifications);
        }
    };
    BroadcastReceiver updateNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifications= DBHelper.getDB(NotificationsActivity.this).
                    getNotificationsByUserId(SharedPreferenceManager.getInstance(context).getUserId());;
            notificationAdapter.refreshList(notifications);
        }
    };

    private void initMenuListener() {
        onMenuItemClickListener=new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.not_menu_mute:
                        // menu.getItem(0).setTitle(getString(R.string.play_sound));
                        if (item.getTitle().equals(getString(R.string.mute))){
                            SharedPreferenceManager.getInstance(NotificationsActivity.this).saveIsMute(true);
                            item.setTitle(getString(R.string.unmute));
                        }
                        else {
                            SharedPreferenceManager.getInstance(NotificationsActivity.this).saveIsMute(false);
                            item.setTitle(getString(R.string.mute));
                        }

                        return true;
                    case R.id.not_menu_clear:
                        //Toast.makeText(NotificationsActivity.this, "clear all", Toast.LENGTH_SHORT).show();
                        utils.openPopupWindow(NotificationsActivity.this,
                                false,
                                getString(R.string.confirm),
                                getString(R.string.warning_delete_all_notifications),
                                getResources().getString(R.string.ok),
                                getResources().getString(R.string.cancel),
                                null,
                                new PopupInterface(){
                                    @Override
                                    public void onOk() {
                                        super.onOk();
                                        DBHelper.getDB(NotificationsActivity.this).deleteAllNotifications
                                                (SharedPreferenceManager.getInstance(NotificationsActivity.this).getUserId());
                                        notifications=DBHelper.getDB(NotificationsActivity.this).getNotificationsByUserId
                                                (SharedPreferenceManager.getInstance(NotificationsActivity.this).getUserId());
                                        notificationAdapter.refresh(notifications);
                                    }
                                });
                        return true;
                    default:
                        return false;
                }
            }
        };
    }


    public void openPopupMenu(View view){
        final PopupMenu popup = new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notification_menu, popup.getMenu());
        if (SharedPreferenceManager.getInstance(this).isMute()) {
            menu = popup.getMenu();
            menu.getItem(0).setTitle(getString(R.string.unmute));
        }
        popup.show();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case Utils.MY_PERMISSIONS_REQUEST_DIALER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + SharedPreferenceManager.getInstance(this).getSupportPhone()));
                    startActivity(intent);
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToSupport(View view){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE,},
                    Utils.MY_PERMISSIONS_REQUEST_DIALER);

        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + SharedPreferenceManager.getInstance(this).getSupportPhone()));
            startActivity(intent);
        }
    }
    private class UserDetailsChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AlarmReceiver.APPLICATION_DETAILS_CHANGED)) {
                //TODO close application after show message
                utils.openPopupWindow(NotificationsActivity.this,
                        false,
                        getString(R.string.notice),
                        getString(R.string.user_details_changed_message),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {

                            @Override
                            public void onOk() {
                                utils.closeApplication(NotificationsActivity.this);
                            }
                        });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userDetailsChangedReceiver == null)
            userDetailsChangedReceiver = new UserDetailsChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(AlarmReceiver.APPLICATION_DETAILS_CHANGED);
        registerReceiver(userDetailsChangedReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userDetailsChangedReceiver != null)
            unregisterReceiver(userDetailsChangedReceiver);
    }

}
