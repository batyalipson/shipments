package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 15/12/2016.
 */
public class SendExceptionAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=sendEmail";
    private String urlParameters="";
    Context context;
    String exception;

    public SendExceptionAsyncTask(Context context,String exception){
        this.context=context;
        this.exception=exception;
        urlParameters+="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        urlParameters+="&email_from="+ SharedPreferenceManager.getInstance(context).getUserEmail();
        urlParameters+="&email_body="+ exception;
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+urlParameters);
        return resultEntity;
    }
}
