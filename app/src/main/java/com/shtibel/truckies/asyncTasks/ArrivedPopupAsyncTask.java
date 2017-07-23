package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 05/07/2017.
 */
public class ArrivedPopupAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=arrivedPickupShow";

    Context context;
    long jobId;
    String fullUrl="";

    public ArrivedPopupAsyncTask(Context context,long jobId){
        this.context=context;
        this.jobId=jobId;
        fullUrl=URL+ "&user_id="+ SharedPreferenceManager.getInstance(context).getUserId()+
                "&shipment_id="+jobId;
    }



    @Override
    protected ResultEntity doInBackground(Void... params) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
        return resultEntity;
    }

}
