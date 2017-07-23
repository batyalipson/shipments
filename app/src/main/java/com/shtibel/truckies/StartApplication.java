package com.shtibel.truckies;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.JobStatuses;
import com.shtibel.truckies.generalClasses.NewImageDownloader;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.pushNotifications.BadgeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shtibel on 01/08/2016.
 */
public class StartApplication extends Application {
    public static String START_URL="http://truckies.shtibel.com";
    public static String packageName="com.shtibel.truckies";
//    public static String START_URL="https://www.truckiez.com.au";
    private static Context context;
    List<JobStatuses> statuses=new ArrayList<>();
    //public TruckType truckTypes[]=null;

    public static Context getAppContext() {
        return StartApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StartApplication.context = getApplicationContext();
        initImageLoaderData();
        BadgeUtils.setBadge(context, DBHelper.getDB(this)
                .getNotOpenedNotifications(SharedPreferenceManager.getInstance(this).getUserId()));
        MultiDex.install(this);
        Utils utils=new Utils();
        statuses=utils.fillStandardStatuses();
    }

    private void initImageLoaderData() {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .showImageOnFail(R.drawable.icon)
                .showImageForEmptyUri(R.drawable.icon)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new NewImageDownloader(this))
                .memoryCacheExtraOptions(1200, 1000)
                .threadPoolSize(1)
                .build();

        ImageLoader.getInstance().init(config);

    }


    public List<JobStatuses> getStatuses() {
        return statuses;
    }
    public String getStatusNameById(long id){
        for (int i=0;i<statuses.size();i++){
            if (statuses.get(i).getId()==id)
                return statuses.get(i).getName();
        }
        return "";
    }

//    public TruckType[] getTruckTypes() {
//        return truckTypes;
//    }
//
//    public String getTruckNameById(long id){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==id)
//                return truckTypes[i].getName();
//        }
//        return "";
//    }
//    public TruckType getTruckTypeById(long truckId){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==truckId)
//                return truckTypes[i];
//        }
//        return truckTypes[0];
//    }
//
//    public int getTruckTypePosition(long truckId){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==truckId)
//               return i;
//        }
//       return  0;
//    }

}
