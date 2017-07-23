package com.shtibel.truckies.activities.job;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.asyncTasks.GetLoadsTypeAsyncTask;
import com.shtibel.truckies.asyncTasks.SendConfirmationsAsyncTask;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JobConfirmation extends AppCompatActivity {

    Utils utils=new Utils();
    List<LoadType>loadTypes=new ArrayList<>();
    TextView title;
    LinearLayout loadsTypesContainer;
    EditText totalWeight;
    Button plusButton;
    EditText senderName;
    EditText confirmationPhone;
    int loadsCounter=0;
    int numberOfLoads=0;
    View.OnClickListener deleteLoadTypeListener;
    int status=0;
    Jobs job;
    SignaturePad signaturePad;
    ImageView signatureImg;
    Button signatureBtn;

    RelativeLayout driverConfWrapper;
    Button driverConf;
    ProgressBar confProgress;

    View.OnClickListener driverConfListener;
    Button close;
    RelativeLayout addTypeButton;
    LinearLayout showMoreDetails;
    TextView driverName;
    TextView date;
    TextView signatureTxt;

    UserDetailsChangedReceiver userDetailsChangedReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_confirmation);

        status=getIntent().getIntExtra("status", 0);
        job= (Jobs) getIntent().getSerializableExtra("job");
        initDeleteListener();
        initDriverConfListener();
        initViews();
        getLoadsType();
    }


    public void getLoadsType() {
        addTypeButton.setClickable(false);
        GetLoadsTypeAsyncTask getLoadsTypeAsyncTask=new GetLoadsTypeAsyncTask(this, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                try {
                    JSONArray jsonArray=new JSONArray(result.getResult());
                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        LoadType loadType=new LoadType();
                        loadType.setId(jsonObject.getInt("id"));
                        loadType.setName(jsonObject.getString("load_type_name"));
                        loadType.setIsDefault(jsonObject.getInt("default_load"));
                        loadTypes.add(loadType);
                        //if(loadType.getIsDefault()==1)


                    }
                    if (status==JobActivity.CONFIRMATIONS_PICKUP||status==JobActivity.CONFIRMATIONS_DROPOFF) {
                        addTypeButton.setClickable(true);
                    }

                    if (status==JobActivity.CONFIRMATIONS_PICKUP){
                        addLoadType();
                    }
                    else {
                        fillLoadTypes();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                utils.showServerError(JobConfirmation.this,resultEntity.getResult());
            }
        });
        getLoadsTypeAsyncTask.execute();
    }

    private void fillLoadTypes() {

        if (status==JobActivity.CONFIRMATIONS_SHOW_PICKUP){
            for (int i=0;i<job.getLoadTypesPickup().size();i++){
                int loadId=job.getLoadTypesPickup().get(i);
                int loadQuantity=job.getLoadTypesQuantityPickup().get(i);
                LoadType loadType=getLoadTypeById(loadId);
                if (loadType!=null) {
                    loadType.setQuantity(loadQuantity);
                    addShownOnlyLoadType(loadType);
                }

            }
        }
        else if (status==JobActivity.CONFIRMATIONS_SHOW_DROPOFF){
            for (int i=0;i<job.getLoadTypesDropoff().size();i++){
                int loadId=job.getLoadTypesDropoff().get(i);
                int loadQuantity=job.getLoadTypesQuantityDropoff().get(i);
                LoadType loadType=getLoadTypeById(loadId);
                if (loadType!=null) {
                    loadType.setQuantity(loadQuantity);
                    addShownOnlyLoadType(loadType);
                }

            }
        }

        else if (status==JobActivity.CONFIRMATIONS_DROPOFF){
            for (int i=0;i<job.getLoadTypesPickup().size();i++){
                int loadId=job.getLoadTypesPickup().get(i);
                int loadQuantity=job.getLoadTypesQuantityPickup().get(i);
                LoadType loadType=getLoadTypeById(loadId);
                if (loadType!=null) {
//                    loadType.setQuantity(loadQuantity);
                    addLoadType(loadType,loadQuantity);
                }

            }
        }
    }

    private LoadType getLoadTypeById(int id){
        for (LoadType loadType:loadTypes){
            if (loadType.getId()==id)
                return loadType;
        }

        return null;
    }

    private void addLoadType() {

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.conf_field_height));
        if (numberOfLoads>0)
            linearLayoutParams.setMargins(0,(int)getResources().getDimension(R.dimen.small_margin),0,0);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setTag("linearLayout" + loadsCounter);
