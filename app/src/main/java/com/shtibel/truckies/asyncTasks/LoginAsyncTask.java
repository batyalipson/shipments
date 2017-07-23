package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONObject;

/**
 * Created by Shtibel on 23/06/2016.
 */
public class LoginAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=login";
    String phone;
    String password;
    IResultsInterface iResultsInterface;
    Context context;
    Utils utils=new Utils();

    public LoginAsyncTask(String phone, String password, Context context, IResultsInterface iResultsInterface){
        this.phone=phone;
        this.password=password;
        this.context=context;
        this.iResultsInterface=iResultsInterface;

    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity=null;
        JSONObject jsonObject=new JSONObject();
        String uniqueDeviceNumber="&unique_device_number="+utils.getUniqueNumber(context);
     //   try {
          //  jsonObject.put("email",email);
          //  jsonObject.put("password",password);
          //  resultEntity=JsonParser.requestPostOkHttp(URL,jsonObject.toString());
            resultEntity= JsonParser.requestGetOkHttp(URL + "&phone=" + phone + "&password=" + password+uniqueDeviceNumber);

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        iResultsInterface.onCompleteWithResult(resultEntity);
    }
}
