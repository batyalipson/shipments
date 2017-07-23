package com.shtibel.truckies.generalClasses;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kyleduo.switchbutton.SwitchButton;
import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.activities.dashboard.tabs.offers.Offer;
import com.shtibel.truckies.activities.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Shtibel on 06/07/2016.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Utils {


    public static final String MAIN_DIRECTORY="truckies";
    public static final String RECORD_DIRECTORY="records";
    public static final int TAKE_IMAGE_FROM_GALLERY=1111;
    public static final int TAKE_IMAGE_FROM_CAMERA=2222;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA =1 ;
    public static final int MY_PERMISSIONS_REQUEST_DIALER =2 ;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public Utils(){

    }

    public String getPointsOfPath(String  result) {

        try {
            //Tranform the string into a json object
            List<Integer> colors=new ArrayList<>();
            colors.add(Color.GREEN);
            colors.add(Color.RED);
            colors.add(Color.BLUE);
            colors.add(Color.YELLOW);
            colors.add(Color.CYAN);
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            Log.d("routeArray.length()", routeArray.length() + "");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
//            List<LatLng> list = decodePoly(encodedString);
           return encodedString;
        }
        catch (JSONException e) {

        }
        return null;
    }

    public void drawPath(String  result,GoogleMap googleMap) {

        try {
            //Tranform the string into a json object
            List<Integer> colors=new ArrayList<>();
            colors.add(Color.GREEN);
            colors.add(Color.RED);
            colors.add(Color.BLUE);
            colors.add(Color.YELLOW);
            colors.add(Color.CYAN);
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            Log.d("routeArray.length()", routeArray.length() + "");
            for (int i=0;i<routeArray.length();i++){
                JSONObject routes = routeArray.getJSONObject(i);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                Polyline line = googleMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(8)
                                .color(colors.get(i))//Google maps blue color
                                .geodesic(true)


                );
            }

        }
        catch (JSONException e) {

        }
    }

    public Polyline drawOnePath(String  result,GoogleMap googleMap,int color) {

        Polyline line=null;
        try {
            //Tranform the string into a json object
            List<Integer> colors=new ArrayList<>();

            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            Log.d("routeArray.length()", routeArray.length() + "");
//            for (int i=0;i<routeArray.length();i++){
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                 line = googleMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(10)
                                .color(color)//Google maps blue color
                                .geodesic(true)


                );
//            }

        }
        catch (Exception e) {

        }
        return line;
    }

    public String makeURLtoGetPath (double sourcelat, double sourcelog, double destlat, double destlog){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyAKJvVBiZFJjGTdb6CbJNqMnpBEE-ktmIo");
        urlString.append("&language=iw");
        return urlString.toString();
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }


    public Calendar getZeroTimeDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

