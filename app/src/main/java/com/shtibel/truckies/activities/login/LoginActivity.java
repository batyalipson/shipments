package com.shtibel.truckies.activities.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.asyncTasks.GetGeneralSettingsAsyncTask;
import com.shtibel.truckies.asyncTasks.GetLastVersionAsyncTask;
import com.shtibel.truckies.asyncTasks.GetPasswordSMSAsyncTask;
import com.shtibel.truckies.asyncTasks.GetStatusesAsyncTask;
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

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private static final int RESULT_ID=1111;
    private static final int RESULT_TERMS=2222;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    EditText phone;
//    EditText password;
    LinearLayout termsLayout;
    AppCompatCheckBox loginAgreeTerms;
    TextView loginAgreeTermsText;
    Utils utils=new Utils();
//    String passwordStr;
    ImageView backgroundImage;
    ImageView topLogo;
    ImageView centerLogo;
    int resID=0;
    LinearLayout loginDetails;
    ProgressBar loginProgress;
    View.OnClickListener agreeTermsClickListener;
    String termsText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        GooglePlayDetailsAsyncTask googlePlayDetailsAsyncTask=new GooglePlayDetailsAsyncTask(this,new IResultsInterface(){
//
//            @Override
//            public void onCompleteWithResult(ResultEntity result) {
//
//            }
//        });
//
//        googlePlayDetailsAsyncTask.execute();

        initViews();

        if(SharedPreferenceManager.getInstance(this).getIsLogin()){
                if (permissionLocationMissed())
                    goToDashboard();
        }
        getGeneralSettings();

    }


    private void initViews() {

        phone= (EditText) findViewById(R.id.login_phone);
        addPhoneListeners();
       // email.setError("aaaa");
//        password= (EditText) findViewById(R.id.login_password);
        termsLayout= (LinearLayout) findViewById(R.id.login_agree_terms_layout);
        loginAgreeTerms= (AppCompatCheckBox) findViewById(R.id.login_agree_terms);
        loginAgreeTermsText= (TextView) findViewById(R.id.login_agree_terms_link);
        backgroundImage= (ImageView) findViewById(R.id.login_image);
        loginDetails= (LinearLayout) findViewById(R.id.login_details);
        topLogo= (ImageView) findViewById(R.id.login_logo_top);
        centerLogo= (ImageView) findViewById(R.id.login_logo_center);

        if (SharedPreferenceManager.getInstance(this).getIsLogin()) {
            loginDetails.setVisibility(View.GONE);
            topLogo.setVisibility(View.GONE);
        }
        else {
            phone.setText(SharedPreferenceManager.getInstance(this).getUserPhone());
//            password.setText(SharedPreferenceManager.getInstance(this).getUserPassword());
            centerLogo.setVisibility(View.GONE);
        }
        TypedArray splash=null;
        splash=getResources().obtainTypedArray(R.array.splash);
        Random rand = new Random();
        int rndInt = rand.nextInt(splash.length());
        resID = splash.getResourceId(rndInt, 0);
        backgroundImage.setImageResource(resID);

        loginProgress= (ProgressBar) findViewById(R.id.login_progress);

        //SharedPreferenceManager.getInstance(this).saveIsFirstTime(true);
        if (!SharedPreferenceManager.getInstance(this).isFirstTime()){
            loginAgreeTerms.setChecked(true);
            termsLayout.setVisibility(View.GONE);
        }

        agreeTermsClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(LoginActivity.this,AgreeTermsActivity.class);
                intent.putExtra("agreeCheckbox", loginAgreeTerms.isChecked());
                intent.putExtra("imageBgRes", resID);
                intent.putExtra("termsText",termsText);
                startActivityForResult(intent,RESULT_TERMS);
            }
        };
        //loginAgreeTermsText.setOnClickListener();
    }

    private void addPhoneListeners() {

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length()-phone.getSelectionStart();
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here =D
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                String phoneS = string.replaceAll("[^\\d]", "");
                if (!editedFlag) {

                    if (phoneS.length() >= 6 && !backspacingFlag) {
                        editedFlag = true;
//                        String ans = "(" + phoneS.substring(0,2) + ") " + phoneS.substring(2,6) + "-" + phoneS.substring(6);
                        String ans = phoneS.substring(0,2) + "-" + phoneS.substring(2,6) + "-" + phoneS.substring(6);
                        phone.setText(ans);
                        phone.setSelection(phone.getText().length()-cursorComplement);
                    }
                    else if (phoneS.length() >= 2 && !backspacingFlag) {
                        editedFlag = true;
//                        String ans = "(" +phoneS.substring(0, 2) + ") " + phoneS.substring(2);
                        String ans = phoneS.substring(0, 2) + "-" + phoneS.substring(2);
                        phone.setText(ans);
                        phone.setSelection(phone.getText().length()-cursorComplement);
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false;
                }

            }
        });

    }

    private void sendToken() {

        SendTokenGcmAsyncTask sendTokenGcmAsyncTask=new SendTokenGcmAsyncTask(this);
        sendTokenGcmAsyncTask.execute();

    }

    public void login(View view){

        final String phoneStr= phone.getText().toString().replaceAll("[^\\d]", "");
//        passwordStr=password.getText().toString();

        if (phoneStr.length()<10&&phoneStr.length()>0){
            utils.openPopupWindow(this,
                    true,
                    getString(R.string.notice),
                    getString(R.string.phone_format_error),
                    getString(R.string.ok),
                    "",
                    null,
                    new PopupInterface() {
                    });
        }
        else if (phoneStr.equals("")||!loginAgreeTerms.isChecked()){

            if (phoneStr.equals(""))
                phone.setError(getString(R.string.required_field));
            else
                phone.setError(null);
//            if (passwordStr.equals(""))
//                password.setError(getString(R.string.required_field));
//            else
//                password.setError(null);
            if (!loginAgreeTerms.isChecked())
                loginAgreeTerms.setError(getString(R.string.required_field));
            else
                loginAgreeTerms.setError(null);
        }
        else {
            loginAgreeTerms.setError(null);
            loginProgress.setVisibility(View.VISIBLE);
            GetPasswordSMSAsyncTask getPasswordSMSAsyncTask = new GetPasswordSMSAsyncTask(phoneStr, this, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    loginProgress.setVisibility(View.GONE);
                    String errorType = "";
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        errorType = jsonObject.getString("errType");
                        if (errorType.equals("")) {

                            String passwordStr=jsonObject.getString("password");
                            goToFillPassword(phoneStr,passwordStr);
//                            fillAndSaveData(jsonObject, passwordStr);
//                            if (utils.isWifiOpen(LoginActivity.this)) {
//                                if (permissionLocationMissed())

                        } else {
                            utils.openPopupWindow(LoginActivity.this,
                                    true,
                                    getString(R.string.notice),
                                    getString(R.string.login_error_txt),
                                    getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        utils.showServerError(LoginActivity.this, e.getMessage());
                    }
                }

                @Override
                public void onErrorWithResult(ResultEntity resultEntity) {
                    loginProgress.setVisibility(View.GONE);
                    utils.showServerError(LoginActivity.this, resultEntity.getResult());
                }
            });
            getPasswordSMSAsyncTask.execute();
        }
    }

    private void goToFillPassword(String phone,String password) {
        Intent intent=new Intent();
        intent.setClass(this, FillPasswordActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("password",password);
        intent.putExtra("imageBgRes", resID);
        startActivity(intent);
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
                // If request is cancelled, the result arrays are empty.
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

//    private void fillAndSaveData(JSONObject jsonObject,String passwordStr) throws JSONException {
//
//        long userId=jsonObject.getLong("user_id");
//        String userName=jsonObject.getString("user_first_name")+" "+jsonObject.getString("user_last_name");
//        String firstName=jsonObject.getString("user_first_name");
//        String lastName=jsonObject.getString("user_last_name");
//        String phone=jsonObject.getString("phone");
//        String email=jsonObject.getString("user_email");
//        String password=passwordStr;
//        String truckType=jsonObject.getString("user_type_name");
//        long carrierId=jsonObject.getLong("carrier_id");
//        int canAcceptOffers=jsonObject.getInt("can_accept_offers");
//        int canSeePrice=jsonObject.getInt("can_see_price");
//        int canSeeOffers=jsonObject.getInt("can_see_offers");
//        long truckId=jsonObject.getLong("truck_type_id");
//        String userImage=jsonObject.getString("user_image");
//        int statusId=jsonObject.getInt("user_status");
//
//
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserId(userId);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserName(userName);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserFirstName(firstName);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserLastName(lastName);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserPhone(phone);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUseEmail(email);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserPassword(password);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserTruckType(truckType);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveCarrierId(carrierId);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveCanAcceptOffers(canAcceptOffers);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveCanSeePrice(canSeePrice);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveCanSeeOffers(canSeeOffers);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveTruckType(truckId);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserImageUrl(userImage);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveIsLogin(true);
//        SharedPreferenceManager.getInstance(LoginActivity.this).saveUserStatus(statusId);
//        //SharedPreferenceManager.getInstance(LoginActivity.this).savePassword(passwordStr);
//
//    }

    private void goToDashboard(){

        sendToken();
        getStatuses();
        getLastVersion();

        BadgeUtils.setBadge(this, DBHelper.getDB(this)
                .getNotOpenedNotifications(SharedPreferenceManager.getInstance(this).getUserId()));

//        if (SharedPreferenceManager.getInstance(this).isFirstTime()){
//            SharedPreferenceManager.getInstance(this).saveIsFirstTime(false);
//            Intent intent=new Intent();
//            intent.putExtra("imageBgRes", resID);
//            intent.setClass(this, ProfileImageActivity.class);
//            startActivity(intent);
//            LoginActivity.this.finish();
//        }
//        else {
        boolean isFromOpenNotification = getIntent().getBooleanExtra("isFromOpenNotification", false);

        final Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        if (isFromOpenNotification) {
            Notification notification = (Notification) getIntent().getSerializableExtra("notification");
            String action = getIntent().getStringExtra("action");
            intent.putExtra("isFromOpenNotification", true);
            intent.putExtra("notification", notification);
            intent.putExtra("action", action);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                LoginActivity.this.finish();
            }
        }, 1000);


//            updateUserDetails();
//        }
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
                    SharedPreferenceManager.getInstance(LoginActivity.this).saveLastVersion(lastVersion);
                    SharedPreferenceManager.getInstance(LoginActivity.this).saveLastTestingVersion(lastTestingVersion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                utils.showServerError(LoginActivity.this, resultEntity.getResult());
            }
        });

        getLastVersionAsyncTask.execute();

    }


    public void openSite(View view){
        String url = StartApplication.START_URL+"/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==RESULT_OK&&resultCode== Activity.RESULT_OK){
//            String password=data.getStringExtra("password");
//        }
         if (requestCode==RESULT_TERMS&&resultCode== Activity.RESULT_OK){
            boolean isAgreeCheck=data.getBooleanExtra("agreeCheckbox", false);
            loginAgreeTerms.setChecked(isAgreeCheck);
        }
    }

    public void getGeneralSettings() {
        GetGeneralSettingsAsyncTask getGeneralSettingsAsyncTask=new GetGeneralSettingsAsyncTask(this, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                try {
                    JSONObject jsonObject=new JSONObject(result.getResult());
                    String supportPhone=jsonObject.getString("app_support_phone");
                    SharedPreferenceManager.getInstance(LoginActivity.this).saveSupportPhone(supportPhone);
                    termsText= Html.fromHtml(jsonObject.getString("app_terms")).toString();

                    int timeShowShipperInfo=jsonObject.getInt("time_show_shipper_info_origin_destination_contact_info");
                    SharedPreferenceManager.getInstance(LoginActivity.this).saveTimeShowShipperInfo(timeShowShipperInfo);

                    loginAgreeTermsText.setOnClickListener(agreeTermsClickListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getGeneralSettingsAsyncTask.execute();
    }
}
