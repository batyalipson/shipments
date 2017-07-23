package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;


/**
 * Created by Shtibel on 13/07/2016.
 */
public class UpdateJobStatus extends AsyncTask<Void,Void,ResultEntity> {
//    &user_id=11&status_id=11&shipment_id=151
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateShipmentSatus";

    Context context;
    long shipmentId;
    int status;
    String fullUrl;
    IResultsInterface iResultsInterface;


    public UpdateJobStatus(Context context, long shipmentId, int status, IResultsInterface iResultsInterface){
        this.context=context;
        this.shipmentId=shipmentId;
        this.status=status;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        String userIdStr="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String statusIdStr="&status_id="+status;
        String shipmentIdStr="&shipment_id="+shipmentId;

        fullUrl=URL+userIdStr+statusIdStr+shipmentIdStr;

    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);
        else
            iResultsInterface.onErrorWithResult(resultEntity);
        super.onPostExecute(resultEntity);
    }

//    public String getTimeOfWay() {
//        String googleUrl="https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                "origins=" +origin.latitude+","+origin.longitude+
//                "&destinations=" +destination.latitude+","+origin.longitude+
//                "&mode=driving"+
//                "&key=AIzaSyAKJvVBiZFJjGTdb6CbJNqMnpBEE-ktmIo";
//
//        ResultEntity resultEntity= JsonParser.requestGetOkHttp(googleUrl);
//        if (resultEntity.getIsOk()) {
//            try {
//                JSONObject jsonObject = new JSONObject(resultEntity.getResult());
//                JSONArray rowsArray = jsonObject.getJSONArray("rows");
//                JSONArray elementsArray = rowsArray.getJSONObject(0).getJSONArray("elements");
//                JSONObject elementObj = elementsArray.getJSONObject(0);
//                JSONObject duration = elementObj.getJSONObject("duration");
//
//                return  duration.getString("text");
//
//
//            } catch (JSONException e) {
//               return "";
//            }
//        }
//        else
//            return "";
//
//    }
}
