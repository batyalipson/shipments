package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.JobStatuses;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shtibel on 15/08/2016.
 */
public class GetStatusesAsyncTask extends AsyncTask<Void,Void,ResultEntity>{

    private static final String URL=StartApplication.START_URL+"/tr-api/?action=getStatusData";

    Context context;

    public GetStatusesAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL);
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);

        if (resultEntity.getIsOk())
            fillStatusesList(resultEntity);
    }

    private void fillStatusesList(ResultEntity resultEntity) {

        try {
            JSONArray jsonArray=new JSONArray(resultEntity.getResult());
            ((StartApplication) context.getApplicationContext()).getStatuses().clear();
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                long id=jsonObject.getLong("id");
                String name=jsonObject.getString("status_name");

                JobStatuses status=new JobStatuses(id,name);
                ((StartApplication) context.getApplicationContext()).getStatuses().add(status);

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
