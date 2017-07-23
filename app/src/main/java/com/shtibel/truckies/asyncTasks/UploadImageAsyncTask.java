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
 * Created by Shtibel on 14/07/2016.
 */
public class UploadImageAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    //http://truckies.shtibel.com/tr-api/?action=uploadImage&user_id=12&shipment_id=149&image_type=bol
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=uploadImage";

    File imageFile;
    long shipmentId;
    String imageType;
    Context context;
    String fileName;
    String receiverName;
    IResultsInterface iResultsInterface;
    String fullUrl;
    public UploadImageAsyncTask(File imageFile, Context context, long shipmentId, String imageType,String fileName,String receiverName, IResultsInterface iResultsInterface){

        this.imageFile=imageFile;
        this.shipmentId=shipmentId;
        this.imageType=imageType;
        this.context=context;
        this.fileName=fileName;
        this.receiverName=receiverName;
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
        String imageTypeStr="&image_type="+imageType;
        String receiverNameStr="";
        if (imageType.equals("signature"))
            receiverNameStr="&receiver_name="+receiverName;

        fullUrl=URL+userIdStr+shipmentIdStr+imageTypeStr+receiverNameStr;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        ResultEntity resultEntity= JsonParser.uploadImage(fullUrl, imageFile,fileName);

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);

        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);
        else
            iResultsInterface.onErrorWithResult(resultEntity);
        //Log.e("load_kg image status",resultEntity.getIsOk()+" "+resultEntity.getResult());

    }
}
