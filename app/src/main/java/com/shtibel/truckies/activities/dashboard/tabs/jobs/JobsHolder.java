package com.shtibel.truckies.activities.dashboard.tabs.jobs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shtibel.truckies.R;

/**
 * Created by Shtibel on 03/08/2016.
 */
public class JobsHolder  extends RecyclerView.ViewHolder {

    View circleColor;
    ImageView headerIconImage;
    TextView headerText;

    ImageView map;
    ProgressBar mapProgressBar;
    TextView totalPayment;

    LinearLayout footerDropoff;
    ImageView footerIconImage;
    TextView footerText;

    TextView numberOfBoxes;
    ImageView numberOfBoxesIcon;

    TextView numberOfPallets;
    ImageView numberOfPalletsIcon;

    TextView totalWeigh;
    ImageView totalWeighIcon;

    LinearLayout specialsImages;

    TextView idTxt;

//    TextView fromTime;
//    TextView fromDate;
//    TextView toTime;
//    TextView toDate;
//    TextView totalDistance;
//    TextView statusName;
//
//
//    TextView inTime;

    public JobsHolder(View itemView,int viewType) {
        super(itemView);

        circleColor = itemView.findViewById(R.id.jobs_adap_header_icon_circle);
        headerIconImage= (ImageView) itemView.findViewById(R.id.jobs_adap_header_icon_image);
        headerText= (TextView) itemView.findViewById(R.id.jobs_adap_header_txt);

        map = (ImageView) itemView.findViewById(R.id.jobs_adap_map);
        mapProgressBar= (ProgressBar) itemView.findViewById(R.id.jobs_adap_map_progress);
        totalPayment = (TextView) itemView.findViewById(R.id.jobs_adap_payment);

        footerDropoff= (LinearLayout) itemView.findViewById(R.id.jobs_adap_dropoff_footer);
        footerIconImage= (ImageView) itemView.findViewById(R.id.jobs_adap_footer_icon_image);
        footerText= (TextView) itemView.findViewById(R.id.jobs_adap_footer_txt);

        numberOfBoxes= (TextView) itemView.findViewById(R.id.jobs_adap_boxes);
        numberOfBoxesIcon= (ImageView) itemView.findViewById(R.id.jobs_adap_boxes_icon);

        numberOfPallets= (TextView) itemView.findViewById(R.id.jobs_adap_pallets);
        numberOfPalletsIcon= (ImageView) itemView.findViewById(R.id.jobs_adap_pallets_icon);

        totalWeigh = (TextView) itemView.findViewById(R.id.jobs_adap_kg);
        totalWeighIcon= (ImageView) itemView.findViewById(R.id.jobs_adap_kg_icon);

        specialsImages= (LinearLayout) itemView.findViewById(R.id.jobs_adap_specials);
        idTxt= (TextView) itemView.findViewById(R.id.jobs_adap_id);

//        statusName = (TextView) itemView.findViewById(R.id.jobs_adap_status_txt);
//        fromTime= (TextView) itemView.findViewById(R.id.jobs_adap_pickup_time);
//        toTime= (TextView) itemView.findViewById(R.id.jobs_adap_dropoff_time);
//        fromDate = (TextView) itemView.findViewById(R.id.jobs_adap_pickup_date);
//        toDate = (TextView) itemView.findViewById(R.id.jobs_adap_dropoff_date);
//        totalDistance = (TextView) itemView.findViewById(R.id.jobs_adap_km);
//        inTime= (TextView) itemView.findViewById(R.id.jobs_adap_in_time);
    }

}
