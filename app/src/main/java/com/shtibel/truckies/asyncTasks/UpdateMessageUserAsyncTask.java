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

/**
 * Created by Shtibel on 23/08/2016.
 */
public class UpdateMessageUserAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

  //  http://truckies.shtibel.com/tr-api/?action=updateMessageUser&user_id=12&message_id=​21

    private static final String URL = StartApplication.START_URL+"/tr-api/?action=updateMessageUser";
    Context context;
    long messageId;

    public UpdateMessageUserAsyncTask(Context context,long messageId){
        this.context=context;
        this.messageId=messageId;
    }
    
    @Override
    protected ResultEntity doInBackground(Void... voids) {

        String userIdStr="&user_id="+ SharedPreferenceManager.getInstance(context).getUserId();
        String messageIdStr="&message_id="+messageId;

        ResultEntity resultEntity= JsonParser.requestGetOkHttp(URL+userIdStr+messageIdStr);

        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);
        if (!resultEntity.getIsOk()){
            Utils utils=new Utils();
           // utils.showInfoPopup(context.getString(R.string.server_error),resultEntity.getResult(),context);
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
