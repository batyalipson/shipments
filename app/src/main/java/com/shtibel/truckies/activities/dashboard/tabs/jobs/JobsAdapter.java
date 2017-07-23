package com.shtibel.truckies.activities.dashboard.tabs.jobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.job.JobActivity;
import com.shtibel.truckies.asyncTasks.GetPathAsyncTask;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.SpecialRequest;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Shtibel on 04/07/2016.
 */
public class JobsAdapter extends  RecyclerView.Adapter<JobsHolder> implements View.OnClickListener {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    Utils utils=new Utils();
    List<Jobs> jobsList;
    Context context;
    int pressedButton;
    int mapHeight;
    int mapWidth;

    public JobsAdapter(List<Jobs> jobsList,int pressedButton, Context context){
        this.jobsList=jobsList;
        this.context=context;
        this.pressedButton=pressedButton;
        mapHeight=utils.getStaticMapHeight(context);
        mapWidth=utils.getStaticMapWidth(context);
        if (mapWidth>640){
            mapHeight=640*mapHeight/mapWidth;
            mapWidth=640;
        }
    }

    @Override
    public JobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_adapter,parent,false);
        JobsHolder vhItem = new JobsHolder(v,viewType);

        return vhItem;
    }

    @Override
    public void onBindViewHolder(final JobsHolder holder, int position) {

        Jobs jobs = jobsList.get(position);

        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);

        if (jobs.getStatusId()>=5&&jobs.getStatusId()<11){
            holder.circleColor.setVisibility(View.GONE);
            holder.headerIconImage.setVisibility(View.VISIBLE);
            holder.headerIconImage.setImageResource(R.drawable.pickup);

            holder.headerText.setText(context.getString(R.string.pickup)+" "+jobs.getPickupDate()+" "+jobs.getPickupFromTime()+"-"+jobs.getPickupTillTime());

            if (jobs.getPickupInTime().contains("late by"))
                holder.headerText.setTextColor(context.getResources().getColor(R.color.red));
            else if (jobs.getPickupInTime().contains("1 hour")&&!jobs.getPickupInTime().contains("day"))
                holder.headerText.setTextColor(context.getResources().getColor(R.color.orange));
            else
                holder.headerText.setTextColor(context.getResources().getColor(R.color.strong_green));
        }
        else if (jobs.getStatusId()>=11&&jobs.getStatusId()<15){
            holder.circleColor.setVisibility(View.GONE);
            holder.headerIconImage.setVisibility(View.VISIBLE);
            holder.headerIconImage.setImageResource(R.drawable.dropoff);

            holder.headerText.setText(context.getString(R.string.dropoff)+" "+jobs.getDropoffDate()+" "+jobs.getDropoffFromTime()+"-"+jobs.getDropoffTillTime());

            if (jobs.getDropoffInTime().contains("late by"))
                holder.headerText.setTextColor(context.getResources().getColor(R.color.red));
            else if (jobs.getDropoffInTime().contains("1 hour")&&!jobs.getDropoffInTime().contains("day"))
                holder.headerText.setTextColor(context.getResources().getColor(R.color.orange));
            else
                holder.headerText.setTextColor(context.getResources().getColor(R.color.strong_green));
        }
        else if (jobs.getStatusId()>=15&&jobs.getStatusId()<18) {
            holder.circleColor.setVisibility(View.VISIBLE);
            holder.headerIconImage.setVisibility(View.GONE);
            holder.headerText.setText(jobs.getStatusName());
            holder.headerText.setTextColor(context.getResources().getColor(R.color.text_grey));
            utils.drawCircle(holder.circleColor.getWidth(),
                    holder.circleColor.getHeight(),jobs.getCircleColor() , holder.circleColor);
        }
        else {
            holder.circleColor.setVisibility(View.VISIBLE);
            holder.headerIconImage.setVisibility(View.GONE);
            holder.headerText.setText(context.getString(R.string.completed_at) + " " + jobs.getCompletedDate());
            holder.headerText.setTextColor(context.getResources().getColor(R.color.text_grey));
            utils.drawCircle(holder.circleColor.getWidth(),
                    holder.circleColor.getHeight(),"#1D993B", holder.circleColor);

        }

        holder.map.setImageResource(R.drawable.map_bg);
        initImageMap(holder, jobs);

        holder.totalPayment.setText(jobs.getShipmentShipperPaymentText());
        if (SharedPreferenceManager.getInstance(context).getCanSeePrice()==0)
            holder.totalPayment.setVisibility(View.GONE);
        else
            holder.totalPayment.setVisibility(View.VISIBLE);


        if (jobs.getStatusId()>=5&&jobs.getStatusId()<11){

            holder.footerDropoff.setVisibility(View.VISIBLE);
            holder.footerText.setText(context.getString(R.string.dropoff)+" "+jobs.getDropoffDate()+" "+jobs.getDropoffFromTime()+"-"+jobs.getDropoffTillTime());
        }
        else
            holder.footerDropoff.setVisibility(View.GONE);

        holder.numberOfBoxesIcon.setVisibility(View.VISIBLE);
        holder.numberOfBoxesIcon.setImageResource(R.drawable.box);
        holder.numberOfPalletsIcon.setVisibility(View.VISIBLE);
        holder.numberOfPalletsIcon.setImageResource(R.drawable.pallet);

        holder.numberOfBoxes.setVisibility(View.VISIBLE);
        holder.numberOfPallets.setVisibility(View.VISIBLE);

//        if (jobs.getNumberTruckload()>0) {
            holder.numberOfBoxesIcon.setImageResource(R.drawable.truckload);
            holder.numberOfBoxesIcon.setVisibility(View.GONE);
            holder.numberOfBoxes.setText(jobs.getTruckloadName());
            holder.numberOfPalletsIcon.setVisibility(View.GONE);
            holder.numberOfPallets.setVisibility(View.GONE);
