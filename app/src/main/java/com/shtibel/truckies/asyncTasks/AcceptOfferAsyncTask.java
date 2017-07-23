package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 10/08/2016.
 */
public class AcceptOfferAsyncTask extends AsyncTask<Void,Void,ResultEntity> {
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=acceptOffer";


    Context context;
    IResultsInterface iResultsInterface;
    long shipmentId;
    String fullUrl;

    public AcceptOfferAsyncTask(Context context,long shipmentId,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
        this.shipmentId=shipmentId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String carrierId="&carrier_id="+SharedPreferenceManager.getInstance(context).getCarrierId();
        String shipmentId="&shipment_id="+this.shipmentId;
        fullUrl=URL+userId+carrierId+shipmentId;

    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);
        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);
        else
            iResultsInterface.onErrorWithResult(resultEntity);
    }
}
