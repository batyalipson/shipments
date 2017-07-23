package com.shtibel.truckies.activities.account;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.asyncTasks.SendExceptionAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdatePasswordAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateUserInfoAsyncTask;
import com.shtibel.truckies.asyncTasks.UploadImageUserAsyncTask;
import com.shtibel.truckies.generalClasses.OldRoundedBitmapDisplayer;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    Utils utils=new Utils();
    ImageView userImage;

    TextView ratingUserTxt;
    RatingBar ratingBar;

    TextView driverName;

    EditText firstName;
    EditText lastName;
    EditText phone;
    EditText email;
    //EditText password;
//    Spinner truckType;
    ProgressBar updateUserDetailsProgress;
//    RelativeLayout truckTypeProgress;

    int sum=0;
    ProgressBar uploadImageProgress;
    DisplayImageOptions defaultOptions;

    View availableCircle;
    TextView availableTxt;
//    public TruckType truckTypes[]=null;


    EditText oldPassword;
    EditText newPassword;
    EditText confirmPassword;
    ProgressBar passwordProgress;
    LinearLayout mainLayout;

    File photo=null;
    Uri imageUri;
    UserDetailsChangedReceiver userDetailsChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initViews();
    }

    private void initViews() {
        userImage= (ImageView) findViewById(R.id.account_user_image);
        initUserImage();

        ratingUserTxt= (TextView) findViewById(R.id.account_text_rating);
        ratingBar= (RatingBar) findViewById(R.id.account_rating_bar);
        getRating();

        driverName= (TextView) findViewById(R.id.account_driver_name);
        driverName.setText(SharedPreferenceManager.getInstance(this).getUserFirstName()+
                " "+SharedPreferenceManager.getInstance(this).getUserLastName());

        uploadImageProgress= (ProgressBar) findViewById(R.id.account_upload_image_progress);

        firstName= (EditText) findViewById(R.id.account_first_name);
        firstName.setText(SharedPreferenceManager.getInstance(this).getUserFirstName());

        lastName= (EditText) findViewById(R.id.account_last_name);
        lastName.setText(SharedPreferenceManager.getInstance(this).getUserLastName());

        phone= (EditText) findViewById(R.id.account_phone);
        phone.setText(SharedPreferenceManager.getInstance(this).getUserPhone());

        email= (EditText) findViewById(R.id.account_email);
        email.setText(SharedPreferenceManager.getInstance(this).getUserEmail());

//        password= (EditText) findViewById(R.id.account_password);
//        password.setText(SharedPreferenceManager.getInstance(this).getUserPassword());
        updateUserDetailsProgress= (ProgressBar) findViewById(R.id.account_update_info_progress);

//        truckType= (Spinner) findViewById(R.id.account_truck_type);
//        getTruckTypes();


        // initTruckTypesSpinner();

        availableCircle=findViewById(R.id.account_available_circle);
        availableTxt= (TextView) findViewById(R.id.account_available_text);

        if (SharedPreferenceManager.getInstance(this).getAvailableStatus()==
                SharedPreferenceManager.AVAILABLE_STATUS_AVAILABLE){
            utils.drawCircle(availableCircle.getWidth(),availableCircle.getHeight(),"#39B54A",availableCircle);
            availableTxt.setText(getString(R.string.available));
        }
        else {
            utils.drawCircle(availableCircle.getWidth(),availableCircle.getHeight(),"#FD553A",availableCircle);
            availableTxt.setText(getString(R.string.unavailable));
        }

        oldPassword= (EditText) findViewById(R.id.account_old_password);
        newPassword= (EditText) findViewById(R.id.account_new_password);
        confirmPassword= (EditText) findViewById(R.id.account_confirm_password);
        passwordProgress= (ProgressBar) findViewById(R.id.account_password_progress);

        mainLayout= (LinearLayout) findViewById(R.id.account_layout);
        mainLayout.requestFocus();
    }

