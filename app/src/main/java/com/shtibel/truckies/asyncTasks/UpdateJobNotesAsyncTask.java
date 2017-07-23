package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 11/01/2017.
 */
public class UpdateJobNotesAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateDriverNotes";

    Context context;
    long jobId;
    String notes;
    IResultsInterface iResultsInterface;

    String fullUrl="";

    public UpdateJobNotesAsyncTask(Context context,long jobId,String notes,IResultsInterface iResultsInterface){
        this.context=context;
        this.jobId=jobId;
        this.notes=notes;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        String userIdStr= "&user_id="+SharedPreferenceManager.getInstance(context).getUserId();
        String shipmentIdStr="&shipment_id="+jobId;
        String notesStr= "&notes="+notes;

        fullUrl+=URL+userIdStr+shipmentIdStr+notesStr;
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {
        ResultEntity resultEntity=JsonParser.requestGetOkHttp(fullUrl);
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
