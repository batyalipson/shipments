package com.shtibel.truckies.activities.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.account.AccountActivity;
import com.shtibel.truckies.activities.dashboard.drawer.DrawerAdapter;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabJobs;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabsAdapter;
import com.shtibel.truckies.activities.login.LoginActivity;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.activities.notifications.NotificationsActivity;
import com.shtibel.truckies.asyncTasks.GetRatingAsyncTask;
import com.shtibel.truckies.asyncTasks.LogOutAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateAvailableStatusAsyncTask;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.OldRoundedBitmapDisplayer;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.pushNotifications.MyFcmListenerService;
import com.shtibel.truckies.pushNotifications.NotificationReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

//import com.crashlytics.android.Crashlytics;

public class MainActivity extends AppCompatActivity {

    public static boolean IS_ACTIVE=false;
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    DashboardTabsAdapter dashboardTabsAdapter;
    ViewPager viewPager;
    SwitchButton availableSwitch;
    int availableStatus;
    TextView numberOfNewNotifications;
    ImageView userImage;
    TextView userFullName;
    RatingBar userRating;
    TextView availableTxt;
    View circleAvailable;

    String numberOfVoters="";
    double avgRating=0;
    DrawerAdapter recyclerDrawerAdapter;

    Utils utils=new Utils();
    TextView numberOfOffers;

    Intent locationIntent;
    ProgressBar progressBar;
    UserDetailsChangedReceiver userDetailsChangedReceiver;

    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this,new Crashlytics());
        setContentView(R.layout.activity_main);

        logUser();
        IS_ACTIVE=true;
        String action=getIntent().getStringExtra("action");

        if (action!=null&&!action.equals("")) {
            Notification notification= (Notification) getIntent().getSerializableExtra("notification");
            if (action.equals(NotificationReceiver.GENERAL_NOTIFICATION_OPEN)||notification.getType().equals("message"))
                 goToNotificationsIntent();
        }
        startLocationService();
        startAlarmManager();
        initViewPager();
        initAvailableSwitch();
        initNumberOfNotificationsFirstTime();
        initDrawer();
        initVersion();

        registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter(MyFcmListenerService.CGM_BROADCAST));
        registerReceiver(this.updateNotificationsReceiver,
                new IntentFilter(NotificationReceiver.UPDATE_NOTIFICATIONS_TABLE));

        progressBar= (ProgressBar) findViewById(R.id.main_logout_progress);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
