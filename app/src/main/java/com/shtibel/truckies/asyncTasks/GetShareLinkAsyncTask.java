package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 05/01/2017.
 */
public class GetShareLinkAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getShareLink";

    Context context;
    long shipmentId;
    IResultsInterface iResultsInterface;

    public GetShareLinkAsyncTask(Context context,long shipmentId,IResultsInterface iResultsInterface){
        this.context=context;
        this.shipmentId=shipmentId;
        this.iResultsInterface=iResultsInterface;
    }


    @Override
    protected ResultEntity doInBackground(Void... params) {

        String userIdStr= "&user_id="+SharedPreferenceManager.getInstance(context).getUserId();
        String shipmentIdStr="&shipment_id="+shipmentId;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+userIdStr+shipmentIdStr);


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
}
