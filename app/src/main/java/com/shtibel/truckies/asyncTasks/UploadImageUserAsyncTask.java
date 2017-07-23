package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

import java.io.File;

/**
 * Created by Shtibel on 21/08/2016.
 */
public class UploadImageUserAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=uploadImageDriver&user_id=";

    Context context;
    File imageFile;
    String fileName;
    IResultsInterface iResultsInterface;

    public UploadImageUserAsyncTask(Context context,File imageFile,String fileName,IResultsInterface iResultsInterface){
        this.context=context;
        this.imageFile=imageFile;
        this.fileName=fileName;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        String fullUrl=URL+ SharedPreferenceManager.getInstance(context).getUserId();
        ResultEntity resultEntity= JsonParser.uploadImage(fullUrl,imageFile,fileName);

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
