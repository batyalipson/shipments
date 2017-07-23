package com.shtibel.truckies.activities.notifications;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabJobs;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.activities.dashboard.tabs.offers.Offer;
import com.shtibel.truckies.activities.job.JobActivity;
import com.shtibel.truckies.activities.offer.OfferActivity;
import com.shtibel.truckies.asyncTasks.GetSpecificJobAsyncTask;
import com.shtibel.truckies.asyncTasks.GetSpecificOfferAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateMessageUserAsyncTask;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shtibel on 22/08/2016.
 */
public class NotificationAdapter  extends RecyclerView.Adapter<NotificationHolder> implements View.OnClickListener{

    List<Notification> notifications;
    Context context;
    TextView nothingYet;
    Utils utils=new Utils();

    NotificationAdapter(List<Notification> notifications,Context context,TextView nothingYet){
        this.notifications=notifications;
        this.context=context;
        this.nothingYet=nothingYet;
    }

    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_adapter, parent, false);
        NotificationHolder vhItem = new NotificationHolder(v); //Creating ViewHolder and passing the object of type view

        return vhItem;
    }

    @Override
    public void onBindViewHolder(NotificationHolder holder, int position) {

        Notification notification=notifications.get(position);

        holder.content.setText(notification.getContent().trim());
        holder.bottomWrapper.setOnClickListener(this);
        holder.bottomWrapper.setTag(notification);
       // holder.edit.setOnClickListener(this);
        holder.date.setText(utils.convertDateToString(notification.getDate(), "dd/MM/yy HH:mm:ss"));
        //holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,holder.topWrapper);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        holder.topWrapper.setOnClickListener(this);
        holder.topWrapper.setTag(notification);

        if (notification.isRead()) {
            holder.titleLayout.setBackgroundResource(R.drawable.style_grey_title);
            holder.title.setTextColor(context.getResources().getColor(R.color.text_grey));
            holder.date.setTextColor(context.getResources().getColor(R.color.text_grey));
        }

        else {
            holder.titleLayout.setBackgroundResource(R.drawable.style_light_blue_title);
            holder.title.setTextColor(Color.WHITE);
            holder.date.setTextColor(Color.WHITE);
        }

        holder.centerLayout.setOnClickListener(this);
        holder.centerLayout.setTag(notification);
        holder.icon.setImageResource(getResNotificationIcon(notification));

        if (notification.getType().equals("message"))
            holder.title.setText(context.getString(R.string.n_message));
        else if (notification.getType().equals("offer"))
            holder.title.setText(context.getString(R.string.n_new_offer)+"#"+notification.getMessageId());
        else if (notification.getType().equals("job"))
            holder.title.setText(context.getString(R.string.n_job)+"#"+notification.getMessageId());
    }

    private int getResNotificationIcon(Notification notification) {
        int resId=0;
        if (notification.isRead()){
            switch (notification.getType()){
                case "message":resId=R.drawable.push_black_message;
                    break;
                case "offer":resId=R.drawable.push_black_offer;
                    break;
                case "job":resId=R.drawable.push_black_job;
                    break;
            }
        }
        else {
            switch (notification.getType()) {
                case "message":
                    resId = R.drawable.push_white_message;
                    break;
                case "offer":
                    resId = R.drawable.push_white_offer;
                    break;
                case "job":
                    resId = R.drawable.push_white_job;
                    break;
            }
        }
        return resId;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public void onClick(final View view) {
//        if (view.getId()==R.id.notification_adap_edit)
//            Toast.makeText(context,"edit",Toast.LENGTH_SHORT).show();
         if (view.getId()==R.id.bottom_wrapper||view.getId()==R.id.top_wrapper) {
             Notification notification = (Notification) view.getTag();
             DBHelper.getDB(context).deleteNotification(notification.getId(), notification.getUserId());
             notifications = DBHelper.getDB(context).getNotificationsByUserId(SharedPreferenceManager.getInstance(context).getUserId());
             notifyDataSetChanged();

        }
        else {
            View parent = (View) view.getParent();
            ProgressBar progressBar= (ProgressBar) parent.findViewById(R.id.notification_adap_progress);
            Notification notification= (Notification) view.getTag();
            notification.setIsRead(true);
            notification.setIsOnStatusBar(false);
            DBHelper.getDB(context).updateNotification(notification);
            notifications=DBHelper.getDB(context)
                    .getNotificationsByUserId(SharedPreferenceManager.getInstance(context).getUserId());

            if (notification.getType().equals("message")) {
                notifyDataSetChanged();
                UpdateMessageUserAsyncTask updateMessageUserAsyncTask = new UpdateMessageUserAsyncTask(context, notification.getMessageId());
                updateMessageUserAsyncTask.execute();
            }
            else if (notification.getType().equals("offer")){
                goToOffer(context,notification,progressBar);
            }
            else if (notification.getType().equals("job")){
                goToJob(context,notification,progressBar);
            }
        }

    }

    public void refreshList(List<Notification>notifications){
        this.notifications=notifications;
        notifyDataSetChanged();
        if (notifications.size()>0)
            nothingYet.setVisibility(View.GONE);
        else
            nothingYet.setVisibility(View.VISIBLE);
    }

    private void goToOffer(final Context context,Notification notification, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        GetSpecificOfferAsyncTask getSpecificOfferAsyncTask=new GetSpecificOfferAsyncTask(notification.getMessageId(), context, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                progressBar.setVisibility(View.GONE);
                notifyDataSetChanged();
                if (utils.isWifiOpen(context)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        int err = jsonObject.getInt("err");
                        //String jsonObjStr = jsonObject.getString("offer");
                        if (err == 0) {
                            JSONObject offerJson = jsonObject.getJSONObject("offer");
                            Offer offer = utils.fillOffer(offerJson);
                            Intent intent = new Intent();
                            intent.setClass(context, OfferActivity.class);
                            intent.putExtra("offer", offer);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
//                        utils.showInfoPopup(context.getString(R.string.offer)
//                                ,context.getString(R.string.offer_error),context);
                            utils.openPopupWindow(context,
                                    true,
                                    context.getString(R.string.notice),
                                    context.getString(R.string.offer_error),
                                    context.getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        utils.showServerError(context, e.getMessage());
                    }
                }
                else
                    utils.locationClosePopup(context);
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                progressBar.setVisibility(View.GONE);
                utils.showServerError(context, resultEntity.getResult());
            }
        });
        getSpecificOfferAsyncTask.execute();
    }



    private void goToJob(final Context context, Notification notification, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        GetSpecificJobAsyncTask getSpecificJobAsyncTask=new GetSpecificJobAsyncTask(notification.getMessageId(), context, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                    progressBar.setVisibility(View.GONE);
                    notifyDataSetChanged();
                if (utils.isWifiOpen(context)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        //String jobObjStr = jsonObject.getString("job");
                        int err = jsonObject.getInt("err");
                        if (err == 0 ) {
                            JSONObject jobJson = jsonObject.getJSONObject("job");
                            Jobs job = utils.fillJob(jobJson);
                            Intent intent = new Intent();
                            intent.setClass(context, JobActivity.class);
                            intent.putExtra("job", job);
                            intent.putExtra("pressedButton", DashboardTabJobs.SCHEDULE);
                            context.startActivity(intent);
                        } else {
//                        utils.showInfoPopup(context.getString(R.string.job_)
//                                ,context.getString(R.string.job_error),context);
                            utils.openPopupWindow(context,
                                    true,
                                    context.getString(R.string.notice),
                                    context.getString(R.string.job_error),
                                    context.getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        utils.showServerError(context, e.getMessage());
                    }
                }
                else
                    utils.locationClosePopup(context);
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                progressBar.setVisibility(View.GONE);
                utils.showServerError(context, resultEntity.getResult());
            }
        });

        getSpecificJobAsyncTask.execute();

    }


    public void refresh(List<Notification> notifications){
        this.notifications=notifications;
        notifyDataSetChanged();
    }

}
