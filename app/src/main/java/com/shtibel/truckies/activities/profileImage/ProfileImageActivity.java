package com.shtibel.truckies.activities.profileImage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.asyncTasks.GetStatusesAsyncTask;
import com.shtibel.truckies.asyncTasks.SendTokenGcmAsyncTask;
import com.shtibel.truckies.asyncTasks.UploadImageUserAsyncTask;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileImageActivity extends AppCompatActivity {

    ImageView bgImage;
    ProgressBar progressBar;
    int imageRes;
    Utils utils=new Utils();
    Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        progressBar= (ProgressBar) findViewById(R.id.profile_img_progress);
        imageRes = getIntent().getIntExtra("imageBgRes", 0);
        bgImage= (ImageView) findViewById(R.id.profile_img_image);
        bgImage.setImageResource(imageRes);
    }

    public void openCaptureImage(View view){

        ArrayList<String> arrPerm = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.CAMERA);
        }
        if(!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions,  Utils.MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            imageUri=utils.getPickImageIntent(this);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK&&requestCode==Utils.TAKE_IMAGE_FROM_GALLERY) {
            boolean isSuccess=false;
            String path ="";
//            }
            if(data==null||data.getData()==null) {
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

            }
            else {
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

                    progressBar.setVisibility(View.VISIBLE);
                    UploadImageUserAsyncTask uploadImageUserAsyncTask = new UploadImageUserAsyncTask(this, lowQualityImage, "userImage." + ext, new IResultsInterface() {
                        @Override
                        public void onCompleteWithResult(ResultEntity result) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(result.getResult());
                                String imageUrl = jsonObject.getString("url");
                                SharedPreferenceManager.getInstance(ProfileImageActivity.this).saveUserImageUrl(imageUrl);
                                MemoryCacheUtils.removeFromCache(StartApplication.START_URL + imageUrl, ImageLoader.getInstance().getMemoryCache());
                                DiskCacheUtils.removeFromCache(StartApplication.START_URL + imageUrl, ImageLoader.getInstance().getDiscCache());
                                goToDashboard();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onErrorWithResult(ResultEntity resultEntity) {
                            progressBar.setVisibility(View.GONE);
                            showFailedMessage();
                        }
                    });
                    uploadImageUserAsyncTask.execute();

                }
            }
            if (!isSuccess){
                showFailedMessage();
            }
        }
    }

    private void showFailedMessage() {
        utils.openPopupWindow(ProfileImageActivity.this,
                true,
                getString(R.string.notice),
                getString(R.string.profile_image_problem),
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

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void skip(View view){
        goToDashboard();
    }

    private void goToDashboard(){
        final Intent intent = new Intent();
        intent.setClass(ProfileImageActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        ProfileImageActivity.this.finish();

        sendToken();
        getStatuses();
//        updateUserDetails();
    }

    private void sendToken() {

        SendTokenGcmAsyncTask sendTokenGcmAsyncTask=new SendTokenGcmAsyncTask(this);
        sendTokenGcmAsyncTask.execute();

    }
    public void getStatuses() {
        GetStatusesAsyncTask getStatusesAsyncTask=new GetStatusesAsyncTask(this);
        getStatusesAsyncTask.execute();
    }

//    public void updateUserDetails() {
//        UpdateUserDetailsAsyncTask updateUserDetailsAsyncTask=new UpdateUserDetailsAsyncTask(this);
//        updateUserDetailsAsyncTask.execute();
//    }

}