//    public void showInfoPopup(String title, String message, Context context)
//    {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//
//        // set title
//        alertDialogBuilder.setTitle(title);
//
//        // set dialog message
//        alertDialogBuilder
//                .setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.d("alert_dialog", "ok click");
//                    }
//                });
//                    /*.setNegativeButton("No",new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog,int id) {
//                            // if this button is clicked, just close
//                            // the dialog box and do nothing
//                            dialog.cancel();
//                        }
//                    });*/
//
//        // create alert dialog
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
//    }
//    public void showInfoPopupAndFinish(String title, String message, final Activity context)
//    {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//        alertDialogBuilder.setTitle(title);
//        alertDialogBuilder
//                .setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.d("alert_dialog", "ok click");
//                        context.finish();
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        alertDialog.show();
//    }

    public Date convertStringToDate(String dateStr){
        //String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date=null;
        try {
            date = format.parse(dateStr);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String convertDateToString(Date date,String strFormat){
       SimpleDateFormat simpleDateFormat=new SimpleDateFormat(strFormat);
       return simpleDateFormat.format(date);
    }

    public void setVisibilityAnimation(final View view,boolean visible){
        if (visible&&view.getVisibility()==View.GONE){
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    });
        }
        else if (view.getVisibility()==View.VISIBLE){
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }

//    public void showSettingsAlert(final Context context){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing the Settings button.
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                context.startActivity(intent);
//            }
//        });
//
//        // On pressing the cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//
//            }
//        });
//        alertDialog.setCancelable(false);
//        // Showing Alert Message
//        alertDialog.show();
//    }

    public boolean isGPSConnect(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        else
            return false;
    }


    public static void drawCircle (int width, int height, String color,View view) {

        if(Build.VERSION.SDK_INT >= 16) {
            view.setBackground (getCircle(width,height, color));
        }else{
            view.setBackgroundDrawable (getCircle(width, height, color));
        }
    }

    private static ShapeDrawable getCircle(int width, int height, String color){
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(height);
        oval.setIntrinsicWidth(width);
        oval.getPaint ().setColor(Color.parseColor(color));
        return oval;
    }

    public void switchColor(SwitchButton switchButton){


        int colorOn = Color.parseColor("#39B54A");
        int colorOff = Color.parseColor("#FD553A");
        int colorDisabled = Color.parseColor("#656565");
        StateListDrawable thumbStates = new StateListDrawable();
        ColorDrawable colorDrawable=new ColorDrawable();
        thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorCircleDrawable(colorOn));
        thumbStates.addState(new int[]{-android.R.attr.state_enabled}, new ColorCircleDrawable(colorDisabled));
        thumbStates.addState(new int[]{}, new ColorCircleDrawable(colorOff)); // this one has to come last
        switchButton.setThumbDrawable(thumbStates);

    }
    public int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width;
    }
    public int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return height;
    }

    public int getStaticMapHeight(Context context){
        int mapHeight=(int)context.getResources().getDimension(R.dimen.map_height);
        return mapHeight;
    }
    public int getStaticMapWidth(Context context){
        int screenWidth=getScreenWidth(context);
        int margins=(int)(context.getResources().getDimension(R.dimen.medium_margin)*2);
        return screenWidth-margins;
    }
    public Bitmap resizeMapIcons(Context context,String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public Uri getPickImageIntent(Context context) {

//        String path= Environment.getExternalStorageDirectory().getAbsolutePath();
//        File folder = new File(path +File.separator+ "truckies");
//        if (!folder.exists()) {
//            folder.mkdir();
//        }
//        File photo=new File(path +File.separator+ "truckies","temp_image");
//        if (photo.exists()) {
//            photo.delete();
//        }



        Intent choosePicIntent=new Intent();
        choosePicIntent.setType("image/*");
        choosePicIntent.setAction(Intent.ACTION_PICK);
        choosePicIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        choosePicIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);


        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Uri imageUri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent tackPicIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        tackPicIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
        tackPicIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        String pickTitle = context.getString(R.string.choose_image);
        Intent chooserIntent = Intent.createChooser(choosePicIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{tackPicIntent});

        ((Activity)context).startActivityForResult(chooserIntent, TAKE_IMAGE_FROM_GALLERY);

        return imageUri;
    }
    public void openCameraIntent(Context context){

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                Manifest.permission.CAMERA)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.CAMERA,},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity)context).startActivityForResult(takePictureIntent, TAKE_IMAGE_FROM_GALLERY);
    }


    public File getFileFromBitmap(Bitmap image,String filename,Context context){
        //create a file to write bitmap data
        File f = new File(context.getCacheDir(), filename+".png");
        try {
            f.createNewFile();

            //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

          //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
                return null;
        }
    }

    public void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void showKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public boolean isAppSentToBackground(final Context context) {

        try {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            // The first in the list of RunningTasks is always the foreground
            // task.
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity
                    .getPackageName();// get the top fore ground activity
            PackageManager pm = context.getPackageManager();
            PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(
                    foregroundTaskPackageName, 0);

            String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo
                    .loadLabel(pm).toString();

            // Log.e("", foregroundTaskAppName +"----------"+
            // foregroundTaskPackageName);
            if (!foregroundTaskAppName.equals("Your App name")) {
                return true;
            }
        } catch (Exception e) {
            Log.e("isAppSentToBackground", "" + e);
        }
        return false;
    }

