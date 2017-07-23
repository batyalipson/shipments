package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;


/**
 * Created by Shtibel on 02/05/2016.
 */
public class GetPathAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    Context context;
    String url;
    IResultsInterface iResultsInterface;

    public GetPathAsyncTask(Context context, String url, IResultsInterface iResultsInterface){
        this.context=context;
        this.url=url;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(url);
//        ResultEntity resultEntity1=JsonParser.requestPostOkHttp(url,"");
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {

        if (resultEntity.getIsOk()){
//            Toast.makeText(context,resultEntity.getResult(),Toast.LENGTH_LONG).show();
            Log.d("path",resultEntity.getResult());
            iResultsInterface.onCompleteWithResult(resultEntity);
        }
        else
            iResultsInterface.onCompleteWithResult(resultEntity);
        super.onPostExecute(resultEntity);
    }
}
