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
public class GetRatingAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=getRating&user_id=";
    Context context;
    IResultsInterface iResultsInterface;

    public GetRatingAsyncTask(Context context,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        long userId= SharedPreferenceManager.getInstance(context).getUserId();
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+userId);

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