//    public File decodeSampledBitmapFromResource(String pathName,Context context) {
//        int reqWidth,reqHeight;
//        reqWidth = getScreenWidth(context);
//        reqWidth = (reqWidth/5)*2;
//        reqHeight = reqWidth;
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
////  BitmapFactory.decodeStream(is, null, options);
//        BitmapFactory.decodeFile(pathName, options);
//// Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//// Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        Bitmap imageBitmap=BitmapFactory.decodeFile(pathName, options);
//
//        int index=pathName.indexOf(".");
//        File f = new File(pathName.substring(0,index)+"(1)"+pathName.substring(index));
////        File f = new File(pathName.substring(0,index)+pathName.substring(index));
//        try {
//            f.createNewFile();
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//            byte[] bitmapdata = bos.toByteArray();
//
////write the bytes in file
//            FileOutputStream fos = new FileOutputStream(f);
//            fos.write(bitmapdata);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////Convert bitmap to byte array
//
//
//        return f;
//    }

    public  boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    public File saveBitmapToFile(File file,Activity activity){
        try {
            ExifInterface oldExif = new ExifInterface(file.getPath());
            String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file

            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED) {

                String path= Environment.getExternalStorageDirectory().getAbsolutePath();
                File folder = new File(path +File.separator+ MAIN_DIRECTORY);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File photo=new File(path +File.separator+ MAIN_DIRECTORY, file.getName());
                if (photo.exists()) {
                    photo.delete();
                }

                photo.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(photo);

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                if (exifOrientation != null) {
                    ExifInterface newExif = new ExifInterface(photo.getPath());
                    newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                    newExif.saveAttributes();
                }
                    return photo;
            }

        } catch (Exception e) {
            return null;
        }
         return null;
    }


    public   int calculateInSampleSize(BitmapFactory.Options options,
                                       int reqWidth, int reqHeight) {
// Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }
    public void openPopupWindow(Context context,boolean cancelAble,String title,String content,String okText,
                                 final String cancelText, final List<View> contentLayout, final PopupInterface popupInterface){
        if (context!=null&&context instanceof Activity&&!((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_message);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancelAble);

            TextView popupTitle = (TextView) dialog.findViewById(R.id.popup_title);
            popupTitle.setText(title);
            final TextView popupContent = (TextView) dialog.findViewById(R.id.popup_content_text);
            popupContent.setText(content);

            LinearLayout popupContentLayout = (LinearLayout) dialog.findViewById(R.id.popup_content_layout);
            popupContentLayout.removeAllViews();

            if (contentLayout != null && contentLayout.size() > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                boolean isFirst = true;
                for (View view : contentLayout) {
                    if (!isFirst)
                        layoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.small_margin), 0, 0);
                    else
                        isFirst = false;
                    view.setLayoutParams(layoutParams);
                    popupContentLayout.addView(view);
                }
            } else
                popupContentLayout.setVisibility(View.GONE);

            Button ok = (Button) dialog.findViewById(R.id.popup_ok_btn);
            ok.setText(okText);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    popupInterface.onOk();

                }
            });

            Button cancel = (Button) dialog.findViewById(R.id.popup_cancel_btn);
            if (!cancelText.equals("")) {
                cancel.setText(cancelText);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        popupInterface.onCancel();
                    }
                });
            } else
                cancel.setVisibility(View.GONE);

            dialog.show();
        }

    }

    public void openPopupWindowWithoutValidation(Context context,boolean cancelAble,String title,String content,String okText,
                                final String cancelText, final List<View> contentLayout, final PopupInterface popupInterface){
//        if (context!=null&&context instanceof Activity&&!((Activity) context).isFinishing()) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_message);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancelAble);

            TextView popupTitle = (TextView) dialog.findViewById(R.id.popup_title);
            popupTitle.setText(title);
            final TextView popupContent = (TextView) dialog.findViewById(R.id.popup_content_text);
            popupContent.setText(content);

            LinearLayout popupContentLayout = (LinearLayout) dialog.findViewById(R.id.popup_content_layout);
            popupContentLayout.removeAllViews();

            if (contentLayout != null && contentLayout.size() > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                boolean isFirst = true;
                for (View view : contentLayout) {
                    if (!isFirst)
                        layoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.small_margin), 0, 0);
                    else
                        isFirst = false;
                    view.setLayoutParams(layoutParams);
                    popupContentLayout.addView(view);
                }
            } else
                popupContentLayout.setVisibility(View.GONE);

            Button ok = (Button) dialog.findViewById(R.id.popup_ok_btn);
            ok.setText(okText);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    popupInterface.onOk();

                }
            });

            Button cancel = (Button) dialog.findViewById(R.id.popup_cancel_btn);
            if (!cancelText.equals("")) {
                cancel.setText(cancelText);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        popupInterface.onCancel();
                    }
                });
            } else
                cancel.setVisibility(View.GONE);

            dialog.show();
