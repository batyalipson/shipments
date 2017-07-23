package com.shtibel.truckies.activities.dashboard.drawer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shtibel.truckies.R;

/**
 * Created by Shtibel on 06/07/2015.
 */
    public class DrawerViewHolder extends RecyclerView.ViewHolder {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    ImageView userImage;
    TextView name;
//    TextView phone;

    TextView text;
    ImageView image;
    TextView notifications;

    int type;

        public DrawerViewHolder(View itemView,int viewType) {
            super(itemView);
            if(viewType==TYPE_ITEM) {
                text = (TextView) itemView.findViewById(R.id.drawer_row_text);
                image = (ImageView) itemView.findViewById(R.id.drawer_row_icon);
                notifications= (TextView) itemView.findViewById(R.id.drawer_row_notifications);
                type=viewType;
            }
            else
            {
//                userImage= (ImageView) itemView.findViewById(R.id.dh_client_image);
//                name= (TextView) itemView.findViewById(R.id.dh_client_name);
//                phone= (TextView) itemView.findViewById(R.id.drawer_header_phone);
            }
        }
    }
