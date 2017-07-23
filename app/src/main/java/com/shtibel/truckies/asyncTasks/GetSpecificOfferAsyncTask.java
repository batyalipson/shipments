package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 23/08/2016.
 */
public class GetSpecificOfferAsyncTask extends AsyncTask<Void,Void,ResultEntity> {
    //http://truckies.shtibel.com/tr-api/?action=getSpecificOffer&user_id=12&carrier_id=4&offer_id=238

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getSpecificOffer";

    long offerId;
    Context context;
    IResultsInterface iResultsInterface;

    public GetSpecificOfferAsyncTask(long offerId,Context context,IResultsInterface iResultsInterface){
        this.offerId=offerId;
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        String userIdStr="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String carrierIdStr="&carrier_id="+SharedPreferenceManager.getInstance(context).getCarrierId();
        String offerIdStr="&offer_id="+offerId;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+userIdStr+carrierIdStr+offerIdStr);
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
