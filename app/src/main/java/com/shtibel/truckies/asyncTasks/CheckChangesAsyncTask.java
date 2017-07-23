package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 02/01/2017.
 */
public class CheckChangesAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=" +
            "" +
            "";
    String fullUrl;
    Context context;
    IResultsInterface iResultsInterface;
    Utils utils=new Utils();

    public CheckChangesAsyncTask(Context context,IResultsInterface iResultsInterface){
        this.context=context;
        this.iResultsInterface=iResultsInterface;
    }

    @Override
    protected void onPreExecute() {

        String userId="&user_id="+SharedPreferenceManager.getInstance(context).getUserId();
        String password="&password="+ SharedPreferenceManager.getInstance(context).getUserPassword();
        String statusId="&status_id="+SharedPreferenceManager.getInstance(context).getUserStatus();
        String carrierId="&carrier_id="+SharedPreferenceManager.getInstance(context).getCarrierId();
        String canAcceptOffers="&can_accept_offers="+SharedPreferenceManager.getInstance(context).getCanAcceptOffers();
        String canSeePrice="&can_see_price="+SharedPreferenceManager.getInstance(context).getCanSeePrice();
        String canSeeOffers="&can_see_offers="+SharedPreferenceManager.getInstance(context).getCanSeeOffers();
        String uniqueDeviceNumber="&unique_device_number="+utils.getUniqueNumber(context);

        fullUrl=URL+userId+password+statusId+carrierId+canAcceptOffers+canSeePrice+canSeeOffers+uniqueDeviceNumber;
        super.onPreExecute();
    }

    @Override
    protected ResultEntity doInBackground(Void... params) {

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {

        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);

        super.onPostExecute(resultEntity);
    }
//    private String getUniqueNumber(Context context){
//        String android_id = Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//
//        return android_id;
//
//    }
}
