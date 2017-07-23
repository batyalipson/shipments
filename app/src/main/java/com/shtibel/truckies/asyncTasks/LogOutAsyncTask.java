package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 01/01/2017.
 */
public class LogOutAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=logOut";

    Context context;
    IResultsInterface iResultsInterface;

    public LogOutAsyncTask(Context context,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {

        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
       // String gcmId="&app_gcm_id="+"";
        String driverApplicationStatusId="&driver_application_status_id="+SharedPreferenceManager.AVAILABLE_STATUS_OFFLINE;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+userId+driverApplicationStatusId);

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
