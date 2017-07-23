package com.shtibel.truckies.jsonParser;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Shtibel on 01/05/2016.
 */
public class JsonParser {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final String IMGUR_CLIENT_ID = "9199fdef135c122";
    private static final MediaType PNG = MediaType.parse("image/png");

    public static ResultEntity requestPostOkHttp(String url,String json){

        ResultEntity resultEntity=new ResultEntity();
        if (hasInternetConnection()) {
            OkHttpClient client = new OkHttpClient();

            Charset charset = JSON.charset();
            final byte[] bytes = json.getBytes(charset);
            CustomRequestBody customRequestBody = new CustomRequestBody(JSON, bytes, new ProgressListener() {

                @Override
                public void transferred(long num) {

                }
            });

            Request request = new Request.Builder()
                    .url(url)
                    .post(customRequestBody)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                resultEntity.setIsOk(true);
                resultEntity.setResult(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                resultEntity.setIsOk(false);
                resultEntity.setResult(e.toString());
            }
        }
        else {
            resultEntity.setIsOk(false);
            resultEntity.setResult("no internet connection");
        }
        return resultEntity;
    }


    public static ResultEntity requestGetOkHttp(String url){

        ResultEntity resultEntity=new ResultEntity();

        if (hasInternetConnection()) {
//            OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                resultEntity.setIsOk(true);
                resultEntity.setResult(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                resultEntity.setIsOk(false);
                resultEntity.setResult(e.toString());
            }
        }
        else {
            resultEntity.setIsOk(false);
            resultEntity.setResult(StartApplication.getAppContext().getString(R.string.no_internet));
        }
        return resultEntity;

    }


    public static ResultEntity uploadImage(String url, File sourceFile,String fileName) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();



        //OkHttpClient client = new OkHttpClient();
        client.connectTimeoutMillis();
        ResultEntity resultEntity=new ResultEntity();
        if (hasInternetConnection()) {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("title", "tmp_name")
//                    .addFormDataPart("file", "tmp_name")
//                    .addFormDataPart("image", sourceFile.getName(),
//                            RequestBody.create(PNG, sourceFile))
                    .addFormDataPart("file",fileName, RequestBody.create(PNG, sourceFile))
                    .build();

            Request request = new Request.Builder()
                    //.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                resultEntity.setIsOk(true);
                resultEntity.setResult(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                resultEntity.setIsOk(false);
                resultEntity.setResult(e.toString());
            }
        }
        else {
            resultEntity.setIsOk(false);
            resultEntity.setResult("no internet connection");
        }

        return resultEntity;
    }


    public static boolean hasInternetConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) StartApplication.getAppContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
