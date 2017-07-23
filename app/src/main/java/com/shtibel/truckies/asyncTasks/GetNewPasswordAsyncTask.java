package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 29/08/2016.
 */
public class GetNewPasswordAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getPassword";
    Context context;
    String phone;
    IResultsInterface iResultsInterface;

    public GetNewPasswordAsyncTask(Context context,String phone,IResultsInterface iResultsInterface){
        this.context=context;
        this.phone=phone;
        this.iResultsInterface=iResultsInterface;
    }


    @Override
    protected ResultEntity doInBackground(Void... voids) {

        String phoneStr="&phone="+phone;
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+phoneStr);
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
