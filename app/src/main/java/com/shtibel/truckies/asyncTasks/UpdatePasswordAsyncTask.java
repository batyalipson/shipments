package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 21/08/2016.
 */
public class UpdatePasswordAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateUserPassword";
    Context context;
    String password;
    IResultsInterface iResultsInterface;
    String fullUrl;

    public UpdatePasswordAsyncTask(Context context,String password,IResultsInterface iResultsInterface)
    {
        this.context=context;
        this.password=password;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String passwordStr="&password="+password;
        fullUrl=URL+userId+passwordStr;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
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