//        Crashlytics.setUserIdentifier(SharedPreferenceManager.getInstance(this).getUserId() + "");
//        Crashlytics.setUserEmail(SharedPreferenceManager.getInstance(this).getUserEmail());
//        Crashlytics.setUserName(SharedPreferenceManager.getInstance(this).getUserName());
    }

    private void goToNotificationsIntent() {
        Intent intent=new Intent();
        intent.setClass(this, NotificationsActivity.class);
        startActivity(intent);
    }

    private void initDrawer() {

        drawerLayout= (DrawerLayout) findViewById(R.id.main_drawer);
        RecyclerView recyclerViewDrawer = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerViewDrawer.setHasFixedSize(true);
        recyclerDrawerAdapter = new DrawerAdapter(this,drawerLayout);
        recyclerViewDrawer.setAdapter(recyclerDrawerAdapter);
        LinearLayoutManager drawerLayoutManager = new LinearLayoutManager(this);
        recyclerViewDrawer.setLayoutManager(drawerLayoutManager);

        userImage= (ImageView) findViewById(R.id.main_user_image);
        initUserImage();

        userFullName= (TextView) findViewById(R.id.main_user_full_name);
        userFullName.setText(SharedPreferenceManager.getInstance(this).getUserName());

        userRating= (RatingBar) findViewById(R.id.main_rating_bar);
//        LayerDrawable stars = (LayerDrawable) userRating.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.rate_orange), PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        getRating();

        availableTxt= (TextView) findViewById(R.id.main_available_text);
        circleAvailable=findViewById(R.id.main_available_circle);
        if (availableStatus == SharedPreferenceManager.AVAILABLE_STATUS_AVAILABLE){
            utils.drawCircle(circleAvailable.getWidth(),circleAvailable.getHeight(),"#39B54A",circleAvailable);
            availableTxt.setText(getString(R.string.available));
        }
        else {
            utils.drawCircle(circleAvailable.getWidth(),circleAvailable.getHeight(),"#FD553A",circleAvailable);
            availableTxt.setText(getString(R.string.unavailable));
        }
        
    }

    private void initVersion() {

        version= (TextView) findViewById(R.id.main_version);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        version.setText(getString(R.string.app_version_code) + " " + versionCode
                + "  " +
                getString(R.string.app_version_name) + " " + versionName);
    }

    private void initUserImage(){

        ImageLoader imageLoader=ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .displayer(new OldRoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.default_driver_image)
                .showImageOnLoading(null)
                .showImageOnFail(R.drawable.default_driver_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imageLoader.displayImage(StartApplication.START_URL + SharedPreferenceManager.getInstance(this).getUserImageUrl()
                , userImage, defaultOptions);
    }

    public void getRating() {
        GetRatingAsyncTask getRatingAsyncTask=new GetRatingAsyncTask(this, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                try {
                    JSONObject jsonObject=new JSONObject(result.getResult());
                    numberOfVoters=jsonObject.getString("number_of_voters");
                    avgRating=jsonObject.getDouble("avg_rating");
//                    ratingUserTxt.setText(numberOfVoters+" "+getString(R.string.rating));
                    userRating.setRating((float)avgRating);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        getRatingAsyncTask.execute();
    }

    private void initViewPager() {

        dashboardTabsAdapter=new DashboardTabsAdapter(getSupportFragmentManager(),this);
        viewPager= (ViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(dashboardTabsAdapter);

        tabLayout= (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setCustomView(sampleTabView(getString(R.string.my_jobs), Color.parseColor("#378EB9")));
        if (SharedPreferenceManager.getInstance(this).getCanSeeOffers()==1)
            tabLayout.getTabAt(1).setCustomView(sampleTabView(getString(R.string.my_offers), Color.parseColor("#656565")));
        else {
            tabLayout.setSelectedTabIndicatorHeight(0);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ((TextView) tab.getCustomView().findViewById(R.id.d_tab_content))
                        .setTextColor(Color.parseColor("#378EB9"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                ((TextView) tab.getCustomView().findViewById(R.id.d_tab_content))
                        .setTextColor(Color.parseColor("#656565"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private View sampleTabView(String text,int color){

        View v= LayoutInflater.from(this).inflate(R.layout.dashboard_tab, null);
        TextView textView= (TextView) v.findViewById(R.id.d_tab_content);
        textView.setText(text);
        textView.setTextColor(color);

        if (text.equals(getString(R.string.my_offers))) {
            numberOfOffers = (TextView) v.findViewById(R.id.d_tab_offers);
            //numberOfOffers.setVisibility(View.VISIBLE);
        }
        return v;

    }

    private void initAvailableSwitch() {

//        availableStatus= SharedPreferenceManager.getInstance(this).getAvailableStatus();
        availableSwitch= (SwitchButton) findViewById(R.id.main_available_switch);
        if (SharedPreferenceManager.getInstance(this).getAvailableStatus()==SharedPreferenceManager.AVAILABLE_STATUS_AVAILABLE)
            availableSwitch.setChecked(true);
        else
            availableSwitch.setChecked(false);
        utils.switchColor(availableSwitch);

        setAvailableStatus();

        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (SharedPreferenceManager.getInstance(MainActivity.this).getLastVersion().equals(BuildConfig.VERSION_NAME)
                        ||SharedPreferenceManager.getInstance(MainActivity.this).getLastTestingVersion().equals(BuildConfig.VERSION_NAME)) {
                    if (isChecked) {
                        if (utils.isWifiOpen(MainActivity.this)) {
                            availableStatus = SharedPreferenceManager.AVAILABLE_STATUS_AVAILABLE;
                            SharedPreferenceManager.getInstance(MainActivity.this).saveAvailableStatus(availableStatus);
                            utils.drawCircle(circleAvailable.getWidth(), circleAvailable.getHeight(), "#39B54A", circleAvailable);
                            availableTxt.setText(getString(R.string.available));
                        } else {
                            availableSwitch.setChecked(false);
                            utils.locationClosePopup(MainActivity.this);
                        }
                    } else {
                        availableStatus = SharedPreferenceManager.AVAILABLE_STATUS_UNAVAILABLE;
                        SharedPreferenceManager.getInstance(MainActivity.this).saveAvailableStatus(availableStatus);
                        utils.drawCircle(circleAvailable.getWidth(), circleAvailable.getHeight(), "#FD553A", circleAvailable);
                        availableTxt.setText(getString(R.string.unavailable));
                    }
                    UpdateAvailableStatusAsyncTask updateAvailableStatusAsyncTask = new UpdateAvailableStatusAsyncTask(MainActivity.this,
                            new IResultsInterface() {
                                @Override
                                public void onCompleteWithResult(ResultEntity result) {
                                }

                                @Override
                                public void onErrorWithResult(ResultEntity resultEntity) {

                                    utils.showServerError(MainActivity.this, resultEntity.getResult());
                                }
                            });
                    updateAvailableStatusAsyncTask.execute();
                }
                else {
                    utils.showVersionError(MainActivity.this);
                    availableSwitch.setChecked(!isChecked);
                }
            }
        });


    }

    private void initNumberOfNotificationsFirstTime() {
        numberOfNewNotifications= (TextView) findViewById(R.id.main_notifications);
        int numberOfNotOpened=DBHelper.getDB(this)
                .getNotOpenedNotifications(SharedPreferenceManager.getInstance(this).getUserId());
        if (numberOfNotOpened>0)
            numberOfNewNotifications.setText(numberOfNotOpened+"");
        else
            numberOfNewNotifications.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAvailableStatus();
        initNumberOfNotifications();
        initUserImage();

    }

    private void setAvailableStatus(){
        if (!utils.isWifiOpen(this)){
            SharedPreferenceManager.getInstance(this).saveAvailableStatus(SharedPreferenceManager.AVAILABLE_STATUS_UNAVAILABLE);
        }
        availableStatus= SharedPreferenceManager.getInstance(this).getAvailableStatus();
        if (availableStatus==SharedPreferenceManager.AVAILABLE_STATUS_AVAILABLE) {
            availableSwitch.setChecked(true);
        }
        else {
            availableSwitch.setChecked(false);
        }
    }

    private void startLocationService() {
            locationIntent = new Intent(this, LocationService.class);
            locationIntent.setPackage(StartApplication.packageName);
                    startService(locationIntent);
    }

    private void startAlarmManager() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                //1000*60*10 ten minutes, 1000*60*10 two minutes
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 *10,
                        pendingIntent);
            }
        }, 1000 * 60);

    }

    private void stopAlarmManager(){
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void openMenu(View view){
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    protected void onDestroy() {
        IS_ACTIVE=false;
        unregisterReceiver(notificationBroadcastReceiver);
        unregisterReceiver(updateNotificationsReceiver);

        stopAlarmManager();

        super.onDestroy();
    }

    public String getNumberOfVoters() {
        return numberOfVoters;
    }

    public double getAvgRating() {
        return avgRating;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK&&requestCode == DrawerAdapter.ACCOUNT_RESULT){
            initUserImage();
        }
//        if (resultCode == Activity.RESULT_OK&&requestCode== DashboardTabOffers.ACCEPT_OFFER_RESULT){
//            isFromAcceptOffer=true;
//            offerId=data.getLongExtra("offer_id",-1);
//        }
    }
    BroadcastReceiver notificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         initNumberOfNotifications();
        }
    };
    BroadcastReceiver updateNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          initNumberOfNotifications();
        }
    };
    public void signOut(View view){

        utils.openPopupWindow(this,
                true,
                getString(R.string.notice),
                getString(R.string.log_out_warning_content),
                getString(R.string.ok),
                getString(R.string.cancel),
                null,
                new PopupInterface() {
                    @Override
                    public void onOk() {
                        progressBar.setVisibility(View.VISIBLE);
                        final Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        SharedPreferenceManager.getInstance(MainActivity.this).saveIsLogin(false);
                        LogOutAsyncTask logOutAsyncTask=new LogOutAsyncTask(MainActivity.this, new IResultsInterface() {
                            @Override
                            public void onCompleteWithResult(ResultEntity result) {
                                progressBar.setVisibility(View.GONE);
                                DBHelper.getDB(MainActivity.this).deleteAllNotifications(SharedPreferenceManager.getInstance(MainActivity.this).getUserId());
                                SharedPreferenceManager.getInstance(MainActivity.this).clearUserData();
                                stopService(locationIntent);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onErrorWithResult(ResultEntity resultEntity) {
                                progressBar.setVisibility(View.GONE);
                                SharedPreferenceManager.getInstance(MainActivity.this).saveIsLogin(true);
                                utils.showServerError(MainActivity.this,resultEntity.getResult());
                            }
                        });
                        logOutAsyncTask.execute();

                    }

                    @Override
                    public void onCancel() {

                    }
                });




    }
    private void initNumberOfNotifications(){
        int numberOfNotOpened=DBHelper.getDB(MainActivity.this)
                .getNotOpenedNotifications(SharedPreferenceManager.getInstance(MainActivity.this).getUserId());
        if (numberOfNotOpened>0) {
            numberOfNewNotifications.setText(numberOfNotOpened + "");
            numberOfNewNotifications.setVisibility(View.VISIBLE);

        }
        else
            numberOfNewNotifications.setVisibility(View.GONE);
        recyclerDrawerAdapter.notifyDataSetChanged();
    }

    public TextView getNumberOfOffers() {
        return numberOfOffers;
    }
    public void goToAccounts(View view){
        drawerLayout.closeDrawer(Gravity.LEFT);
        Intent intent=new Intent();
        intent.setClass(this,AccountActivity.class);
        intent.putExtra("numberOfVoters", numberOfVoters);
        intent.putExtra("avgRating",avgRating);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (dashboardTabsAdapter.getCount()>0) {
            if (dashboardTabsAdapter.getCurrentFragment() instanceof DashboardTabJobs ) {
                DashboardTabJobs jobFragment = (DashboardTabJobs) dashboardTabsAdapter.getCurrentFragment();
                if (jobFragment.getSearchAdvanceHeader().getVisibility() == View.VISIBLE)
                    jobFragment.getSearchAdvanceHeader().setVisibility(View.GONE);
//                else if (jobFragment.getSearchHeader().getVisibility() == View.VISIBLE)
//                    jobFragment.getSearchHeader().setVisibility(View.GONE);
                else
                    finish();
            }
            else
                finish();

        }
        else
            super.onBackPressed();
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
                utils.openPopupWindow(MainActivity.this,
                                        false,
                                        getString(R.string.notice),
                                        getString(R.string.user_details_changed_message),
                                        getString(R.string.ok),
                                        "",
                                        null,
                                        new PopupInterface() {

                                            @Override
                                            public void onOk() {
                                                utils.closeApplication(MainActivity.this);
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
