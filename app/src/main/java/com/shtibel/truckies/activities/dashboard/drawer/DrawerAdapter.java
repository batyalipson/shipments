package com.shtibel.truckies.activities.dashboard.drawer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.account.AccountActivity;
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.activities.notifications.NotificationsActivity;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Shtibel on 06/07/2015.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerViewHolder> implements View.OnClickListener{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public static final int ACCOUNT_RESULT=1234;

    private List<String> titels;
    private List<Integer> icons;
    private Context context;
    DrawerLayout drawerLayout;
    //View.OnClickListener chooseImageClick;
//    private DrawerLayout drawer;

    public DrawerAdapter(Context context,DrawerLayout drawerLayout)
    {
        this.context=context;
        //this.chooseImageClick=chooseImageClick;
        this.drawerLayout=drawerLayout;
        initArrays();
    }

    private void initArrays() {
        titels= Arrays.asList(context.getResources().getStringArray(R.array.drawer_array));

        icons=new ArrayList<Integer>();
        icons.add(R.drawable.dashboard_notification);
        icons.add(R.drawable.dashboard_account);
        icons.add(R.drawable.dashboard_support);
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if(viewType==TYPE_ITEM)
             v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_row, parent, false); //Inflating the layout
       // else
          //  v=LayoutInflater.from(parent.getContext())
          //          .inflate(R.layout.drawer_header, parent, false); //Inflating the layout
        DrawerViewHolder vhItem = new DrawerViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

        return vhItem;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {

        if(holder.type==TYPE_ITEM) {
            holder.text.setText(titels.get(position));
            holder.image.setImageResource(icons.get(position));

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);

            if (position==0){
                int numberOfNotOpened= DBHelper.getDB(context)
                        .getNotOpenedNotifications(SharedPreferenceManager.getInstance(context).getUserId());
                if (numberOfNotOpened>0) {
                    holder.notifications.setText(numberOfNotOpened + "");
                    holder.notifications.setVisibility(View.VISIBLE);
                }
                else
                    holder.notifications.setVisibility(View.GONE);
            }
            else {
                holder.notifications.setVisibility(View.GONE);
            }
        }
        else {

        }
    }


    @Override
    public int getItemCount() {
        return titels.size();
    }

    @Override
    public int getItemViewType(int position) {
       // if (isPositionHeader(position))
       //    return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onClick(View view) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        int position= (int) view.getTag();
        if (position==0) {
            Intent intent = new Intent();
            intent.setClass(context, NotificationsActivity.class);
            context.startActivity(intent);
        }
        else if (position==1){
            Intent intent = new Intent();
            intent.setClass(context, AccountActivity.class);
            String numberOfVoters=((MainActivity)context).getNumberOfVoters();
            double avgRating = ((MainActivity)context).getAvgRating();
            intent.putExtra("numberOfVoters", numberOfVoters);
            intent.putExtra("avgRating",avgRating);
            ((Activity)context).startActivityForResult(intent,ACCOUNT_RESULT);
        }
        else if (position==2){
            openDialer(SharedPreferenceManager.getInstance(context).getSupportPhone());
        }
    }

    private void openDialer(String phoneNumber){
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.CALL_PHONE,},
                    Utils.MY_PERMISSIONS_REQUEST_DIALER);

        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }

//    @Override
//    public void onClick(View view) {
//        Intent intent=new Intent();
//        intent.setClass(context,ScanImageActivity.class);
//        context.startActivity(intent);
//    }
}
