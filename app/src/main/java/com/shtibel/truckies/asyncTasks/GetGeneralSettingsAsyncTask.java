package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 04/07/2016.
 */
public class GetGeneralSettingsAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=generalSettings";
   // private static final String URL2="http://truckies.shtibel.com/tr-api/?action=getStatusData";

    Context context;
    IResultsInterface iResultsInterface;

    public GetGeneralSettingsAsyncTask(Context context, IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL);
        //ResultEntity resultEntity2= JsonParser.requestGetOkHttp(URL2);
        //fillStatusesList(resultEntity2);

        return resultEntity;
    }

//    private void fillStatusesList(ResultEntity resultEntity) {
//
//        try {
//            JSONArray jsonArray=new JSONArray(resultEntity.getResult());
//
//            for (int i=0;i<jsonArray.length();i++) {
//                JSONObject jsonObject=jsonArray.getJSONObject(i);
//                long id=jsonObject.getLong("id");
//                String name=jsonObject.getString("status_name");
//
//                JobStatuses status=new JobStatuses(id,name);
//                ((StartApplication) context.getApplicationContext()).getStatuses().add(status);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);
        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);
        else
            iResultsInterface.onErrorWithResult(resultEntity);
    }
}
