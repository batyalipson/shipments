package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 26/02/2017.
 */
public class GetPasswordSMSAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getPasswordSMS";

    String phone;
    Context context;
    IResultsInterface iResultsInterface;

    public GetPasswordSMSAsyncTask(String phone,Context context,IResultsInterface iResultsInterface){
        this.phone=phone;
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+"&phone="+phone);
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