//    public void getTruckTypes() {
//        truckTypeProgress= (RelativeLayout) findViewById(R.id.account_truck_progress);
//        truckTypeProgress.setVisibility(View.VISIBLE);
//        updateUserDetailsProgress.setVisibility(View.VISIBLE);
//        GetTruckTypesAsyncTask getTruckTypesAsyncTask=new GetTruckTypesAsyncTask(this, new IResultsInterface() {
//            @Override
//            public void onCompleteWithResult(ResultEntity resultEntity) {
//                truckTypeProgress.setVisibility(View.GONE);
//                updateUserDetailsProgress.setVisibility(View.GONE);
//                try {
//                    JSONArray jsonArray=new JSONArray(resultEntity.getResult());
//                    truckTypes=new TruckType[jsonArray.length()];
//                    for (int i=0;i<jsonArray.length();i++) {
//                        JSONObject jsonObject=jsonArray.getJSONObject(i);
//                        long id=jsonObject.getLong("id");
//                        String name=jsonObject.getString("name");
//
//                        TruckType truckType=new TruckType(id,name);
//                        truckTypes[i]=truckType;
//                    }
//                    initTruckTypesSpinner();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    utils.showServerError(AccountActivity.this, e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onErrorWithResult(ResultEntity resultEntity) {
//                truckTypeProgress.setVisibility(View.GONE);
//                updateUserDetailsProgress.setVisibility(View.GONE);
//                utils.showServerError(AccountActivity.this, resultEntity.getResult());
//            }
//        });
//        getTruckTypesAsyncTask.execute();
//    }

