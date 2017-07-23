package com.shtibel.truckies.activities.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.shtibel.truckies.R;

/**
 * Created by Shtibel on 22/08/2016.
 */
public class NotificationHolder extends RecyclerView.ViewHolder {

    SwipeLayout swipeLayout;
    View bottomWrapper;
    View topWrapper;
    LinearLayout centerLayout;
    TextView title;
    TextView content;
    TextView date;
    ImageView icon;
    RelativeLayout titleLayout;
    ProgressBar progress;

    ImageView delete;
    //Button edit;

    public NotificationHolder(View itemView) {
        super(itemView);

        swipeLayout= (SwipeLayout) itemView.findViewById(R.id.notification_adapter_swipe);
        bottomWrapper=itemView.findViewById(R.id.bottom_wrapper);
        topWrapper=itemView.findViewById(R.id.top_wrapper);
        centerLayout= (LinearLayout) itemView.findViewById(R.id.notification_adap_center);
        title= (TextView) itemView.findViewById(R.id.notification_adap_title);
        content= (TextView) itemView.findViewById(R.id.notification_adap_content);
        date= (TextView) itemView.findViewById(R.id.notification_adap_date);

        delete= (ImageView) itemView.findViewById(R.id.notification_adap_delete);
        icon= (ImageView) itemView.findViewById(R.id.notification_adap_image);
        titleLayout= (RelativeLayout) itemView.findViewById(R.id.notification_adap_title_layout);
        progress= (ProgressBar) itemView.findViewById(R.id.notification_adap_progress);
        //edit= (Button) itemView.findViewById(R.id.notification_adap_edit);
    }
}