//        }

    }

    public void openPopupWindow(Context context,boolean cancelAble,String title,String content,String okText,
                                final String cancelText,String customBtnTxt, final List<View> contentLayout, final PopupInterface popupInterface,
                                Drawable btnOkIcon,Drawable btnCancelIcon,Drawable btnCustomIcon){
        if (context!=null&&context instanceof Activity&&!((Activity) context).isFinishing()) {
            int drawableHeight = (int) context.getResources().getDimension(R.dimen.small_icon);
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_message);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancelAble);

            TextView popupTitle = (TextView) dialog.findViewById(R.id.popup_title);
            popupTitle.setText(title);
            final TextView popupContent = (TextView) dialog.findViewById(R.id.popup_content_text);
            popupContent.setText(content);

            LinearLayout popupContentLayout = (LinearLayout) dialog.findViewById(R.id.popup_content_layout);
            popupContentLayout.removeAllViews();

            if (contentLayout != null && contentLayout.size() > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                boolean isFirst = true;
                for (View view : contentLayout) {
                    if (!isFirst)
                        layoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.small_margin), 0, 0);
                    else
                        isFirst = false;
                    view.setLayoutParams(layoutParams);
                    popupContentLayout.addView(view);
                }
            } else
                popupContentLayout.setVisibility(View.GONE);

            Button ok = (Button) dialog.findViewById(R.id.popup_ok_btn);
            ok.setText(okText);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    popupInterface.onOk();

                }
            });
            if (btnOkIcon != null) {
                //Drawable drawable=changeDrawableSize(context,ok.getHeight(),ok.getHeight(),btnOkIcon);
                btnOkIcon.setBounds(0, 0, drawableHeight, drawableHeight);
                ok.setCompoundDrawables(btnOkIcon, null, null, null);
                ok.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.small_padding));
            }

            Button cancel = (Button) dialog.findViewById(R.id.popup_cancel_btn);
            if (!cancelText.equals("")) {
                cancel.setText(cancelText);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        popupInterface.onCancel();
                    }
                });
                if (btnCancelIcon != null) {
                    //Drawable drawable=changeDrawableSize(context,cancel.getHeight(),cancel.getHeight(),btnCancelIcon);
                    btnCancelIcon.setBounds(0, 0, drawableHeight, drawableHeight);
                    cancel.setCompoundDrawables(btnCancelIcon, null, null, null);
                    cancel.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.small_padding));
                }
            } else
                cancel.setVisibility(View.GONE);

            Button customBtn = (Button) dialog.findViewById(R.id.popup_custom_btn);
            if (!customBtnTxt.equals("")) {
                customBtn.setVisibility(View.VISIBLE);
                customBtn.setText(customBtnTxt);
                customBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        popupInterface.customBtn();
                    }
                });
                if (btnCustomIcon != null) {
                    //Drawable drawable=changeDrawableSize(context,drawableHeight,drawableHeight,btnCustomIcon);
                    btnCustomIcon.setBounds(0, 0, drawableHeight, drawableHeight);
                    customBtn.setCompoundDrawables(btnCustomIcon, null, null, null);
                    customBtn.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.small_padding));
                }
            }

            dialog.show();
        }

    }

    public void showServerError(Context context,String error){
        if (context!=null&&context instanceof Activity&&!((Activity) context).isFinishing()&&error!=null) {
//            if (error.equals(context.getString(R.string.no_internet))) {
                openPopupWindow(context,
                        true,
                        context.getString(R.string.notice),
                        context.getString(R.string.no_internet),
                        context.getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {
                        });
//            } else {
//                openPopupWindow(context,
//                        true,
//                        context.getString(R.string.server_error_title),
//                        context.getString(R.string.server_error_text),
//                        context.getString(R.string.ok),
//                        "",
//                        null,
//                        new PopupInterface() {
//                        });
//            }
        }
    }
    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public List<JobStatuses> fillStandardStatuses(){
        List<JobStatuses> jobStatuses=new ArrayList<>();

        JobStatuses jobStatus;

        jobStatus=new JobStatuses(5,"Waiting for pickup");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(7,"En route to pickup");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(11,"Shipment picked up");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(12,"En route to drop off");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(15,"Shipment delivered");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(16,"Signature received");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(17,"BOL received");
        jobStatuses.add(jobStatus);

        jobStatus=new JobStatuses(18,"Delivered & pending payment");
        jobStatuses.add(jobStatus);

        return jobStatuses;
    }
    public boolean isWifiOpen(Context context){

        LocationManager locationManager;

        locationManager = (LocationManager) context
                .getSystemService(context.LOCATION_SERVICE);
        // locationManager.addGpsStatusListener(this);
//            stopUsingGPS();
        // Getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            return false;
        }
        return true;
    }
    public void locationClosePopup(final Context context){
        openPopupWindow(context,true,
                context.getString(R.string.gps_close_title),
                context.getString(R.string.gps_close_content),
                context.getString(R.string.settings),
                context.getString(R.string.close),
                null,
                new PopupInterface() {
                    @Override
                    public void onOk() {
                        super.onOk();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);


                    onCancel();
                    }
                });
    }

    public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

        /*public void showSettingsAlert(final Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }*/
        public Jobs fillJob(JSONObject jsonObject) throws JSONException {

            Jobs jobs=new Jobs();
            jobs.setId(jsonObject.getLong("id"));
            jobs.setStatusId(jsonObject.getInt("status_id"));
            jobs.setStatusName(Html.fromHtml(jsonObject.getString("status_name")).toString());

            jobs.setOriginalPickupDate(jsonObject.getString("original_pickup_date"));
            jobs.setPickupDate(jsonObject.getString("pickup_date"));
            jobs.setPickupFromTime(jsonObject.getString("pickup_from_time"));
            jobs.setPickupTillTime(jsonObject.getString("pickup_till_time"));

            jobs.setDropoffDate(jsonObject.getString("dropoff_date"));
            jobs.setDropoffFromTime(jsonObject.getString("dropoff_from_time"));
            jobs.setDropoffTillTime(jsonObject.getString("dropoff_till_time"));

            jobs.setOriginAddress(Html.fromHtml(jsonObject.getString("origin_address")).toString());
            jobs.setDestinationAddress(Html.fromHtml(jsonObject.getString("destination_address")).toString());

            jobs.setTotalLoadWeightText(jsonObject.getLong("total_load_weight"));
            jobs.setTotalWeightType(jsonObject.getString("load_weight_type"));
            jobs.setShipmentShipperPaymentText(jsonObject.getString("shipment_carrier_payout_text"));
            jobs.setTotalDrivingDistance(jsonObject.getString("total_driving_distance"));

            jobs.setOriginLat(jsonObject.getDouble("origin_lat"));
            jobs.setOriginLng(jsonObject.getDouble("origin_lng"));
            jobs.setDestinationLat(jsonObject.getDouble("destination_lat"));
            jobs.setDestinationLng(jsonObject.getDouble("destination_lng"));

            jobs.setCircleColor(Html.fromHtml(jsonObject.getString("status_name_circle_color")).toString());
            jobs.setNumberBoxes(jsonObject.getInt("box_num"));
            jobs.setNumberPallets(jsonObject.getInt("pallet_num"));
            jobs.setNumberTruckload(jsonObject.getInt("truckload_num"));
            //jobs.setPickupIn(jsonObject.getString("pickup_in_time"));
            jobs.setComments(jsonObject.getString("comments"));
            String specialRequestsIcons=jsonObject.getString("shipment_special_request");
            if (!specialRequestsIcons.equals("null")&&!specialRequestsIcons.equals("")){
                List<String> fullSpecials= Arrays.asList(specialRequestsIcons.split(","));
                List<SpecialRequest> specialRequests=new ArrayList<>();
                for (String specialsStr:fullSpecials){
                    int index=specialsStr.indexOf('[');
                    SpecialRequest specialRequest=new SpecialRequest();
                    if (index>=0){
                        specialRequest.setImageResource(specialsStr.substring(0,index));
                        specialRequest.setImageLoafFile(specialsStr.substring(index,(specialsStr.length())));
                    }
                    else {
                        specialRequest.setImageResource(specialsStr);
                        specialRequest.setImageLoafFile("");
                    }
                    specialRequests.add(specialRequest);
                }
                jobs.setSpecials(specialRequests);
            }
            // jobs.setSpecialRequest(Arrays.asList(specialRequestsIcons.split(",")));
            String loads=jsonObject.getString("shipment_load");
            if (!loads.equals("null"))
                jobs.setLoads(Arrays.asList(loads.split(",")));

            jobs.setOriginContactName(jsonObject.getString("origin_contact_name"));
            jobs.setOriginContactPhone(jsonObject.getString("origin_contact_phone"));
            jobs.setDestinationContactName(jsonObject.getString("destination_contact_name"));
            jobs.setDestinationContactPhone(jsonObject.getString("destination_contact_phone"));

            jobs.setPickupInTime(jsonObject.getString("pickup_in_time"));
            jobs.setDropoffInTime(jsonObject.getString("dropoff_in_time"));
            if (jsonObject.getString("shipment_complete_date").equals("null"))
                jobs.setCompletedDate("");
            else
                jobs.setCompletedDate(jsonObject.getString("shipment_complete_date"));

            jobs.setSignatureUrlPickup(jsonObject.getString("signature_url_pickup"));
            jobs.setReceiverNamePickup(jsonObject.getString("signature_receiver_name_pickup"));
            jobs.setTotalWeightPickup(jsonObject.getInt("total_weight_pickup"));
            jobs.setConfPhonePickup(jsonObject.getString("confirmation_phone_pickup"));
            jobs.setConfDatePickup(jsonObject.getString("confirmation_date_pickup"));
            jobs.setDriverNamePickup(jsonObject.getString("confirmation_user_pickup"));

            String loadTypesPickup=jsonObject.getString("load_types_pickup_str");
            if (!loadTypesPickup.equals("null")) {
                List<String> temp=Arrays.asList(loadTypesPickup.split(","));
                for (String s:temp) {
                    jobs.getLoadTypesPickup().add(Integer.parseInt(s));
                }
            }
            String loadTypesQuantityPickup=jsonObject.getString("load_types_pickup_quantity_str");
            if (!loadTypesQuantityPickup.equals("null")) {
                List<String> temp=Arrays.asList(loadTypesQuantityPickup.split(","));
                for (String s:temp) {
                    jobs.getLoadTypesQuantityPickup().add(Integer.parseInt(s));
                }
            }


            jobs.setSignatureUrl(jsonObject.getString("signature_url"));
            jobs.setReceiverName(jsonObject.getString("signature_receiver_name"));
            jobs.setTotalWeightDropoff(jsonObject.getInt("total_weight_dropoff"));
            jobs.setConfPhoneDropoff(jsonObject.getString("confirmation_phone_dropoff"));
            jobs.setConfDateDropoff(jsonObject.getString("confirmation_date_dropoff"));
            jobs.setDriverNameDropoff(jsonObject.getString("confirmation_user_dropoff"));

            String loadTypesDropoff=jsonObject.getString("load_types_dropoff_str");
            if (!loadTypesDropoff.equals("null")) {
                List<String> temp=Arrays.asList(loadTypesDropoff.split(","));
                for (String s:temp) {
                    jobs.getLoadTypesDropoff().add(Integer.parseInt(s));
                }
            }
            String loadTypesQuantityDropoff=jsonObject.getString("load_types_dropoff_quantity_str");
            if (!loadTypesQuantityDropoff.equals("null")) {
                List<String> temp=Arrays.asList(loadTypesQuantityDropoff.split(","));
                for (String s:temp) {
                    jobs.getLoadTypesQuantityDropoff().add(Integer.parseInt(s));
                }
            }


            jobs.setBolUrl(jsonObject.getString("bol_url"));
            jobs.setLatLngPoints(jsonObject.getString("shipment_google_root"));

            jobs.setTruckloadName(jsonObject.getString("truck_type_name"));
            jobs.setTruckloadMaxWeight(jsonObject.getInt("truck_max_weight"));
            jobs.setShipperName(jsonObject.getString("shipper_name"));
            jobs.setShipperPhone(jsonObject.getString("shipper_phone"));

            jobs.setOriginSpecialSiteInstructions(jsonObject.getString("origin_special_site_instructions"));
            jobs.setDestinationSpecialSiteInstructions(jsonObject.getString("destination_special_site_instructions"));
            jobs.setDriverNotes(jsonObject.getString("driver_notes"));

            jobs.setRating((float) jsonObject.getDouble("rating"));
            jobs.setFeedback(jsonObject.getString("feedback"));

            jobs.setIsPopupShow(jsonObject.getInt("arrived_at_pickup_popup_show") == 1);

            return jobs;
        }
    public Offer fillOffer(JSONObject jsonObject) throws JSONException {

        Offer offer=new Offer();

        offer.setId(jsonObject.getLong("id"));
        offer.setStatusName(Html.fromHtml(jsonObject.getString("status_name")).toString());

        offer.setPickupDate(jsonObject.getString("pickup_date"));
        offer.setPickupFromTime(jsonObject.getString("pickup_from_time"));
        offer.setPickupTillTime(jsonObject.getString("pickup_till_time"));

        offer.setDropoffDate(jsonObject.getString("dropoff_date"));
        offer.setDropoffFromTime(jsonObject.getString("dropoff_from_time"));
        offer.setDropoffTillTime(jsonObject.getString("dropoff_till_time"));

        offer.setOriginAddress(Html.fromHtml(jsonObject.getString("origin_address")).toString());
        offer.setOriginAddressName(jsonObject.getString("origin_address_name"));
        offer.setDestinationAddress(Html.fromHtml(jsonObject.getString("destination_address")).toString());
        offer.setDestinationAddressName(jsonObject.getString("destination_address_name"));

        offer.setTotalLoadWeightText(jsonObject.getLong("total_load_weight"));
        offer.setTotalWeightType(jsonObject.getString("load_weight_type"));
        offer.setShipmentShipperPaymentText(jsonObject.getString("shipment_carrier_payout_text"));
        offer.setTotalDrivingDistance(jsonObject.getString("total_driving_distance"));

        offer.setOriginLat(jsonObject.getDouble("origin_lat"));
        offer.setOriginLng(jsonObject.getDouble("origin_lng"));
        offer.setDestinationLat(jsonObject.getDouble("destination_lat"));
        offer.setDestinationLng(jsonObject.getDouble("destination_lng"));

        offer.setCircleColor(Html.fromHtml(jsonObject.getString("status_name_circle_color")).toString());
        offer.setNumberBoxes(jsonObject.getInt("box_num"));
        offer.setNumberPallets(jsonObject.getInt("pallet_num"));
        offer.setNumberTruckload(jsonObject.getInt("truckload_num"));

        String specialRequestsIcons=jsonObject.getString("shipment_special_request");
        if (!specialRequestsIcons.equals("null")&&!specialRequestsIcons.equals(""))
            offer.setSpecialRequest(Arrays.asList(specialRequestsIcons.split(",")));
        offer.setPickupIn(jsonObject.getString("pickup_in_time"));
        offer.setComments(jsonObject.getString("comments"));
        String loads=jsonObject.getString("shipment_load");
        if (!loads.equals("null"))
            offer.setLoads(Arrays.asList(loads.split(",")));
        offer.setLatLngPoints(jsonObject.getString("shipment_google_root"));

        offer.setTruckloadName(jsonObject.getString("truck_type_name"));

        offer.setOriginSpecialSiteInstructions(jsonObject.getString("origin_special_site_instructions"));
        offer.setDestinationSpecialSiteInstructions(jsonObject.getString("destination_special_site_instructions"));
        return offer;
    }

    public void closeApplication(final Context context){

        final Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    public String getUniqueNumber(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;

    }

    /*Context context,boolean cancelAble,String title,String content,String okText,
                                 final String cancelText, final List<View> contentLayout, final PopupInterface popupInterface*/

    public void showVersionError(final Context context){
        openPopupWindow(context,
                true,
                context.getString(R.string.notice),
                context.getString(R.string.change_last_version_err),
                context.getString(R.string.update),
                context.getString(R.string.cancel),
                null,
                new PopupInterface() {

                    @Override
                    public void onOk() {
                        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
    }
}
