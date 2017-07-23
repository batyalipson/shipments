package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 07/08/2016.
 */
public class GetOffersDataAsyncTask extends AsyncTask<Void,Void,ResultEntity> {
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=offers&user_id=";
    Context context;
    IResultsInterface iResultsInterface;

    public GetOffersDataAsyncTask(Context context, IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        long userId= SharedPreferenceManager.getInstance(context).getUserId();
        long carrierId=SharedPreferenceManager.getInstance(context).getCarrierId();
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL + userId+"&carrier_id="+carrierId);
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
