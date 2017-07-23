package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shtibel on 17/08/2016.
 */
public class UpdateUserDetailsAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    private static final String URL= StartApplication.START_URL+"/tr-api/?action=updateUserDetails&user_id=";

    Context context;

    public UpdateUserDetailsAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+ SharedPreferenceManager.getInstance(context).getUserId());

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);

        if (resultEntity.getIsOk()){
            try {
                JSONObject jsonObject=new JSONObject(resultEntity.getResult());
                int canSeePrice=jsonObject.getInt("can_see_price");
                SharedPreferenceManager.getInstance(context).saveCanSeePrice(canSeePrice);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            Utils utils=new Utils();
            //utils.showInfoPopup(context.getString(R.string.server_error),resultEntity.getResult(),StartApplication.getAppContext());
            utils.openPopupWindow(context,
                    true,
                    context.getString(R.string.server_error),
                    resultEntity.getResult(),
                    context.getString(R.string.ok),
                    "",
                    null,
                    new PopupInterface() {});
        }
    }
}
