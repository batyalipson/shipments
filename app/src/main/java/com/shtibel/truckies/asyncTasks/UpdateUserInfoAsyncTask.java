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
public class UpdateUserInfoAsyncTask extends AsyncTask<Void,Void,ResultEntity> {
    //&user_id=12&first_name=michal&last_name=zak&email=michal@shtibel.com&phone=0500000005&truck_type=3
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateUserAccount";

    Context context;
    IResultsInterface iResultsInterface;
    String firstName;
    String lastName;
    String email;
    String phone;
    long truckTypeId;

    String fullUrl;

    public UpdateUserInfoAsyncTask
            (Context context,String firstName,String lastName, String email,
             String phone,long truckTypeId,IResultsInterface iResultsInterface){

        this.context=context;
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.phone=phone;
        this.truckTypeId=truckTypeId;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String userId="&user_id="+SharedPreferenceManager.getInstance(context).getUserId();
        String firstNameStr="&first_name="+firstName;
        String lastNameStr="&last_name="+lastName;
        String emailStr="&email="+email;
        String phoneStr="&phone="+phone;
        String truckTypeIdStr="&truck_type="+truckTypeId;

        fullUrl=URL+userId+firstNameStr+lastNameStr+emailStr+phoneStr+truckTypeIdStr;

    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
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
