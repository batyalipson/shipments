package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 04/12/2016.
 */
public class UpdateAvailableStatusAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateAvailableStatus";

    Context context;
    IResultsInterface iResultsInterface;
    String fullUrl="";

    public UpdateAvailableStatusAsyncTask(Context context,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String availAbleStatus="&driver_application_status_id="+SharedPreferenceManager.getInstance(context).getAvailableStatus();
        fullUrl+=URL+userId+availAbleStatus;
        super.onPreExecute();
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {
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
}
