package com.shtibel.truckies.servicesAndBroadCasts;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.asyncTasks.ArrivedPopupAsyncTask;
import com.shtibel.truckies.asyncTasks.SendCurrentLocationAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateJobStatus;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shtibel on 29/06/2016.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String CHANGE_LOCATION_BROADCAST="CHANGE_LOCATION_BROADCAST";
    public static final String CHANGE_STATUS_PICKUP_ARRIVED="CHANGE_STATUS_PICKUP_ARRIVED";

    private static final long INTERVAL = 0;//1000;
    private static final long FASTEST_INTERVAL = 1000;//TODO to check if need it
    private static int INTERVAL_IN_METERS=0;//50;
    private static final float METERS_PER_SECOND_TO_KPH = (float) 3.6;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    Activity context;

    Location mLastLocation;

    List<Jobs> jobs=new ArrayList<>();
    Utils utils=new Utils();

    private final IBinder binder = new ServiceBinder();

//    public LocationService() {
//        super("location_service");
//    }


    public LocationService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public class ServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!isGooglePlayServicesAvailable()) {
            Log.e("LocationTest","location service have a problem");
        }

        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected())
            Log.e("already connected","already connected");
        else {
            Log.e("not connected","not connected");
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, startId, startId);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Location")
                .setTicker("location")
                .setContentText("app send location")
                .setPriority(Notification.PRIORITY_MIN)
                //.setSmallIcon(R.drawable.icon)
                .build();
        startForeground(0, notification);

//        Intent notificationIntent = new Intent(this, MainActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.app_icon)
//                .setContentTitle("My Awesome App")
//                .setContentText("Doing some work...")
//                .setContentIntent(pendingIntent).build();
//
//        startForeground(1337, notification);

        Log.e("onStartCommand", "onStartCommand");
//        if (!isGooglePlayServicesAvailable()) {
//            Log.e("LocationTest","location service have a problem");
//        }
//
//        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected())
//            Log.e("already connected","already connected");
//        else {
//            Log.e("not connected","not connected");
//            createLocationRequest();
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(LocationServices.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//            mGoogleApiClient.connect();
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(INTERVAL_IN_METERS);
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng currentLatLng=new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLocation = location;
//        Toast.makeText(this,"beta getLatitude " + location.getLatitude() + "getLongitude " + location.getLongitude(),Toast.LENGTH_SHORT).show();
        Log.v("mCurrentLocation", "getLatitude " + location.getLatitude() + "getLongitude " + location.getLongitude());
        if (SharedPreferenceManager.getInstance(this).getIsLogin()) {
            SendCurrentLocationAsyncTask sendCurrentLocationAsyncTask = new SendCurrentLocationAsyncTask(
                    currentLatLng,
                    SharedPreferenceManager.getInstance(this).getAvailableStatus(),
                    this,
                    new IResultsInterface() {
                        @Override
                        public void onCompleteWithResult(ResultEntity result) {
                        }
                    }
            );
            sendCurrentLocationAsyncTask.execute();
        }

        Intent intent=new Intent();
        intent.setAction(CHANGE_LOCATION_BROADCAST);
        intent.putExtra("location", location);
        sendBroadcast(intent);


        for (final Jobs job:jobs){
            double distance;
            if (job.getStatusId()>=7&&job.getStatusId()<11){
                distance=utils.distance(location.getLatitude(),location.getLongitude(),job.getOriginLat(),job.getOriginLng(),"K");
            }
            else {
                distance=utils.distance(location.getLatitude(),location.getLongitude(),job.getDestinationLat(),job.getDestinationLng(),"K");
            }

            if ((distance<=3)&&!job.isLessThreeKm()){
                if (job.getStatusId()==7) {
                    job.setIsLessThreeKm(true);
                    changeStatusOnServer(job, 8,7,false);
                    job.setStatusId(8);
                }
                if (job.getStatusId()==12) {
                    job.setIsLessThreeKm(true);
                    changeStatusOnServer(job, 13,12,false);
                    job.setStatusId(13);
                }
            }
            if ((distance*1000)<=100&&!job.isLessHundredMeters()){

               if (job.getStatusId()==8&&!job.isPopupShow()) {
                   if (context!=null&&context instanceof Activity&&!((Activity) context).isFinishing()) {
                       job.setIsLessHundredMeters(true);
                       job.setIsPopupShow(true);
                       //TODO to send popup was shown
                       changeToArrivedPopup(job.getId());
                       utils.openPopupWindow(context,
                               true,
                               getString(R.string.notice),
                               getString(R.string.confirm_arrival, job.getId() + ""),
                               context.getString(R.string.yes),
                               context.getString(R.string.no),
                               null,
                               new PopupInterface() {
                                   @Override
                                   public void onOk() {
                                       changeStatusOnServer(job, 9, 8,true);
                                       job.setStatusId(9);
                                   }
                               });
                   }

                }
                if (job.getStatusId()==13) {
                    job.setIsLessHundredMeters(true);
                    changeStatusOnServer(job, 14,13,false);
                    job.setStatusId(14);
                }
            }
        }

//        if ((location.getSpeed()*METERS_PER_SECOND_TO_KPH)>=15&&INTERVAL_IN_METERS!=20){
//            INTERVAL_IN_METERS=20;
//            createLocationRequest();
//            startLocationUpdates();
//        }
//        else if ((location.getSpeed()*METERS_PER_SECOND_TO_KPH)<15&&INTERVAL_IN_METERS!=50){
//            INTERVAL_IN_METERS=50;
//            createLocationRequest();
//            startLocationUpdates();
//        }


    }

    private void changeToArrivedPopup(long jobId) {

        ArrivedPopupAsyncTask arrivedPopupAsyncTask=new ArrivedPopupAsyncTask(context,jobId);
        arrivedPopupAsyncTask.execute();

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e("onConnected", "onConnected");
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation!=null){
                Intent intent=new Intent();
                intent.setAction(CHANGE_LOCATION_BROADCAST);
                intent.putExtra("location", mLastLocation);
                sendBroadcast(intent);
            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void startLocationUpdates() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.e("startLocationUpdates", "startLocationUpdates success");
        }
        else {
            Log.e("startLocationUpdates","startLocationUpdates error");
            //Toast.makeText(context,"permission of location missed",Toast.LENGTH_LONG).show();
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability=GoogleApiAvailability.getInstance();
        int status = googleApiAvailability
                .isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            //GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            Log.e("LocationTest","location service have a problem");
            return false;
        }
    }

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setJobs(List<Jobs> jobs) {
        this.jobs = jobs;
    }

    private void changeStatusOnServer(final Jobs job, final int statusToChange,int prevStatus, final boolean isArrivedPickup){

        UpdateJobStatus updateJobStatus = new UpdateJobStatus(this, job.getId(), statusToChange, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                job.setStatusId(statusToChange);
                if (isArrivedPickup){
                    //TODO send broadcast to refresh jobs
                    Intent intent=new Intent();
                    intent.putExtra("job_id",job.getId());
                    intent.setAction(CHANGE_STATUS_PICKUP_ARRIVED);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {

            }
        });

        updateJobStatus.execute();
    }
    public void updateJobIfExist(Jobs updateJob){
        boolean isExist=false;
        for (Jobs job:jobs){
            if (job.getId()==updateJob.getId()){
                isExist=true;
                job.setStatusId(updateJob.getStatusId());
            }
        }
        if (!isExist&&((updateJob.getStatusId()>=7&&updateJob.getStatusId()<11)||(updateJob.getStatusId()>=12&&updateJob.getStatusId()<15)))
            jobs.add(updateJob);
    }
}
