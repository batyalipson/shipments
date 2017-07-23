package com.shtibel.truckies.activities.dashboard.tabs.offers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabOffers;
import com.shtibel.truckies.activities.offer.OfferActivity;
import com.shtibel.truckies.asyncTasks.GetPathAsyncTask;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Shtibel on 04/08/2016.
 */
public class OffersAdapter  extends  RecyclerView.Adapter<OffersHolder> implements View.OnClickListener  {

    Utils utils=new Utils();
    List<Offer> offers;
    Context context;
    int pressedButton;
    int mapHeight;
    int mapWidth;

    public OffersAdapter(List<Offer> offers,int pressedButton, Context context){
        this.offers=offers;
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
    public OffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_adapter,parent,false);
        OffersHolder vhItem = new OffersHolder(v,viewType);

        return vhItem;
    }

    @Override
    public void onBindViewHolder(OffersHolder holder, int position) {
        Offer offer = offers.get(position);

        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);

        holder.pickupDate.setText(offer.getPickupDate());
        holder.pickupTime.setText(offer.getPickupFromTime() + "-" + offer.getPickupTillTime());
        holder.dropoffDate.setText(offer.getDropoffDate());
        holder.dropoffTime.setText(offer.getDropoffFromTime() + "-" + offer.getDropoffTillTime());

        holder.map.setImageResource(R.drawable.map_bg);
        initImageMap(holder, offer);

        holder.totalPayment.setText(offer.getShipmentShipperPaymentText());
        if (SharedPreferenceManager.getInstance(context).getCanSeePrice()==0)
            holder.totalPayment.setVisibility(View.GONE);
        else
            holder.totalPayment.setVisibility(View.VISIBLE);


//        if (offer.getNumberTruckload()>0) {
            holder.boxIcon.setImageResource(R.drawable.truckload);
            holder.boxIcon.setVisibility(View.GONE);
            holder.boxText.setText(offer.getTruckloadName());
            holder.palletIcon.setVisibility(View.GONE);
            holder.palletText.setVisibility(View.GONE);
//        }
//        else {
//            if (offer.getNumberPallets()>0&&offer.getNumberBoxes()>0){
//                holder.boxText.setText(offer.getNumberBoxes()+ " " + context.getString(R.string.boxes));
//                holder.palletText.setText(offer.getNumberPallets() + " " + context.getString(R.string.pallets));
//            }
//            else {
//                if (offer.getNumberBoxes() == 0) {
//                    holder.boxText.setVisibility(View.GONE);
//                    holder.boxIcon.setVisibility(View.GONE);
//                    holder.palletText.setText(offer.getNumberPallets() + " " + context.getString(R.string.pallets));
//                } else {
//                    holder.palletText.setVisibility(View.GONE);
//                    holder.palletIcon.setVisibility(View.GONE);
//                    holder.boxText.setText(offer.getNumberBoxes() + " " + context.getString(R.string.boxes));
//                }
//
//            }
//        }

        //double weightInTon=offer.getTotalLoadWeightText()/1000.0;
        if (offer.getTotalWeightType().equalsIgnoreCase("t")) {
            holder.totalWeigh.setText(new DecimalFormat(".#").format(offer.getTotalLoadWeightText()) + " " + context.getString(R.string.ton));
            holder.totalWeightIcon.setImageResource(R.drawable.load_ton);
        }
        else {
            holder.totalWeigh.setText(offer.getTotalLoadWeightText()+ " " + context.getString(R.string.kg));
            holder.totalWeightIcon.setImageResource(R.drawable.load_kg);
        }

        holder.specialsImages.removeAllViews();
        boolean isFirst=true;
        for (String url:offer.getSpecialRequest()){

            ImageView imageView=new ImageView(context);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(StartApplication.START_URL+url, imageView);
            int size=(int)context.getResources().getDimension(R.dimen.x_small_icon);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);

            if (isFirst)
                isFirst=false;
            else
                params.setMargins((int)context.getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
            imageView.setLayoutParams(params);
            holder.specialsImages.addView(imageView);
        }
        holder.idTxt.setText(context.getString(R.string.offer_s)+" #"+offer.getId());
    }


    private void initImageMap(final OffersHolder holder, final Offer offer) {
        holder.mapProgressBar.setVisibility(View.VISIBLE);
        if (offer.getLatLngPoints().equals("")) {
            String urlToGoogle = utils.makeURLtoGetPath(offer.getOriginLat(), offer.getOriginLng()
                    , offer.getDestinationLat(), offer.getDestinationLng());


            GetPathAsyncTask getPathAsyncTask = new GetPathAsyncTask(context, urlToGoogle, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    String latLngPoints=utils.getPointsOfPath(result.getResult());
                    loadImage(holder, offer, latLngPoints);
                }
            });
            getPathAsyncTask.execute();
        }
        else
            loadImage(holder,offer,offer.getLatLngPoints());
    }

    private void loadImage(final OffersHolder holder, final Offer offer,String latLngPoints){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnFail(null)
                .showImageForEmptyUri(null)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .considerExifParams(true).build();

        //String latLngPoints=utils.getPointsOfPath(result.getResult());
        offer.setLatLngPoints(latLngPoints);
        //          my_location_button1=" +shipment.getOriginLat() + "," + shipment.getOriginLng() + " "&zoom=17" +
        ImageLoader imageLoader = ImageLoader.getInstance();
        String url="http://maps.googleapis.com/maps/api/staticmap?" +
                //"my_location_button1=" +(offer.getOriginLat()+offer.getDestinationLat())/2 + "," + (offer.getOriginLng()+offer.getDestinationLng())/2  +
                "&size="+mapWidth+"x"+mapHeight +
                "&markers=icon:"+StartApplication.START_URL+"/template/application/from.png|"+offer.getOriginLat() + "," + offer.getOriginLng()+"" +
                "&markers=icon:"+StartApplication.START_URL+"/template/application/to.png|"+offer.getDestinationLat() + "," + offer.getDestinationLng()+
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
        return offers.size();
    }

    @Override
    public void onClick(View view) {
        if (utils.isWifiOpen(context)) {
                int position = (int) view.getTag();
                Intent intent = new Intent();
                intent.setClass(context, OfferActivity.class);
                intent.putExtra("offer", offers.get(position));
                intent.putExtra("position", position);
                ((Activity) context).startActivityForResult(intent, DashboardTabOffers.ACCEPT_OFFER_RESULT);
        }
        else
            utils.locationClosePopup(context);

    }
    public void refreshList(List<Offer> offers){
        this.offers=offers;
        notifyDataSetChanged();
    }
}
