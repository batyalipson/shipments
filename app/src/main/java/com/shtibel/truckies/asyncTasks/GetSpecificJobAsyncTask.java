package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 24/08/2016.
 */
public class GetSpecificJobAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getSpecificJob";

    long jobId;
    Context context;
    IResultsInterface iResultsInterface;

    public GetSpecificJobAsyncTask(long jobId,Context context,IResultsInterface iResultsInterface){
        this.jobId=jobId;
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        String userIdStr="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String carrierIdStr="&carrier_id="+SharedPreferenceManager.getInstance(context).getCarrierId();
        String offerIdStr="&job_id="+jobId;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL + userIdStr + carrierIdStr + offerIdStr);
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
