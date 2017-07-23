package com.shtibel.truckies.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shtibel.truckies.R;

public class AgreeTermsActivity extends AppCompatActivity {

    AppCompatCheckBox agreeCheckbox;
    ImageView mainImage;
    TextView agreeTermsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree_terms);

        initView();
    }

    private void initView() {
        agreeCheckbox= (AppCompatCheckBox) findViewById(R.id.agree_terms_agree_terms);
        boolean isAgree=getIntent().getBooleanExtra("agreeCheckbox", false);
        agreeCheckbox.setChecked(isAgree);

        mainImage= (ImageView) findViewById(R.id.agree_terms_image);
        int res=getIntent().getIntExtra("imageBgRes",0);
        mainImage.setImageResource(res);

        agreeTermsText= (TextView) findViewById(R.id.agree_terms_text);
        String agreeTerms=getIntent().getStringExtra("termsText");
        agreeTermsText.setText(agreeTerms);
    }

    public void goBack(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("agreeCheckbox", agreeCheckbox.isChecked());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("agreeCheckbox", agreeCheckbox.isChecked());
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}
