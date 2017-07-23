package com.shtibel.truckies.activities.dashboard.tabs.offers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shtibel.truckies.R;

/**
 * Created by Shtibel on 04/08/2016.
 */
public class OffersHolder extends RecyclerView.ViewHolder {

    TextView pickupDate;
    TextView dropoffDate;
    TextView pickupTime;
    TextView dropoffTime;

    ImageView map;
    ProgressBar mapProgressBar;
    TextView totalPayment;

    ImageView boxIcon;
    TextView boxText;
    ImageView palletIcon;
    TextView palletText;
    TextView totalWeigh;
    ImageView totalWeightIcon;
    LinearLayout specialsImages;
    TextView idTxt;


   // TextView totalDistance;
//    TextView numberOfBoxes;
//    TextView numberOfPallets;
    //TextView statusName;
//    View circleColor;
//    ImageView numberOfBoxesIcon;
//    ImageView numberOfPalletsIcon;
    //    TextView fromTime;
    //    TextView toTime;
    public OffersHolder(View itemView, int viewType) {
        super(itemView);

        pickupDate = (TextView) itemView.findViewById(R.id.offers_adap_pickup_date);
        pickupTime= (TextView) itemView.findViewById(R.id.offers_adap_pickup_time);
        dropoffDate = (TextView) itemView.findViewById(R.id.offers_adap_dropoff_date);
        dropoffTime= (TextView) itemView.findViewById(R.id.offers_adap_dropoff_time);

        map = (ImageView) itemView.findViewById(R.id.offers_adap_map);
        mapProgressBar= (ProgressBar) itemView.findViewById(R.id.offers_adap_map_progress);
        totalPayment = (TextView) itemView.findViewById(R.id.offers_adap_payment);

        boxIcon= (ImageView) itemView.findViewById(R.id.offers_adap_boxes_icon);
        boxText= (TextView) itemView.findViewById(R.id.offers_adap_boxes);
        palletIcon= (ImageView) itemView.findViewById(R.id.offers_adap_pallets_icon);
        palletText= (TextView) itemView.findViewById(R.id.offers_adap_pallets);
        totalWeigh = (TextView) itemView.findViewById(R.id.offers_adap_kg);
        totalWeightIcon= (ImageView) itemView.findViewById(R.id.offers_adap_kg_icon);
        specialsImages= (LinearLayout) itemView.findViewById(R.id.offers_adap_specials);
        idTxt= (TextView) itemView.findViewById(R.id.offers_adap_id);

        // statusName = (TextView) itemView.findViewById(R.id.offers_adap_status_txt);
       // circleColor = itemView.findViewById(R.id.offers_adap_circle);

      //  fromTime= (TextView) itemView.findViewById(R.id.offers_adap_pickup_time);
        //toTime= (TextView) itemView.findViewById(R.id.offers_adap_dropoff_time);



       // totalDistance = (TextView) itemView.findViewById(R.id.offers_adap_km);


//        numberOfBoxes= (TextView) itemView.findViewById(R.id.offers_adap_boxes);
//        numberOfBoxesIcon= (ImageView) itemView.findViewById(R.id.offers_adap_boxes_icon);
//        numberOfPallets= (TextView) itemView.findViewById(R.id.offers_adap_pallets);
//        numberOfPalletsIcon= (ImageView) itemView.findViewById(R.id.offers_adap_pallets_icon);


    }

}