//
        RelativeLayout spinnerRelativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams spinRelativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3.5f);
        spinRelativeLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinnerRelativeLayout.setLayoutParams(spinRelativeLayoutParams);

        ImageView imageView=new ImageView(this);
        RelativeLayout.LayoutParams imageLayoutParams=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.x_small_icon), (int)getResources().getDimension(R.dimen.x_small_icon));
        imageLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageResource(R.drawable.arrow_user);

        final Spinner spinner=new Spinner(this);
        LinearLayout.LayoutParams spinnerLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //spinnerLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinner.setLayoutParams(spinnerLayoutParams);
        spinner.setBackgroundResource(R.drawable.style_offers_jobs_content);
        spinner.setTag("spinner" + loadsCounter);
        ArrayAdapter<LoadType> dataAdapter = new ArrayAdapter<LoadType>(JobConfirmation.this, R.layout.spinner_textview, loadTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                LoadType loadType= (LoadType) spinner.getSelectedItem();
//                loadType.setIsSelected(false);
                setRightSelections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i=0;i<loadTypes.size();i++){
            if (!loadTypes.get(i).isSelected) {
                spinner.setSelection(i);
                loadTypes.get(i).setIsSelected(true);
                i=loadTypes.size();
            }
        }

        spinnerRelativeLayout.addView(spinner);
        spinnerRelativeLayout.addView(imageView);


        EditText editText=new EditText(this);
        LinearLayout.LayoutParams editTextLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f);
        editTextLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        editText.setLayoutParams(editTextLayoutParams);
        editText.setBackgroundResource(R.drawable.style_offers_jobs_content);
        editText.setHintTextColor(Color.parseColor("#a8a8a8"));
        editText.setHint(getString(R.string.quantity));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        editText.setTextColor(Color.parseColor("#656565"));
        editText.setGravity(Gravity.CENTER);
        editText.setPadding((int) getResources().getDimension(R.dimen.small_padding), 0, (int) getResources().getDimension(R.dimen.small_padding), 0);
        editText.setTag("editText" + loadsCounter);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //android:imeOptions="actionDone"

        RelativeLayout delRelativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams relativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
        delRelativeLayout.setLayoutParams(relativeLayoutParams);
        delRelativeLayout.setOnClickListener(deleteLoadTypeListener);


        Button deleteButton=new Button(this);
        RelativeLayout.LayoutParams deleteButtonParams=
                new RelativeLayout.LayoutParams((int)(getResources().getDimension(R.dimen.delete_button)), (int)(getResources().getDimension(R.dimen.delete_button)));
        deleteButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        deleteButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setPadding(0, 0, 0, 0);
        deleteButton.setBackgroundResource(R.drawable.style_switch_btn);
        deleteButton.setText("X");
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        deleteButton.setTextColor(Color.RED);
        deleteButton.setTag("deleteButton" + loadsCounter);
        deleteButton.setClickable(false);

        delRelativeLayout.addView(deleteButton);
        if (numberOfLoads==0)
            delRelativeLayout.setVisibility(View.INVISIBLE);

        linearLayout.addView(spinnerRelativeLayout);
        linearLayout.addView(editText);
        linearLayout.addView(delRelativeLayout);

        loadsTypesContainer.addView(linearLayout);

        loadsCounter++;
        numberOfLoads++;

        Log.d("numberOfLoads",numberOfLoads+"");
        Log.d("loadsCounter",loadsCounter+"");

    }

    private void addLoadType(LoadType loadType,int loadQuantity) {

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.conf_field_height));
        if (numberOfLoads>0)
            linearLayoutParams.setMargins(0,(int)getResources().getDimension(R.dimen.small_margin),0,0);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setTag("linearLayout" + loadsCounter);
