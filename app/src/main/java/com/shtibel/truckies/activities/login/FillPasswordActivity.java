package com.shtibel.truckies.activities.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.alertDialog.AlertDialogActivity;
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.activities.profileImage.ProfileImageActivity;
import com.shtibel.truckies.asyncTasks.GetLastVersionAsyncTask;
import com.shtibel.truckies.asyncTasks.GetStatusesAsyncTask;
import com.shtibel.truckies.asyncTasks.LoginAsyncTask;
import com.shtibel.truckies.asyncTasks.SendTokenGcmAsyncTask;
import com.shtibel.truckies.generalClasses.DBHelper;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.pushNotifications.BadgeUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class FillPasswordActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    EditText passwordEditTxt;
    ImageView backgroundImage;
    Utils utils=new Utils();
    int imageRes;
    ProgressBar sendProgress;

    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_password);

        imageRes=getIntent().getIntExtra("imageBgRes",0);
        phone=getIntent().getStringExtra("phone");
        initViews();
    }

    private void initViews() {
        passwordEditTxt= (EditText) findViewById(R.id.login_fp_password);
//        addPhoneListeners();
        backgroundImage= (ImageView) findViewById(R.id.login_fp_image);
        backgroundImage.setImageResource(imageRes);
        sendProgress= (ProgressBar) findViewById(R.id.login_fp_progress);
    }

    public void login(View view){

        final String passwordStr= passwordEditTxt.getText().toString();

        if (passwordStr.equals(""))
            passwordEditTxt.setError(getString(R.string.required_field));
        else {
            passwordEditTxt.setError(null);

            sendProgress.setVisibility(View.VISIBLE);
            final LoginAsyncTask loginAsyncTask = new LoginAsyncTask(phone, passwordStr, this, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    sendProgress.setVisibility(View.GONE);
                    String errorType = "";
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        errorType = jsonObject.getString("errType");
                        if (errorType.equals("")) {

                            //String passwordStr=jsonObject.getString("password");
                            fillAndSaveData(jsonObject, passwordStr);
                            if (permissionLocationMissed())
                                goToDashboard();
                        } else {
                            utils.openPopupWindow(FillPasswordActivity.this,
                                    true,
                                    getString(R.string.notice),
                                    getString(R.string.password_error_txt),
                                    getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        utils.showServerError(FillPasswordActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onErrorWithResult(ResultEntity resultEntity) {
                    sendProgress.setVisibility(View.GONE);
                    utils.showServerError(FillPasswordActivity.this, resultEntity.getResult());
                }
            });
            loginAsyncTask.execute();
        }

    }

    private boolean permissionLocationMissed() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToDashboard();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    private void fillAndSaveData(JSONObject jsonObject,String passwordStr) throws JSONException {

        long userId=jsonObject.getLong("user_id");
        String userName=jsonObject.getString("user_first_name")+" "+jsonObject.getString("user_last_name");
        String firstName=jsonObject.getString("user_first_name");
        String lastName=jsonObject.getString("user_last_name");
        String phone=jsonObject.getString("phone");
        String email=jsonObject.getString("user_email");
        String password=passwordStr;
        String truckType=jsonObject.getString("user_type_name");
        long carrierId=jsonObject.getLong("carrier_id");
        int canAcceptOffers=jsonObject.getInt("can_accept_offers");
        int canSeePrice=jsonObject.getInt("can_see_price");
        int canSeeOffers=jsonObject.getInt("can_see_offers");
        long truckId=jsonObject.getLong("truck_type_id");
        String userImage=jsonObject.getString("user_image");
        int statusId=jsonObject.getInt("user_status");


        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserId(userId);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserName(userName);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserFirstName(firstName);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserLastName(lastName);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserPhone(phone);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUseEmail(email);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserPassword(password);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserTruckType(truckType);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveCarrierId(carrierId);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveCanAcceptOffers(canAcceptOffers);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveCanSeePrice(canSeePrice);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveCanSeeOffers(canSeeOffers);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveTruckType(truckId);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserImageUrl(userImage);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveIsLogin(true);
        SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveUserStatus(statusId);
        //SharedPreferenceManager.getInstance(LoginActivity.this).savePassword(passwordStr);

    }

    private void goToDashboard(){

        sendToken();
        getStatuses();
        getLastVersion();

        BadgeUtils.setBadge(this, DBHelper.getDB(this)
                .getNotOpenedNotifications(SharedPreferenceManager.getInstance(this).getUserId()));
        if (SharedPreferenceManager.getInstance(this).isFirstTime()){

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Intent i = new Intent(this, AlertDialogActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            else {

                SharedPreferenceManager.getInstance(this).saveIsFirstTime(false);
                Intent intent = new Intent();
                intent.putExtra("imageBgRes", imageRes);
                intent.setClass(this, ProfileImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                FillPasswordActivity.this.finish();
            }
        }
        else {
            final Intent intent = new Intent();
            intent.setClass(FillPasswordActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                    FillPasswordActivity.this.finish();
                }
            }, 1000);


        }
    }

    private void sendToken() {
        SendTokenGcmAsyncTask sendTokenGcmAsyncTask=new SendTokenGcmAsyncTask(this);
        sendTokenGcmAsyncTask.execute();
    }

    public void getStatuses() {
        GetStatusesAsyncTask getStatusesAsyncTask=new GetStatusesAsyncTask(this);
        getStatusesAsyncTask.execute();
    }
    private void getLastVersion() {

        GetLastVersionAsyncTask getLastVersionAsyncTask=new GetLastVersionAsyncTask(this, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                try {
                    JSONObject jsonObject=new JSONObject(result.getResult());
                    String lastVersion=jsonObject.getString("lastVersion");
                    String lastTestingVersion=jsonObject.getString("lastTestingVersion");
                    SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveLastVersion(lastVersion);
                    SharedPreferenceManager.getInstance(FillPasswordActivity.this).saveLastTestingVersion(lastTestingVersion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                utils.showServerError(FillPasswordActivity.this, resultEntity.getResult());
            }
        });

        getLastVersionAsyncTask.execute();

    }

//    private void addPhoneListeners() {
//
//        phoneEditTxt.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
//
//            private boolean backspacingFlag = false;
//            private boolean editedFlag = false;
//            private int cursorComplement;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                cursorComplement = s.length()-phoneEditTxt.getSelectionStart();
//                if (count > after) {
//                    backspacingFlag = true;
//                } else {
//                    backspacingFlag = false;
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // nothing to do here =D
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String string = s.toString();
//                String phoneS = string.replaceAll("[^\\d]", "");
//                if (!editedFlag) {
//
//                    if (phoneS.length() >= 6 && !backspacingFlag) {
//                        editedFlag = true;
////                        String ans = "(" + phoneS.substring(0,2) + ") " + phoneS.substring(2,6) + "-" + phoneS.substring(6);
//                        String ans = phoneS.substring(0,2) + "-" + phoneS.substring(2,6) + "-" + phoneS.substring(6);
//                        phoneEditTxt.setText(ans);
//                        phoneEditTxt.setSelection(phoneEditTxt.getText().length()-cursorComplement);
//                    }
//                    else if (phoneS.length() >= 2 && !backspacingFlag) {
//                        editedFlag = true;
////                        String ans = "(" +phoneS.substring(0, 2) + ") " + phoneS.substring(2);
//                        String ans = phoneS.substring(0, 2) + "-" + phoneS.substring(2);
//                        phoneEditTxt.setText(ans);
//                        phoneEditTxt.setSelection(phoneEditTxt.getText().length()-cursorComplement);
//                    }
//                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
//                } else {
//                    editedFlag = false;
//                }
//
//            }
//        });
//
//    }

    public void openSite(View view){
        String url = StartApplication.START_URL+"/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
//    public void getNewPassword(View view){
//        String phone=phoneEditTxt.getText().toString().replaceAll("[^\\d]", "");
//
//        if (phone.length()<10&&phone.length()>0){
//            utils.openPopupWindow(this,
//                    true,
//                    getString(R.string.notice),
//                    getString(R.string.phone_format_error),
//                    getString(R.string.ok),
//                    "",
//                    null,
//                    new PopupInterface() {
//                    });
//        }
//        else if (phone.equals("")) {
//            phoneEditTxt.setError(getString(R.string.required_field));
////            utils.openPopupWindow(this,
////                    true,
////                    getString(R.string.login_error),
////                    getString(R.string.login_empty_details),
////                    getResources().getString(R.string.ok),
////                    "",
////                    null,
////                    new PopupInterface(){});
//        }
//        else {
//            sendProgress.setVisibility(View.VISIBLE);
//            GetNewPasswordAsyncTask getNewPasswordAsyncTask=new GetNewPasswordAsyncTask(this, phone, new IResultsInterface() {
//                @Override
//                public void onCompleteWithResult(ResultEntity result) {
//                    try {
//                        sendProgress.setVisibility(View.GONE);
//                        JSONObject jsonObject=new JSONObject(result.getResult());
//                        boolean err=jsonObject.getBoolean("err");
//                        if (err){
//                            utils.openPopupWindow(FillPasswordActivity.this,
//                                    true,
//                                    getString(R.string.notice),
//                                    getString(R.string.forgot_password_error_txt),
//                                    getString(R.string.ok),
//                                    "",
//                                    null,
//                                    new PopupInterface() {
//                                    });
//                        }
//                        else {
//                            final String password = jsonObject.getString("password");
//
//                            utils.openPopupWindow(FillPasswordActivity.this,
//                                    true,
//                                    getString(R.string.notice),
//                                    getString(R.string.new_password_message),
//                                    getResources().getString(R.string.ok),
//                                    "",
//                                    null,
//                                    new PopupInterface() {
//
//                                        @Override
//                                        public void onOk() {
//                                            Intent returnIntent = new Intent();
//                                            returnIntent.putExtra("password", password);
//                                            setResult(Activity.RESULT_OK, returnIntent);
//                                            finish();
//                                        }
//                                    });
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        utils.showServerError(FillPasswordActivity.this, e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onErrorWithResult(ResultEntity resultEntity) {
//                    sendProgress.setVisibility(View.GONE);
//                    utils.showServerError(FillPasswordActivity.this,resultEntity.getResult());
//                }
//            });
//
//            getNewPasswordAsyncTask.execute();
//
//
//        }
//    }

    public void goBack(View view){
        finish();
    }
}
