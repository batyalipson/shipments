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
 * Created by Shtibel on 11/01/2017.
 */
public class UploadJobImagesAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=uploadJobImages";

    File imageFile;
    long shipmentId;
    Context context;
    String fileName;
    IResultsInterface iResultsInterface;

    String fullUrl;

    public UploadJobImagesAsyncTask(File imageFile,long shipmentId,Context context,String fileName,IResultsInterface iResultsInterface){
        this.imageFile=imageFile;
        this.shipmentId=shipmentId;
        this.context=context;
        this.fileName=fileName;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        initUrl();
    }

    private void initUrl() {
        String userIdStr="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String shipmentIdStr="&shipment_id="+shipmentId;
        String fileTypeId="&file_type_id=1";

        fullUrl=URL+userIdStr+shipmentIdStr+fileTypeId;
    }


    @Override
    protected ResultEntity doInBackground(Void... params) {
        ResultEntity resultEntity= JsonParser.uploadImage(fullUrl, imageFile, fileName);
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