//
        RelativeLayout spinnerRelativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams spinRelativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3.5f);
        spinRelativeLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinnerRelativeLayout.setLayoutParams(spinRelativeLayoutParams);

        ImageView imageView=new ImageView(this);
        RelativeLayout.LayoutParams imageLayoutParams=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.x_small_icon), (int)getResources().getDimension(R.dimen.x_small_icon));
        imageLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageResource(R.drawable.arrow_user);

        final Spinner spinner=new Spinner(this);
        LinearLayout.LayoutParams spinnerLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //spinnerLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinner.setLayoutParams(spinnerLayoutParams);
        spinner.setBackgroundResource(R.drawable.style_offers_jobs_content);
        spinner.setTag("spinner" + loadsCounter);
        ArrayAdapter<LoadType> dataAdapter = new ArrayAdapter<LoadType>(JobConfirmation.this, R.layout.spinner_textview, loadTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRightSelections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i=0;i<loadTypes.size();i++){
            if (!loadTypes.get(i).isSelected) {
                spinner.setSelection(i);
                loadTypes.get(i).setIsSelected(true);
                i=loadTypes.size();
            }
        }
        int position=loadTypes.indexOf(loadType);
        spinner.setSelection(position);

        spinnerRelativeLayout.addView(spinner);
        spinnerRelativeLayout.addView(imageView);


        EditText editText=new EditText(this);
        LinearLayout.LayoutParams editTextLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f);
        editTextLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        editText.setLayoutParams(editTextLayoutParams);
        editText.setBackgroundResource(R.drawable.style_offers_jobs_content);
        editText.setHintTextColor(Color.parseColor("#a8a8a8"));
        editText.setHint(getString(R.string.quantity));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        editText.setTextColor(Color.parseColor("#656565"));
        editText.setGravity(Gravity.CENTER);
        editText.setPadding((int) getResources().getDimension(R.dimen.small_padding), 0, (int) getResources().getDimension(R.dimen.small_padding), 0);
        editText.setTag("editText" + loadsCounter);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setText(loadQuantity + "");
        //android:imeOptions="actionDone"

        RelativeLayout delRelativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams relativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
        delRelativeLayout.setLayoutParams(relativeLayoutParams);
        delRelativeLayout.setOnClickListener(deleteLoadTypeListener);


        Button deleteButton=new Button(this);
        RelativeLayout.LayoutParams deleteButtonParams=
                new RelativeLayout.LayoutParams((int)(getResources().getDimension(R.dimen.delete_button)), (int)(getResources().getDimension(R.dimen.delete_button)));
        deleteButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        deleteButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setPadding(0, 0, 0, 0);
        deleteButton.setBackgroundResource(R.drawable.style_switch_btn);
        deleteButton.setText("X");
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        deleteButton.setTextColor(Color.RED);
        deleteButton.setTag("deleteButton" + loadsCounter);
        deleteButton.setClickable(false);

        delRelativeLayout.addView(deleteButton);
        if (numberOfLoads==0)
            delRelativeLayout.setVisibility(View.INVISIBLE);

        linearLayout.addView(spinnerRelativeLayout);
        linearLayout.addView(editText);
        linearLayout.addView(delRelativeLayout);

        loadsTypesContainer.addView(linearLayout);

        loadsCounter++;
        numberOfLoads++;

        Log.d("numberOfLoads",numberOfLoads+"");
        Log.d("loadsCounter",loadsCounter+"");

    }

    private void setRightSelections() {

        for (LoadType loadType:loadTypes){
            loadType.setIsSelected(false);
        }

        for (int i=0;i<loadsCounter;i++){
            Spinner spinner=(Spinner) loadsTypesContainer.findViewWithTag("spinner" + i);
            if (spinner!=null){
                LoadType loadType= (LoadType) spinner.getSelectedItem();
                loadType.setIsSelected(true);
            }
        }

    }

    private void addShownOnlyLoadType(LoadType loadType) {

        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.conf_field_height));
        if (numberOfLoads>0)
            linearLayoutParams.setMargins(0,(int)getResources().getDimension(R.dimen.small_margin),0,0);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setTag("linearLayout" + loadsCounter);

        RelativeLayout spinnerRelativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams spinRelativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3.5f);
        spinRelativeLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinnerRelativeLayout.setLayoutParams(spinRelativeLayoutParams);

        ImageView imageView=new ImageView(this);
        RelativeLayout.LayoutParams imageLayoutParams=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.x_small_icon), (int)getResources().getDimension(R.dimen.x_small_icon));
        imageLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageResource(R.drawable.arrow_user);
        imageView.setVisibility(View.GONE);

        final Spinner spinner=new Spinner(this);
        LinearLayout.LayoutParams spinnerLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //spinnerLayoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.small_margin), 0);
        spinner.setLayoutParams(spinnerLayoutParams);
        spinner.setBackgroundResource(R.drawable.style_offers_jobs_content);
        spinner.setTag("spinner" + loadsCounter);
        ArrayAdapter<LoadType> dataAdapter = new ArrayAdapter<LoadType>(JobConfirmation.this, R.layout.spinner_textview, loadTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        int position=loadTypes.indexOf(loadType);
        spinner.setSelection(position);
        spinner.setFocusable(false);
        spinner.setClickable(false);

        spinnerRelativeLayout.addView(spinner);
        spinnerRelativeLayout.addView(imageView);


        EditText editText=new EditText(this);
        LinearLayout.LayoutParams editTextLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.5f);
        //editTextLayoutParams.setMargins(0,0,(int)getResources().getDimension(R.dimen.small_margin),0);
        editText.setLayoutParams(editTextLayoutParams);
        editText.setBackgroundResource(R.drawable.style_offers_jobs_content);
        editText.setHintTextColor(Color.parseColor("#a8a8a8"));
        editText.setHint(getString(R.string.quantity));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        editText.setTextColor(Color.parseColor("#656565"));
        editText.setGravity(Gravity.CENTER);
        editText.setPadding((int) getResources().getDimension(R.dimen.small_padding), 0, (int) getResources().getDimension(R.dimen.small_padding), 0);
        editText.setTag("editText" + loadsCounter);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(loadType.getQuantity() + "");
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setFocusable(false);


        RelativeLayout relativeLayout=new RelativeLayout(this);
        LinearLayout.LayoutParams relativeLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.5f);
        relativeLayout.setLayoutParams(relativeLayoutParams);
        relativeLayout.setOnClickListener(deleteLoadTypeListener);


        Button deleteButton=new Button(this);
        RelativeLayout.LayoutParams deleteButtonParams=
                new RelativeLayout.LayoutParams((int)(getResources().getDimension(R.dimen.delete_button)), (int)(getResources().getDimension(R.dimen.delete_button)));
        deleteButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        deleteButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        deleteButton.setLayoutParams(deleteButtonParams);
        deleteButton.setPadding(0, 0, 0, 0);
        deleteButton.setBackgroundResource(R.drawable.style_switch_btn);
        deleteButton.setText("X");
        deleteButton.setGravity(Gravity.CENTER);
        deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_sub_title));
        deleteButton.setTextColor(Color.RED);
        deleteButton.setTag("deleteButton" + loadsCounter);
        deleteButton.setClickable(false);

        relativeLayout.addView(deleteButton);
        relativeLayout.setVisibility(View.GONE);

        linearLayout.addView(spinnerRelativeLayout);
        linearLayout.addView(editText);
        linearLayout.addView(relativeLayout);

        loadsTypesContainer.addView(linearLayout);

        loadsCounter++;
        numberOfLoads++;

        Log.d("numberOfLoads",numberOfLoads+"");
        Log.d("loadsCounter",loadsCounter+"");

    }

    private void initViews() {
        title= (TextView) findViewById(R.id.job_conf_title);
        String titleTxt="";
        if (status==JobActivity.CONFIRMATIONS_PICKUP||status==JobActivity.CONFIRMATIONS_SHOW_PICKUP)
            titleTxt+=getString(R.string.pickup_t);
        else
            titleTxt+=getString(R.string.dropoff_t);
        titleTxt+=" ";
        titleTxt+=getString(R.string.job_confirmation);
        titleTxt+=" ";
        titleTxt+="#"+job.getId();
        title.setText(titleTxt);
        loadsTypesContainer= (LinearLayout) findViewById(R.id.job_conf_load_types);
        totalWeight= (EditText) findViewById(R.id.job_conf_total_weight);
        signatureTxt= (TextView) findViewById(R.id.job_conf_signature_txt);
        senderName= (EditText) findViewById(R.id.job_conf_sender_name);
        if (status==JobActivity.CONFIRMATIONS_PICKUP) {
            totalWeight.setText(job.getTotalLoadWeightText() + "");
            signatureTxt.setText(getString(R.string.sender_signature));
            senderName.setHint(getString(R.string.sender_name));
        }
        else if (status==JobActivity.CONFIRMATIONS_DROPOFF) {
            totalWeight.setText(job.getTotalWeightPickup() + "");
            signatureTxt.setText(getString(R.string.receiver_signature));
            senderName.setHint(getString(R.string.receiver_name));
        }

        plusButton= (Button) findViewById(R.id.job_conf_plus_btn);
        plusButton.setClickable(false);

        confirmationPhone= (EditText) findViewById(R.id.job_conf_confirmation_phone);
        if (status==JobActivity.CONFIRMATIONS_PICKUP||status==JobActivity.CONFIRMATIONS_DROPOFF)
            initConfirmationPhoneListener();
        signaturePad= (SignaturePad) findViewById(R.id.job_conf_signature_pad);
        signatureImg= (ImageView) findViewById(R.id.job_conf_signature_img);
        signatureImg.setVisibility(View.GONE);
        signatureBtn= (Button) findViewById(R.id.job_conf_clear_signature);

        driverConf= (Button) findViewById(R.id.job_conf_driver_conf);
        driverConfWrapper= (RelativeLayout) findViewById(R.id.job_conf_driver_conf_wrapper);
        driverConf.setOnClickListener(driverConfListener);
        confProgress= (ProgressBar) findViewById(R.id.job_conf_progress);

        close= (Button) findViewById(R.id.job_conf_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobConfirmation.this.finish();
            }
        });

        addTypeButton= (RelativeLayout) findViewById(R.id.job_conf_add_type);
        addTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfLoads<loadTypes.size())
                    addLoadType();
            }
        });

        showMoreDetails= (LinearLayout) findViewById(R.id.job_conf_more_details);
        showMoreDetails.setVisibility(View.GONE);


        if (status==JobActivity.CONFIRMATIONS_SHOW_PICKUP){
            fillPickupDetails();
        }
        else if (status==JobActivity.CONFIRMATIONS_SHOW_DROPOFF){
            fillDropoffDetails();
        }
    }

    private void fillPickupDetails() {

        addTypeButton.setVisibility(View.INVISIBLE);
        totalWeight.setText(job.getTotalWeightPickup() + "");
        totalWeight.setFocusable(false);

        senderName.setText(getString(R.string.sender_name_title) + "  " + job.getReceiverNamePickup());
        senderName.setFocusable(false);

        if (job.getConfPhonePickup().equals("")||job.getConfPhonePickup().equals("null")){
            confirmationPhone.setVisibility(View.GONE);
        }
        else {
            confirmationPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2000)});
            confirmationPhone.setText(getString(R.string.sms_phone_title)+"  "+job.getConfPhonePickup());
            confirmationPhone.setFocusable(false);

        }
        //confirmationPhone.setHint("");

        signatureImg.setVisibility(View.VISIBLE);
        initImage(signatureImg, StartApplication.START_URL + "/" + job.getSignatureUrlPickup());
        signaturePad.setVisibility(View.GONE);
        signatureBtn.setVisibility(View.GONE);

        showMoreDetails.setVisibility(View.VISIBLE);
        driverName= (TextView) findViewById(R.id.job_conf_driver_name);
        driverName.setText(job.getDriverNamePickup());

        date= (TextView) findViewById(R.id.job_conf_date);
        date.setText(job.getConfDatePickup());

        driverConf.setVisibility(View.GONE);
        driverConfWrapper.setVisibility(View.GONE);
    }

    private void fillDropoffDetails() {

        addTypeButton.setVisibility(View.INVISIBLE);
        totalWeight.setText(job.getTotalWeightDropoff() + "");
        totalWeight.setFocusable(false);

        senderName.setText(getString(R.string.receiver_name_title) + "  " + job.getReceiverName());
        senderName.setFocusable(false);

        if (job.getConfPhoneDropoff().equals("")||job.getConfPhoneDropoff().equals("null")){
            confirmationPhone.setVisibility(View.GONE);
        } else {
            confirmationPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2000)});
            confirmationPhone.setText(getString(R.string.sms_phone_title)+"  "+job.getConfPhoneDropoff());
            confirmationPhone.setFocusable(false);

        }
        //confirmationPhone.setHint("");

        signatureImg.setVisibility(View.VISIBLE);
        initImage(signatureImg, StartApplication.START_URL + "/" + job.getSignatureUrl());
        signaturePad.setVisibility(View.GONE);
        signatureBtn.setVisibility(View.GONE);

        showMoreDetails.setVisibility(View.VISIBLE);
        driverName= (TextView) findViewById(R.id.job_conf_driver_name);
        driverName.setText(job.getDriverNameDropoff());

        date= (TextView) findViewById(R.id.job_conf_date);
        date.setText(job.getConfDateDropoff());

        driverConf.setVisibility(View.GONE);
        driverConfWrapper.setVisibility(View.GONE);

    }

    private void initConfirmationPhoneListener() {

        confirmationPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length()-confirmationPhone.getSelectionStart();
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
                        confirmationPhone.setText(ans);
                        confirmationPhone.setSelection(confirmationPhone.getText().length()-cursorComplement);
                    }
                    else if (phoneS.length() >= 2 && !backspacingFlag) {
                        editedFlag = true;
//                        String ans = "(" +phoneS.substring(0, 2) + ") " + phoneS.substring(2);
                        String ans = phoneS.substring(0, 2) + "-" + phoneS.substring(2);
                        confirmationPhone.setText(ans);
                        confirmationPhone.setSelection(confirmationPhone.getText().length()-cursorComplement);
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false;
                }

            }
        });


    }

    private void initDeleteListener() {
        deleteLoadTypeListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parentView= (LinearLayout) v.getParent();
//                for (int i=0;i<parentView.getChildCount();i++){
//                    if (parentView.getChildAt(i) instanceof Spinner){
//                        Spinner spinner= (Spinner) parentView.getChildAt(i);
//                        LoadType loadType= (LoadType) spinner.getSelectedItem();
//                        loadType.setIsSelected(false);
//                    }
//                }
                loadsTypesContainer.removeView(parentView);
                numberOfLoads--;
                setRightSelections();
                Log.d("numberOfLoads", numberOfLoads + "");
                Log.d("loadsCounter",loadsCounter+"");
            }
        };
    }

    private void initDriverConfListener() {
        driverConfListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                //check validation

                //set default quantity value
                for (LoadType loadType:loadTypes){
                    loadType.setQuantity(0);
                }
                for (int i=0;i<loadsCounter;i++){
                    Spinner spinner= (Spinner) loadsTypesContainer.findViewWithTag("spinner" + i);
                    EditText editText= (EditText) loadsTypesContainer.findViewWithTag("editText"+i);
                    if(spinner!=null&&editText!=null){
                        LoadType loadType= (LoadType) spinner.getSelectedItem();
                        String quantityStr=editText.getText().toString();
                        if (quantityStr.equals("")||quantityStr.equals("0")){
                            isValid=false;
                            editText.setError(getString(R.string.required_field));
                        }
                        else {
                            try {
                                int quantity = Integer.parseInt(quantityStr);
                                loadType.setQuantity(loadType.getQuantity() + quantity);
                            } catch (Exception e) {

                            }
                        }


                    }
                }
                String totalWeightStr=totalWeight.getText().toString();
                int totalWeightNum = 0;
                if (totalWeightStr.equals("")){
                    isValid=false;
                    totalWeight.setError(getString(R.string.required_field));
                }

                String senderNameStr=senderName.getText().toString();
                if (senderNameStr.equals("")){
                    isValid=false;
                    senderName.setError(getString(R.string.required_field));
                }
                String phoneStr="";
                if (isValid){
                    phoneStr= confirmationPhone.getText().toString().replaceAll("[^\\d]", "");
                    if (phoneStr.length()<10&&phoneStr.length()>0) {
                        isValid = false;
                        utils.openPopupWindow(JobConfirmation.this,
                                true,
                                getString(R.string.notice),
                                getString(R.string.phone_format_error),
                                getString(R.string.ok),
                                "",
                                null,
                                new PopupInterface() {
                                });
                    }
                }
                if (isValid){
                    try {
                        totalWeightNum = Integer.parseInt(totalWeightStr);
                        if (totalWeightNum > job.getTruckloadMaxWeight()) {
                            isValid = false;
                            utils.openPopupWindow(JobConfirmation.this,
                                    true,
                                    getString(R.string.notice),
                                    getString(R.string.truck_over_max_weight),
                                    getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                    });
                        }
                    } catch (Exception e) {
                        isValid = false;


                    }
                }
                if (isValid&&signaturePad.isEmpty()){
                    isValid=false;
                    utils.openPopupWindow(JobConfirmation.this,
                            true,
                            getString(R.string.notice),
                            getString(R.string.empty_signature),
                            getString(R.string.ok),
                            "",
                            null,
                            new PopupInterface() {
                            });
                }


                if (isValid){
                    //TODO send all the details
                    //String type,int totalWeight,String senderName,String confirmationPhone,long jobId,File signature,List<LoadType> loadTypes
                    File signatureFile=utils.getFileFromBitmap(signaturePad.getSignatureBitmap(), "signature", JobConfirmation.this);
                    final String type;
                    if (status==JobActivity.CONFIRMATIONS_PICKUP)
                        type="pickup";
                    else
                        type="dropoff";

                    confProgress.setVisibility(View.VISIBLE);
                    driverConf.setClickable(false);
                    SendConfirmationsAsyncTask sendConfirmationsAsyncTask=new SendConfirmationsAsyncTask(JobConfirmation.this,
                            type,
                            totalWeightNum,
                            senderNameStr,
                            phoneStr,
                            job.getId(),
                            signatureFile,
                            loadTypes,
                            new IResultsInterface() {
                                @Override
                                public void onCompleteWithResult(ResultEntity result) {
                                    driverConf.setClickable(true);
                                    confProgress.setVisibility(View.GONE);

                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(result.getResult());
                                        JSONObject jobJson = jsonObject.getJSONObject("job");
                                        job = utils.fillJob(jobJson);
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        returnIntent.putExtra("status", status);
                                        returnIntent.putExtra("job",job);
                                        returnIntent.putExtra("isSavedSuccessfully",true);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        utils.showServerError(JobConfirmation.this, result.getResult());
                                    }


                                }

                                @Override
                                public void onErrorWithResult(ResultEntity resultEntity) {
                                    driverConf.setClickable(true);
                                    confProgress.setVisibility(View.GONE);
                                    utils.showServerError(JobConfirmation.this, resultEntity.getResult());
                                }
                            });

                    sendConfirmationsAsyncTask.execute();
                }

            }
        };
    }

//    public void addType(View view){
//        if (numberOfLoads<loadTypes.size())
//            addLoadType();
//    }
    public void close(View v){
        finish();
    }
    public void clearPad(View v){
        signaturePad.clear();
    }


    private void initImage(final ImageView imageView, final String url) {

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnFail(null)
                .showImageForEmptyUri(null)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();

        imageLoader.displayImage(url, imageView, defaultOptions);
    }


    private class UserDetailsChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AlarmReceiver.APPLICATION_DETAILS_CHANGED)) {
                //TODO close application after show message
                utils.openPopupWindow(JobConfirmation.this,
                        false,
                        getString(R.string.notice),
                        getString(R.string.user_details_changed_message),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {

                            @Override
                            public void onOk() {
                                utils.closeApplication(JobConfirmation.this);
                            }
                        });
            }
        }
    }

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
