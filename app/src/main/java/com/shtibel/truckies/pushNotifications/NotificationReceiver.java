package com.shtibel.truckies.pushNotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabJobs;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.activities.dashboard.tabs.offers.Offer;
import com.shtibel.truckies.activities.job.JobActivity;
import com.shtibel.truckies.activities.login.LoginActivity;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.activities.notifications.NotificationsActivity;
import com.shtibel.truckies.activities.offer.OfferActivity;
import com.shtibel.truckies.alertDialogActivity.AlertDialogActivity;
import com.shtibel.truckies.asyncTasks.GetSpecificJobAsyncTask;
import com.shtibel.truckies.asyncTasks.GetSpecificOfferAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateMessageUserAsyncTask;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shtibel on 22/08/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_OPEN="notification_open";
    public static final String GENERAL_NOTIFICATION_OPEN="general_notification_open";
    public static final String NOTIFICATION_DELETE="notification_delete";
    public static final String GENERAL_NOTIFICATION_DELETE="general_notification_delete";
    public static final String UPDATE_NOTIFICATIONS_TABLE="update_notifications_table";
    Utils utils=new Utils();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,MainActivity.IS_ACTIVE+"",Toast.LENGTH_SHORT).show();
        Notification notification= (Notification) intent.getSerializableExtra("notification");

        if (intent.getAction().equals(NOTIFICATION_OPEN)){
            notification.setIsRead(true);
            notification.setIsOnStatusBar(false);
            DBHelper.getDB(context).updateNotification(notification);
            if (notification.getType().equals("message")) {
                UpdateMessageUserAsyncTask updateMessageUserAsyncTask = new UpdateMessageUserAsyncTask(context, notification.getMessageId());
                updateMessageUserAsyncTask.execute();
            }

            if (MainActivity.IS_ACTIVE){
                if (notification.getType().equals("offer")){
                    goToOffer(context,notification);
                }
                else if (notification.getType().equals("job")){
                    goToJob(context,notification);
                }
                else if (notification.getType().equals("message")){
                    Intent notificationIntent = new Intent();
                    notificationIntent.setClass(context, NotificationsActivity.class);
                    notificationIntent.putExtra("action", GENERAL_NOTIFICATION_OPEN);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(notificationIntent);
                }
            }
            else {
                openDashBoard(notification,context,intent.getAction());
            }

        }

        else if (intent.getAction().equals(NOTIFICATION_DELETE)){
            notification.setIsOnStatusBar(false);
            DBHelper.getDB(context).updateNotification(notification);
        }

        else if (intent.getAction().equals(GENERAL_NOTIFICATION_OPEN)) {
            DBHelper.getDB(context)
                    .updateOnStatusBarNotifications(SharedPreferenceManager.getInstance(context).getUserId());
            if (MainActivity.IS_ACTIVE) {
                Intent notificationIntent = new Intent();
                notificationIntent.setClass(context, NotificationsActivity.class);
                notificationIntent.putExtra("action", GENERAL_NOTIFICATION_OPEN);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(notificationIntent);
            }
            else {
                openDashBoard(notification,context,intent.getAction());
            }
        }
        else if (intent.getAction().equals(GENERAL_NOTIFICATION_DELETE)){
            DBHelper.getDB(context)
                    .updateOnStatusBarNotifications(SharedPreferenceManager.getInstance(context).getUserId());
        }
        //Toast.makeText(context,intent.getAction()+"",Toast.LENGTH_SHORT).show();
        sendBroadCast(context);
    }

    private void sendBroadCast(Context context) {
        Intent intent = new Intent();
        intent.setAction(UPDATE_NOTIFICATIONS_TABLE);
        context.sendBroadcast(intent);

    }


    private void openDashBoard(Notification notification, Context context, String action) {
        Intent openDashboardIntent = new Intent();
        openDashboardIntent.setClass(context, LoginActivity.class);
        openDashboardIntent.putExtra("isFromOpenNotification", true);
        openDashboardIntent.putExtra("notification", notification);
        openDashboardIntent.putExtra("action",action);
        openDashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                openDashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                openDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                openDashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openDashboardIntent);
    }

    private void goToOffer(final Context context,Notification notification) {
        GetSpecificOfferAsyncTask getSpecificOfferAsyncTask=new GetSpecificOfferAsyncTask(notification.getMessageId(), context, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
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
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(context, AlertDialogActivity.class);
                            intent.putExtra("title", context.getString(R.string.offer));
                            intent.putExtra("message", context.getString(R.string.offer_error));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
//                        utils.showInfoPopup(context.getString(R.string.offer)
//                                ,context.getString(R.string.offer_error),context);
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
                utils.showServerError(context, resultEntity.getResult());
            }
        });
        getSpecificOfferAsyncTask.execute();
    }
    private void goToJob(final Context context, Notification notification) {
        GetSpecificJobAsyncTask getSpecificJobAsyncTask=new GetSpecificJobAsyncTask(notification.getMessageId(), context, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                if (utils.isWifiOpen(context)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        int err = jsonObject.getInt("err");
                        //String jobObjStr = jsonObject.getString("job");
                        if (err == 0) {
                            JSONObject jobJson = jsonObject.getJSONObject("job");
                            Jobs job = utils.fillJob(jobJson);
                            Intent intent = new Intent();
                            intent.setClass(context, JobActivity.class);
                            intent.putExtra("job", job);
                            intent.putExtra("pressedButton", DashboardTabJobs.SCHEDULE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(context, AlertDialogActivity.class);
                            intent.putExtra("title", context.getString(R.string.job));
                            intent.putExtra("message", context.getString(R.string.job_error));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
//                                utils.showInfoPopup(context.getString(R.string.job_)
//                                        , context.getString(R.string.job_error), context);
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
                utils.showServerError(context,resultEntity.getResult());
            }
        });

        getSpecificJobAsyncTask.execute();

    }


//    private Offer fillOffer(JSONObject jsonObject) throws JSONException {
//
//        Offer offer=new Offer();
//
//        offer.setId(jsonObject.getLong("id"));
//        offer.setStatusName(Html.fromHtml(jsonObject.getString("status_name")).toString());
//
//        offer.setPickupDate(jsonObject.getString("pickup_date"));
//        offer.setPickupFromTime(jsonObject.getString("pickup_from_time"));
//        offer.setPickupTillTime(jsonObject.getString("pickup_till_time"));
//
//        offer.setDropoffDate(jsonObject.getString("dropoff_date"));
//        offer.setDropoffFromTime(jsonObject.getString("dropoff_from_time"));
//        offer.setDropoffTillTime(jsonObject.getString("dropoff_till_time"));
//
//        offer.setOriginAddress(Html.fromHtml(jsonObject.getString("origin_address")).toString());
//        offer.setOriginAddressName(jsonObject.getString("origin_address_name"));
//        offer.setDestinationAddress(Html.fromHtml(jsonObject.getString("destination_address")).toString());
//        offer.setDestinationAddressName(jsonObject.getString("destination_address_name"));
//
//        offer.setTotalLoadWeightText(jsonObject.getLong("total_load_weight"));
//        offer.setShipmentShipperPaymentText(jsonObject.getString("shipment_carrier_payout_text"));
//        offer.setTotalDrivingDistance(jsonObject.getString("total_driving_distance"));
//
//        offer.setOriginLat(jsonObject.getDouble("origin_lat"));
//        offer.setOriginLng(jsonObject.getDouble("origin_lng"));
//        offer.setDestinationLat(jsonObject.getDouble("destination_lat"));
//        offer.setDestinationLng(jsonObject.getDouble("destination_lng"));
//
//        offer.setCircleColor(Html.fromHtml(jsonObject.getString("status_name_circle_color")).toString());
//        offer.setNumberBoxes(jsonObject.getInt("box_num"));
//        offer.setNumberPallets(jsonObject.getInt("pallet_num"));
//        offer.setNumberTruckload(jsonObject.getInt("truckload_num"));
//
//        String specialRequestsIcons=jsonObject.getString("shipment_special_request");
//        if (!specialRequestsIcons.equals("null"))
//            offer.setSpecialRequest(Arrays.asList(specialRequestsIcons.split(",")));
//        offer.setPickupIn(jsonObject.getString("pickup_in_time"));
//        offer.setComments(jsonObject.getString("comments"));
//        String loads=jsonObject.getString("shipment_load");
//        if (!loads.equals("null"))
//            offer.setLoads(Arrays.asList(loads.split(",")));
//        offer.setLatLngPoints(jsonObject.getString("shipment_google_root"));
//        offer.setTruckloadName(jsonObject.getString("truck_type_name"));
//        return offer;
//    }

//    private Jobs fillJob(JSONObject jsonObject) throws JSONException {
//
//        Jobs jobs=new Jobs();
//        jobs.setId(jsonObject.getLong("id"));
//        jobs.setStatusId(jsonObject.getInt("status_id"));
//        jobs.setStatusName(Html.fromHtml(jsonObject.getString("status_name")).toString());
//
//        jobs.setOriginalPickupDate(jsonObject.getString("original_pickup_date"));
//        jobs.setPickupDate(jsonObject.getString("pickup_date"));
//        jobs.setPickupFromTime(jsonObject.getString("pickup_from_time"));
//        jobs.setPickupTillTime(jsonObject.getString("pickup_till_time"));
//
//        jobs.setDropoffDate(jsonObject.getString("dropoff_date"));
//        jobs.setDropoffFromTime(jsonObject.getString("dropoff_from_time"));
//        jobs.setDropoffTillTime(jsonObject.getString("dropoff_till_time"));
//
//        jobs.setOriginAddress(Html.fromHtml(jsonObject.getString("origin_address")).toString());
//        jobs.setDestinationAddress(Html.fromHtml(jsonObject.getString("destination_address")).toString());
//
//        jobs.setTotalLoadWeightText(jsonObject.getLong("total_load_weight"));
//        jobs.setShipmentShipperPaymentText(jsonObject.getString("shipment_carrier_payout_text"));
//        jobs.setTotalDrivingDistance(jsonObject.getString("total_driving_distance"));
//
//        jobs.setOriginLat(jsonObject.getDouble("origin_lat"));
//        jobs.setOriginLng(jsonObject.getDouble("origin_lng"));
//        jobs.setDestinationLat(jsonObject.getDouble("destination_lat"));
//        jobs.setDestinationLng(jsonObject.getDouble("destination_lng"));
//
//        jobs.setCircleColor(Html.fromHtml(jsonObject.getString("status_name_circle_color")).toString());
//        jobs.setNumberBoxes(jsonObject.getInt("box_num"));
//        jobs.setNumberPallets(jsonObject.getInt("pallet_num"));
//        jobs.setNumberTruckload(jsonObject.getInt("truckload_num"));
//        //jobs.setPickupIn(jsonObject.getString("pickup_in_time"));
//        jobs.setComments(jsonObject.getString("comments"));
//        String specialRequestsIcons=jsonObject.getString("shipment_special_request");
//        if (!specialRequestsIcons.equals("null")){
//            List<String> fullSpecials=Arrays.asList(specialRequestsIcons.split(","));
//            List<SpecialRequest> specialRequests=new ArrayList<>();
//            for (String specialsStr:fullSpecials){
//                int index=specialsStr.indexOf('[');
//                SpecialRequest specialRequest=new SpecialRequest();
//                if (index>=0){
//                    specialRequest.setImageResource(specialsStr.substring(0,index));
//                    specialRequest.setImageLoafFile(specialsStr.substring(index,(specialsStr.length())));
//                }
//                else {
//                    specialRequest.setImageResource(specialsStr);
//                    specialRequest.setImageLoafFile("");
//                }
//                specialRequests.add(specialRequest);
//            }
//            jobs.setSpecials(specialRequests);
//        }
//           // jobs.setSpecialRequest(Arrays.asList(specialRequestsIcons.split(",")));
//        String loads=jsonObject.getString("shipment_load");
//        if (!loads.equals("null"))
//            jobs.setLoads(Arrays.asList(loads.split(",")));
//
//        jobs.setOriginContactName(jsonObject.getString("origin_contact_name"));
//        jobs.setOriginContactPhone(jsonObject.getString("origin_contact_phone"));
//        jobs.setDestinationContactName(jsonObject.getString("destination_contact_name"));
//        jobs.setDestinationContactPhone(jsonObject.getString("destination_contact_phone"));
//
//        jobs.setPickupInTime(jsonObject.getString("pickup_in_time"));
//        jobs.setDropoffInTime(jsonObject.getString("dropoff_in_time"));
//        if (jsonObject.getString("shipment_complete_date").equals("null"))
//            jobs.setCompletedDate("");
//        else
//            jobs.setCompletedDate(jsonObject.getString("shipment_complete_date"));
//        jobs.setSignatureUrl(jsonObject.getString("signature_url"));
//        jobs.setBolUrl(jsonObject.getString("bol_url"));
//        jobs.setLatLngPoints(jsonObject.getString("shipment_google_root"));
//        jobs.setReceiverName(jsonObject.getString("signature_receiver_name"));
//        jobs.setTruckloadName(jsonObject.getString("truck_type_name"));
//        jobs.setShipperName(jsonObject.getString("shipper_name"));
//        jobs.setShipperPhone(jsonObject.getString("shipper_phone"));
//        return jobs;
//    }

}
