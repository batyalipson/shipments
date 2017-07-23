package com.shtibel.truckies.activities.job;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;

public class SpecialLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_load);
        String url=getIntent().getStringExtra("url");
        ImageView specialImage= (ImageView) findViewById(R.id.specials_image);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(StartApplication.START_URL +url, specialImage);
    }
    public void close(View view){
        finish();
    }
}