//    private void initTruckTypesSpinner() {
//            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
//                    android.R.layout.simple_spinner_item, truckTypes);
//            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            truckType.setAdapter(spinnerArrayAdapter);
//            long currentTruckId = SharedPreferenceManager.getInstance(this).getTruckType();
//            int position = getTruckTypePosition(currentTruckId);
//            truckType.setSelection(position);
//    }

    public void getRating() {

        String numberOfVoters=getIntent().getStringExtra("numberOfVoters");
        double avgRating=getIntent().getDoubleExtra("avgRating",0);
        ratingUserTxt.setText(numberOfVoters+" "+getString(R.string.rating));
        ratingBar.setRating((float)avgRating);
//        GetRatingAsyncTask getRatingAsyncTask=new GetRatingAsyncTask(this, new IResultsInterface() {
//            @Override
//            public void onCompleteWithResult(ResultEntity result) {
//                try {
//                    JSONObject jsonObject=new JSONObject(result.getResult());
//                    String numberOfVoters=jsonObject.getString("number_of_voters");
//                    double avgRating=jsonObject.getDouble("avg_rating");
//                    ratingUserTxt.setText(numberOfVoters+" "+getString(R.string.rating));
//                    ratingBar.setRating((float)avgRating);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        getRatingAsyncTask.execute();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void changePicture(View view){

        if (!SharedPreferenceManager.getInstance(this).getLastVersion().equals(BuildConfig.VERSION_NAME)
                &&!SharedPreferenceManager.getInstance(AccountActivity.this).getLastTestingVersion().equals(BuildConfig.VERSION_NAME))
        {
            utils.showVersionError(this);
        }
        else {
            ArrayList<String> arrPerm = new ArrayList<>();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                arrPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                arrPerm.add(Manifest.permission.CAMERA);
            }
            if (!arrPerm.isEmpty()) {
                String[] permissions = new String[arrPerm.size()];
                permissions = arrPerm.toArray(permissions);
                ActivityCompat.requestPermissions(this, permissions, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                imageUri = utils.getPickImageIntent(this);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUri=utils.getPickImageIntent(this);
                } else {


                }
                return;
            }
            case Utils.MY_PERMISSIONS_REQUEST_DIALER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + SharedPreferenceManager.getInstance(this).getSupportPhone()));
                    startActivity(intent);
                } else {
                }

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK&&requestCode==Utils.TAKE_IMAGE_FROM_GALLERY) {
            boolean isSuccess=false;
            try {

                String path = "";
                if (data == null || data.getData() == null) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                Uri tempUri = getImageUri(getApplicationContext(), photo);
//                File finalFile = new File(getRealPathFromURI(tempUri));
                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        path = getRealPathFromURI(imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Uri imageUri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(imageUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToLast();

                    path = cursor.getString(column_index);
                }


                if (path == null || path.equals("")) {
                } else {

                    File imageFile = new File(path);
                    if (imageFile.exists()) {
                        isSuccess=true;
                        final File lowQualityImage = utils.saveBitmapToFile(imageFile, this);

                        int lastDotIndex = path.lastIndexOf('.');
                        String ext = path.substring(lastDotIndex, path.length());
//                    String ext=MimeTypeMap.getFileExtensionFromUrl(imageFile.toURL().toString());
                        uploadImageProgress.setVisibility(View.VISIBLE);
                        UploadImageUserAsyncTask uploadImageUserAsyncTask = new UploadImageUserAsyncTask(this, lowQualityImage, "userImage" + ext, new IResultsInterface() {
                            @Override
                            public void onCompleteWithResult(ResultEntity result) {
                                uploadImageProgress.setVisibility(View.GONE);
                                try {

                                    JSONObject jsonObject = new JSONObject(result.getResult());
                                    String imageUrl = jsonObject.getString("url");
//                                    Toast.makeText(AccountActivity.this,imageUrl,Toast.LENGTH_LONG).show();
                                    SharedPreferenceManager.getInstance(AccountActivity.this).saveUserImageUrl(imageUrl);
                                    MemoryCacheUtils.removeFromCache(StartApplication.START_URL + imageUrl, ImageLoader.getInstance().getMemoryCache());
                                    DiskCacheUtils.removeFromCache(StartApplication.START_URL + imageUrl, ImageLoader.getInstance().getDiscCache());
                                    initUserImage();
                                    utils.openPopupWindow(AccountActivity.this,
                                            true,
                                            getString(R.string.notice),
                                            getString(R.string.save_changes_message),
                                            getResources().getString(R.string.ok),
                                            "",
                                            null,
                                            new PopupInterface() {
                                            });
                                    //to refresh user image in the drawer of the main activity
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onErrorWithResult(ResultEntity resultEntity) {
                                uploadImageProgress.setVisibility(View.GONE);
                                showFailedMessage();
                            }
                        });
                        uploadImageUserAsyncTask.execute();

                    }
                }
            }
            catch (Exception e){
                showFailedMessage();
                SendExceptionAsyncTask sendExceptionAsyncTask=new SendExceptionAsyncTask(AccountActivity.this,e.getMessage());
                sendExceptionAsyncTask.execute();
            }
            if (!isSuccess){
                showFailedMessage();
            }
        }
    }

    private void showFailedMessage() {
        utils.openPopupWindow(AccountActivity.this,
                true,
                getString(R.string.notice),
                getString(R.string.save_changes_message_failed),
                getResources().getString(R.string.ok),
                "",
                null,
                new PopupInterface() {
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
//    public String getRealPathFromURI(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        return cursor.getString(idx);
//    }

    public void close(View view){
        finish();
    }
    private void initUserImage(){

        ImageLoader imageLoader=ImageLoader.getInstance();
        defaultOptions= new DisplayImageOptions.Builder()
                .displayer(new OldRoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.default_driver_image)
                .showImageOnLoading(null)
                .showImageOnFail(R.drawable.default_driver_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        imageLoader.displayImage(StartApplication.START_URL + SharedPreferenceManager.getInstance(this).getUserImageUrl()
                , userImage, defaultOptions);
    }

//    public String getTruckNameById(long id){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==id)
//                return truckTypes[i].getName();
//        }
//        return "";
//    }
//    public TruckType getTruckTypeById(long truckId){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==truckId)
//                return truckTypes[i];
//        }
//        return truckTypes[0];
//    }
//
//    public int getTruckTypePosition(long truckId){
//        for (int i=0;i<truckTypes.length;i++){
//            if (truckTypes[i].getId()==truckId)
//               return i;
//        }
//       return  0;
//    }

    public void updateInfo(View view){

        if (!SharedPreferenceManager.getInstance(this).getLastVersion().equals(BuildConfig.VERSION_NAME)
                &&!SharedPreferenceManager.getInstance(AccountActivity.this).getLastTestingVersion().equals(BuildConfig.VERSION_NAME))
        {
            utils.showVersionError(this);
        }
        else if (infoValidation()) {
            final String firstNameStr = firstName.getText().toString();
            final String lastNameStr = lastName.getText().toString();
            final String emailStr = email.getText().toString();
            final String phoneStr = phone.getText().toString();
//            final long truckTypeId = ((TruckType) truckType.getSelectedItem()).getId();

            updateUserDetailsProgress.setVisibility(View.VISIBLE);
            UpdateUserInfoAsyncTask updateUserInfoAsyncTask = new UpdateUserInfoAsyncTask(this, firstNameStr,
                    lastNameStr, emailStr, phoneStr, 0, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    updateUserDetailsProgress.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());
                        boolean err = jsonObject.getBoolean("err");
                            if (err) {

                                String errType=jsonObject.getString("errType");
                                if (errType.equals("email exists"))
                                    utils.openPopupWindow(AccountActivity.this,
                                            true,
                                            getString(R.string.notice),
                                            getString(R.string.email_exist),
                                            getString(R.string.ok),
                                            "",
                                            null,
                                            new PopupInterface() {
                                            });
                                else if (errType.equals("phone exists"))
                                    utils.openPopupWindow(AccountActivity.this,
                                            true,
                                            getString(R.string.notice),
                                            getString(R.string.phone_exist),
                                            getString(R.string.ok),
                                            "",
                                            null,
                                            new PopupInterface() {
                                            });
                        } else {

                            utils.openPopupWindow(AccountActivity.this,
                                    true,
                                    getString(R.string.notice),
                                    getString(R.string.save_changes_message),
                                    getResources().getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });

                            SharedPreferenceManager.getInstance(AccountActivity.this).saveUserFirstName(firstNameStr);
                            SharedPreferenceManager.getInstance(AccountActivity.this).saveUserLastName(lastNameStr);
                            SharedPreferenceManager.getInstance(AccountActivity.this).saveUseEmail(emailStr);
                            SharedPreferenceManager.getInstance(AccountActivity.this).saveUserPhone(phoneStr);
//                            SharedPreferenceManager.getInstance(AccountActivity.this).saveTruckType(truckTypeId);
                        }
                    }

                    catch(Exception exception){

                    }
                }

                @Override
                public void onErrorWithResult(ResultEntity resultEntity) {
                    updateUserDetailsProgress.setVisibility(View.GONE);
                }
            });

            updateUserInfoAsyncTask.execute();
        }

    }

    private boolean infoValidation(){
        firstName.setError(null);
        lastName.setError(null);
        email.setError(null);
        phone.setError(null);

        boolean isValid=true;

//        if (truckTypes==null||truckTypes.length==0){
//            isValid=false;
//        }

        if (email.getText().toString().equals("")){
            isValid=false;
            email.setError(getString(R.string.required_field));
            email.requestFocus();
        }
        else if (!utils.isValidEmail(email.getText())){
            isValid=false;
            email.setError(getString(R.string.invalid_email));
            email.requestFocus();
        }
        if (phone.getText().toString().equals("")){
            isValid=false;
            phone.setError(getString(R.string.required_field));
            phone.requestFocus();
        }
        if (lastName.getText().toString().equals("")){
            isValid=false;
            lastName.setError(getString(R.string.required_field));
            lastName.requestFocus();
        }
        if (firstName.getText().toString().equals("")){
            isValid=false;
            firstName.setError(getString(R.string.required_field));
            firstName.requestFocus();
        }



        return isValid;

    }
    public void updatePassword(View view){

        if (passwordValidation()) {
            String passwordStr = oldPassword.getText().toString();
            final String newPasswordStr = newPassword.getText().toString();
            String confirmPasswordStr = confirmPassword.getText().toString();
            if (passwordStr.equals(SharedPreferenceManager.getInstance(this).getUserPassword())) {
                if (newPasswordStr.equals(confirmPasswordStr)) {
                    passwordProgress.setVisibility(View.VISIBLE);
                    UpdatePasswordAsyncTask updatePasswordAsyncTask = new UpdatePasswordAsyncTask(this, newPasswordStr, new IResultsInterface() {
                        @Override
                        public void onCompleteWithResult(ResultEntity result) {
                            passwordProgress.setVisibility(View.GONE);
                            SharedPreferenceManager.getInstance(AccountActivity.this).saveUserPassword(newPasswordStr);
                            utils.openPopupWindow(AccountActivity.this,
                                    true,
                                    getString(R.string.notice),
                                    getString(R.string.save_changes_message),
                                    getResources().getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }

                        @Override
                        public void onErrorWithResult(ResultEntity resultEntity) {
                            passwordProgress.setVisibility(View.GONE);
                        }
                    });
                    updatePasswordAsyncTask.execute();
                } else {
                    utils.openPopupWindow(AccountActivity.this,
                            true,
                            getString(R.string.notice),
                            getString(R.string.confirm_password_error),
                            getString(R.string.ok),
                            "",
                            null,
                            new PopupInterface() {
                            });
//                utils.showInfoPopup(getString(R.string.error),
//                        getString(R.string.confirm_password_error),AccountActivity.this);
                }
            } else {
                utils.openPopupWindow(AccountActivity.this,
                        true,
                        getString(R.string.notice),
                        getString(R.string.password_error),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {
                        });
//            utils.showInfoPopup(getString(R.string.error),
//                    getString(R.string.password_error),AccountActivity.this);
            }
        }
    }
    private boolean passwordValidation(){
        oldPassword.setError(null);
        newPassword.setError(null);
        confirmPassword.setError(null);
        boolean isValid=true;
        if (confirmPassword.getText().toString().equals("")){
            isValid=false;
            confirmPassword.setError(getString(R.string.required_field));
            confirmPassword.requestFocus();
        }
        if (newPassword.getText().toString().equals("")){
            isValid=false;
            newPassword.setError(getString(R.string.required_field));
            newPassword.requestFocus();
        }
        if (oldPassword.getText().toString().equals("")){
            isValid=false;
            oldPassword.setError(getString(R.string.required_field));
            oldPassword.requestFocus();
        }




        return isValid;
    }
    public void goToSupport(View view){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE,},
                    Utils.MY_PERMISSIONS_REQUEST_DIALER);

        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + SharedPreferenceManager.getInstance(this).getSupportPhone()));
            startActivity(intent);
        }
    }


    private class UserDetailsChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AlarmReceiver.APPLICATION_DETAILS_CHANGED)) {
                //TODO close application after show message
                utils.openPopupWindow(AccountActivity.this,
                        false,
                        getString(R.string.notice),
                        getString(R.string.user_details_changed_message),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {

                            @Override
                            public void onOk() {
                                utils.closeApplication(AccountActivity.this);
                            }
                        });
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (userDetailsChangedReceiver == null)
            userDetailsChangedReceiver = new UserDetailsChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(AlarmReceiver.APPLICATION_DETAILS_CHANGED);
        registerReceiver(userDetailsChangedReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userDetailsChangedReceiver != null)
            unregisterReceiver(userDetailsChangedReceiver);
    }

}
