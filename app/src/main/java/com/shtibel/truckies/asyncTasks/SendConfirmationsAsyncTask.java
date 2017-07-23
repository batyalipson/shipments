package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.job.LoadType;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

import java.io.File;
import java.util.List;

/**
 * Created by Shtibel on 20/02/2017.
 */
public class SendConfirmationsAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=saveConfirmations";



    Context context;
    String type;
    int totalWeight;
    String senderName;
    String confirmationPhone;
    long jobId;
    File signature;
    List<LoadType> loadTypes;
    IResultsInterface iResultsInterface;
    String fullUrl="";


    public SendConfirmationsAsyncTask(Context context,String type,int totalWeight,String senderName,String confirmationPhone,long jobId,File signature,List<LoadType> loadTypes,IResultsInterface iResultsInterface){
        this.context=context;
        this.type=type;
        this.totalWeight=totalWeight;
        this.senderName=senderName;
        this.confirmationPhone=confirmationPhone;
        this.jobId=jobId;
        this.signature=signature;
        this.loadTypes=loadTypes;
        this.iResultsInterface=iResultsInterface;
    }

    private void buildPath(){
        String typeStr="&type="+type;
        String totalWeightStr="&total_weight="+totalWeight;
        String senderNameStr="&sender_name="+senderName;
        String confirmationPhoneStr="&confirmation_phone="+confirmationPhone;
        String jobIdStr="&shipment_id="+jobId;
        String userId="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String quantitiesStr="";
        for (int i=0;i<loadTypes.size();i++){
            LoadType loadType=loadTypes.get(i);
            if (loadType.getQuantity()>0)
            {
                quantitiesStr+="&quantity_"+loadType.getId()+"="+loadType.getQuantity();
            }
        }

        fullUrl+=typeStr+totalWeightStr+senderNameStr+confirmationPhoneStr+jobIdStr+userId+quantitiesStr;

    }

    @Override
    protected ResultEntity doInBackground(Void... params) {
        buildPath();
        String fileName;
        if (type.equals("pickup"))
            fileName="pickup_signature"+jobId+".png";
        else
            fileName="dropoff_signature"+jobId+".png";

        ResultEntity resultEntity= JsonParser.uploadImage(URL+fullUrl, signature, fileName);

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
