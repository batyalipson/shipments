package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 09/03/2017.
 */
public class GooglePlayDetailsAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    Context context;
    IResultsInterface iResultsInterface;


    public GooglePlayDetailsAsyncTask(Context context,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp("https://play.google.com/store/apps/details?id=" +context.getPackageName() + "&hl=en");
        //ResultEntity resultEntity=JsonParser.requestGetOkHttp("https://play.google.com/store/apps/details?id=com.shtibel.truckiez&hl=en");
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);

        Log.d("play store",resultEntity.getResult());

    }
}
