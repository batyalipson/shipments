package com.shtibel.truckies.alertDialogActivity;

import android.app.Activity;
import android.os.Bundle;

import com.shtibel.truckies.R;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.Utils;

/**
 * Created by Shtibel on 13/07/2016.
 */
public class AlertDialogActivity extends Activity
{

    Utils utils=new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String title=getIntent().getStringExtra("title");
        String message=getIntent().getStringExtra("message");

        utils.openPopupWindow(this, true, title, message, getString(R.string.ok), "", null, new PopupInterface() {});

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//
//        alertDialog.setTitle(title);
//        alertDialog.setMessage(message);
//
//        // On pressing the Settings button.
//        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                AlertDialogActivity.this.finish();
//
//            }
//        });
//
//        alertDialog.setCancelable(false);
//        alertDialog.show();
    }
}