//        }
//        else {
//            if (jobs.getNumberPallets()>0&&jobs.getNumberBoxes()>0){
//                holder.numberOfBoxes.setText(jobs.getNumberBoxes()+ " " + context.getString(R.string.boxes));
//                holder.numberOfPallets.setText(jobs.getNumberPallets() + " " + context.getString(R.string.pallets));
//            }
//            else {
//                if (jobs.getNumberBoxes() == 0) {
//                    holder.numberOfBoxes.setVisibility(View.GONE);
//                    holder.numberOfBoxesIcon.setVisibility(View.GONE);
//                    holder.numberOfPallets.setText(jobs.getNumberPallets() + " " + context.getString(R.string.pallets));
//                } else {
//                    holder.numberOfPallets.setVisibility(View.GONE);
//                    holder.numberOfPalletsIcon.setVisibility(View.GONE);
//                    holder.numberOfBoxes.setText(jobs.getNumberBoxes() + " " + context.getString(R.string.boxes));
//                }
//
//            }
//        }
        //double weightInTon=jobs.getTotalLoadWeightText()/1000.0;
        if (jobs.getTotalWeightType().equalsIgnoreCase("t")) {
            holder.totalWeigh.setText(new DecimalFormat(".#").format(jobs.getTotalLoadWeightText())+ " " + context.getString(R.string.ton));
            holder.totalWeighIcon.setImageResource(R.drawable.load_ton);
        }
        else {
            holder.totalWeigh.setText(jobs.getTotalLoadWeightText()+ " " + context.getString(R.string.kg));
            holder.totalWeighIcon.setImageResource(R.drawable.load_kg);
        }

        holder.specialsImages.removeAllViews();
        boolean isFirst=true;
        for (SpecialRequest specialRequest:jobs.getSpecials()){

            ImageView imageView=new ImageView(context);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(StartApplication.START_URL+specialRequest.getImageResource(), imageView);
            int size=(int)context.getResources().getDimension(R.dimen.x_small_icon);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);

            if (isFirst)
                isFirst=false;
            else
                params.setMargins((int)context.getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
            imageView.setLayoutParams(params);
            holder.specialsImages.addView(imageView);

        }

        holder.idTxt.setText(context.getString(R.string.job_s)+" #"+jobs.getId());
    }

    private void initImageMap(final JobsHolder holder, final Jobs jobs) {
        holder.mapProgressBar.setVisibility(View.VISIBLE);
        if (jobs.getLatLngPoints().equals("")
                || jobs.getLatLngPoints().equals("null")) {
            String urlToGoogle = utils.makeURLtoGetPath(jobs.getOriginLat(), jobs.getOriginLng()
                    , jobs.getDestinationLat(), jobs.getDestinationLng());


            GetPathAsyncTask getPathAsyncTask = new GetPathAsyncTask(context, urlToGoogle, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    String latLngPoints=utils.getPointsOfPath(result.getResult());
                    loadImage(holder, jobs, latLngPoints);
                }
            });
            getPathAsyncTask.execute();
        }
        else
            loadImage(holder,jobs,jobs.getLatLngPoints());
    }

    private void loadImage(final JobsHolder holder, final Jobs jobs,String latLngPoints){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnFail(null)
                .showImageForEmptyUri(null)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true).build();

        //String latLngPoints=utils.getPointsOfPath(result.getResult());
        jobs.setLatLngPoints(latLngPoints);
        //          my_location_button1=" +shipment.getOriginLat() + "," + shipment.getOriginLng() + " "&zoom=17" +
        ImageLoader imageLoader = ImageLoader.getInstance();
        String url="http://maps.googleapis.com/maps/api/staticmap?" +
                //"my_location_button1=" +(jobs.getOriginLat()+jobs.getDestinationLat())/2 + "," + (jobs.getOriginLng()+jobs.getDestinationLng())/2  +
                "&size="+mapWidth+"x"+mapHeight +
                "&markers=icon:"+StartApplication.START_URL+"/template/application/from.png|"+jobs.getOriginLat() + "," + jobs.getOriginLng()+"" +
                "&markers=icon:"+StartApplication.START_URL+"/template/application/to.png|"+jobs.getDestinationLat() + "," + jobs.getDestinationLng()+
                "&key=AIzaSyAr8t2iKU1zyA2MxIqLAhiSzOKYoO5f9jk"+
                "&path=color:0x378EB9ff|weight:4|enc:"+latLngPoints+
                "&sensor=false";

        imageLoader.loadImage(url,defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            FadeInBitmapDisplayer.animate(holder.map,500);
                holder.map.setImageBitmap(loadedImage);
                holder.mapProgressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public int getItemCount() {
        return jobsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public void refreshList(List<Jobs> jobsList,int pressedButton){
        this.jobsList=jobsList;
        this.pressedButton=pressedButton;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(JobsHolder holder) {

    }


    @Override
    public void onClick(View view) {
        if (!utils.isWifiOpen(context)){
            utils.locationClosePopup(context);
        }
        else {
            int position = (int) view.getTag();
            Intent intent = new Intent();
            intent.setClass(context, JobActivity.class);
            intent.putExtra("job", jobsList.get(position));
            intent.putExtra("position", position);
            intent.putExtra("pressedButton", pressedButton);
            context.startActivity(intent);
        }
    }

    public static boolean isImageAvailableInCache(String imageUrl){
        MemoryCache memoryCache = ImageLoader.getInstance().getMemoryCache();
        if(memoryCache!=null) {
            for (String key : memoryCache.keys()) {
                if (key.startsWith(imageUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

}
