package com.shtibel.truckies.activities.job;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ogaclejapan.arclayout.ArcLayout;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabJobs;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.asyncTasks.GetPathAsyncTask;
import com.shtibel.truckies.asyncTasks.GetShareLinkAsyncTask;
import com.shtibel.truckies.asyncTasks.GetSpecificJobAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateJobNotesAsyncTask;
import com.shtibel.truckies.asyncTasks.UpdateJobStatus;
import com.shtibel.truckies.asyncTasks.UploadImageAsyncTask;
import com.shtibel.truckies.asyncTasks.UploadJobImagesAsyncTask;
import com.shtibel.truckies.generalClasses.AnimatorUtils;
import com.shtibel.truckies.generalClasses.LatLngInterpolator;
import com.shtibel.truckies.generalClasses.MarkerAnimation;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.SpecialRequest;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.pushNotifications.MyFcmListenerService;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.LocationService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.shtibel.truckies.R.id.job_camera_btn;

public class JobActivity extends AppCompatActivity  implements OnMapReadyCallback,SensorEventListener,View.OnClickListener {

    public static final int TAKE_IMAGE_TO_UPLOAD=123;
    public static final int CONFIRMATIONS_PICKUP=1;
    public static final int CONFIRMATIONS_DROPOFF=2;
    public static final int CONFIRMATIONS_SHOW_PICKUP=3;
    public static final int CONFIRMATIONS_SHOW_DROPOFF=4;


    Jobs job;
    Utils utils=new Utils();

    TextView title;
    SupportMapFragment mapFragment;
    boolean isMapLockOnLocation =false;
    private GoogleMap mMap=null;
    Marker sourceMarker=null;
    Marker destinationMarker=null;
    Marker currentMarker=null;
    private DataUpdateReceiver dataUpdateReceiver;

    Location lastLocation=null;
    SensorManager sensorManager;
    private float[] mRotationMatrix = new float[16];
    double mDeclination = 0;
    boolean isMapLoad=false;
    private LatLngInterpolator mLatLngInterpolator;

    RelativeLayout touchLayer;
    long pressStartTime;
    float pressedX;
    float pressedY;
    private static final long MAX_CLICK_DURATION = 300;
    private static final float MAX_CLICK_DISTANCE = 10f;

    int permissionCheck;

    TextView originAddress;
    TextView destinationAddress;
    TextView pickupDate;
    TextView dropoffDate;

    TextView specialInstructionOrigin;
    TextView specialInstructionDest;

    LinearLayout contactOriginLayout;
    TextView contactOriginName;
    TextView contactOriginPhone;
    LinearLayout contactDestLayout;
    TextView contactDestName;
    TextView contactDestPhone;

    RelativeLayout shipperDetailsLayout;
    TextView shipperName;
    TextView shipperPhone;


    LinearLayout loadsContainer;

    RelativeLayout specialRequestsLayout;
    LinearLayout bigSpecialRequest;

    RelativeLayout commentsLayout;
    TextView comments;

    //TextView signature;
//    View circleStatus;
//    TextView statusName;

//    //TextView bol;
//    TextView pickUpInTime;

    /*TextView pickupInTxt;
    TextView pickupIn;
    TextView distance;
    TextView payment;
    TextView weight;*/
    ImageView generalDateIcon;
    View generalStatusIcon;
    TextView generalText;
    TextView payment;
    ImageView boxIcon;
    TextView boxText;
    ImageView palletIcon;
    TextView palletsText;
    LinearLayout smallSpecialRequest;
    float bearing;

//    ArcMenu arcMenu;
    ImageView fab;
    boolean isShown=false;
    ArcLayout arcLayout;
//    ImageView boxes;
//    ImageView pallets;



    int screenWidth;
    int screenHeight;
    ScrollView scrollView;
    SlidingUpPanelLayout slidingUpPanelLayout;

    ImageView centerCurrentPosition;
    ImageView goToGoogleMaps;
    ImageView share;
    ProgressBar shareProgress;
    RelativeLayout shareWrapper;
    ImageView cameraImg;
    ProgressBar takImageProgress;
    ProgressBar noteProgress;

    MediaRecorder mediaRecorder;
    boolean isRecording=false;
    String path= Environment.getExternalStorageDirectory().getAbsolutePath();
    String recordDefaultName="job_record";

    int statusToChange;
    Button changeStatus;
    boolean changeStatusVisibility=true;
    ProgressBar statusProgress;
    Handler handler = new Handler();

//    SignaturePad signaturePad;
//    EditText receiverName;
    //RelativeLayout signatureLayer;
//    Bitmap signatureBitmap=null;
//    ImageView signatureImage;

    RelativeLayout bolLayer;
    ImageView bolImage;
    File bolCaptureImage=null;
    Button captureBolBtn;

    RelativeLayout progressLayout;

    Button signaturePBtn;
    Button signatureDBtn;
    Button bolButton;
    RelativeLayout rootView;

    int pressedButton;
    Polyline polyline=null;

    RelativeLayout footer;

//    Button saveSignature;
    Button saveBol;

    String phoneNumber="";
    boolean isFirstTime=true;
    Uri imageUri=null;
    boolean isMapReady=false;
    private LocationService locationService;
    UserDetailsChangedReceiver userDetailsChangedReceiver;
    boolean isCaptureBol=true;

    RelativeLayout ratingLayout;
    RatingBar rating;
    TextView ratingFeedback;

//    LatLngBounds bounds;
    int padding;
    boolean isCenterOnDriver=false;

    private ServiceConnection serviceConnection =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            LocationService.ServiceBinder serviceBinder = (LocationService.ServiceBinder)binder;
            locationService = serviceBinder.getService();
            locationService.setContext(JobActivity.this);
            initMap();
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        job= (Jobs) getIntent().getSerializableExtra("job");
        pressedButton=getIntent().getIntExtra("pressedButton", DashboardTabJobs.SCHEDULE);
        boolean isFromAcceptOffer=getIntent().getBooleanExtra("isFromAcceptOffer",false);
        if (isFromAcceptOffer){
            showSuccessMessage();
        }
        initAllData();

        bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter(MyFcmListenerService.CGM_BROADCAST));
        registerReceiver(this.jobArrivedPickupBroadcastReceiver,
                new IntentFilter(LocationService.CHANGE_STATUS_PICKUP_ARRIVED));
    }

    private void initAllData() {
        initArcMenu();
        initViews();
        initRating();
        initRoutData();
        initShipperDetails();
        initLoads();
        initSpecialsRequests();
        initComments();
        initSignatureBolBtns();
        initGeneralDetails();
//        initSignatureObjects();
        initBolObjects();
        initSensorManager();

    }

    private void showSuccessMessage() {
        utils.openPopupWindow(this,
                false,
                getString(R.string.notice),
                getString(R.string.offer_accept_success),
                getString(R.string.ok),
                "",
                null,
                new PopupInterface() {
                    @Override
                    public void onOk() {
                        super.onOk();
                    }
                });
    }

    private void initViews() {
        footer= (RelativeLayout) findViewById(R.id.job_footer);
        title= (TextView) findViewById(R.id.job_title);
        title.setText(getString(R.string.job_t)+"#"+job.getId());
        slidingUpPanelLayout= (SlidingUpPanelLayout) findViewById(R.id.job_sliding_layout);
        mLatLngInterpolator = new LatLngInterpolator.Linear();
        touchLayer= (RelativeLayout) findViewById(R.id.job_map_touch_layer);
        screenHeight=utils.getScreenHeight(this);
        screenWidth=utils.getScreenWidth(this);
        scrollView= (ScrollView) findViewById(R.id.job_details_scroll);
        scrollView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnViewGlobalLayoutListener(scrollView));
        centerCurrentPosition= (ImageView) findViewById(R.id.job_center_btn);
        goToGoogleMaps= (ImageView) findViewById(R.id.job_rout_btn);
        goToGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng source = null;
                LatLng dest = null;
                if (job.getStatusId() < 11) {
                    if (lastLocation != null) {
                        source = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        dest = new LatLng(sourceMarker.getPosition().latitude, sourceMarker.getPosition().longitude);
                    }
                }
                //marksPath(currentLatLng, sourceLatLng);
                else {
                    source = new LatLng(sourceMarker.getPosition().latitude, sourceMarker.getPosition().longitude);
                    dest = new LatLng(destinationMarker.getPosition().latitude, destinationMarker.getPosition().longitude);
                }
                //marksPath(sourceLatLng,destLatLng);

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" +
                                "saddr=" + source.latitude + "," + source.longitude +
                                "&daddr=" + dest.latitude + "," + dest.longitude));
                startActivity(intent);
            }
        });
        changeStatus= (Button) findViewById(R.id.job_status_btn);
        statusBtnAnim();
//        if (pressedButton==DashboardTabJobs.COMPLETE)
//            changeStatus.setVisibility(View.GONE);
        changeStatus.setOnClickListener(this);
        getStatusToChange();
        if (job.getStatusId()==15) {
            changeStatus.setText(getString(R.string.get_signature));
        }
        else if (job.getStatusId()==16) {
            changeStatus.setText(getString(R.string.get_bol));
        }
        else  if (job.getStatusId()>=17&&job.getStatusId()<=22) {
//            changeStatus.setText(getString(R.string.shipment_completed));
//            changeStatus.setClickable(false);
            centerCurrentPosition.setVisibility(View.GONE);
            changeStatusVisibility=false;
            changeStatus.setVisibility(View.GONE);

            RelativeLayout.LayoutParams fParams = (RelativeLayout.LayoutParams) fab.getLayoutParams();
            fParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            fab.setLayoutParams(fParams);

            RelativeLayout.LayoutParams aParams = (RelativeLayout.LayoutParams) arcLayout.getLayoutParams();
            aParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            arcLayout.setLayoutParams(aParams);

            //footer.setVisibility(View.GONE);
        }
        else {
            String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
            changeStatus.setText(nextStatusName);
        }

        statusProgress= (ProgressBar) findViewById(R.id.job_status_progress);
        progressLayout= (RelativeLayout) findViewById(R.id.job_progress_layer);
        rootView= (RelativeLayout) findViewById(R.id.job_root_view);

        shareWrapper= (RelativeLayout) findViewById(R.id.job_share);
        share= (ImageView) findViewById(R.id.job_share_btn);
        shareProgress= (ProgressBar) findViewById(R.id.job_share_progress);

        cameraImg= (ImageView) findViewById(job_camera_btn);
        takImageProgress= (ProgressBar) findViewById(R.id.job_take_image_progress);
        noteProgress= (ProgressBar) findViewById(R.id.job_note_progress);

        if (job.getStatusId()>=15) {
            shareWrapper.setVisibility(View.GONE);
        }
        if (job.getStatusId()>=17&&job.getStatusId()<=22) {
            goToGoogleMaps.setVisibility(View.GONE);
        }

    }

    private void statusBtnAnim(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.Pulse)
                        .duration(1000)
                        .repeat(0)
                        .playOn(changeStatus);
                statusBtnAnim();
            }
        }, 5000);
    }

    private void initRating() {

        ratingLayout= (RelativeLayout) findViewById(R.id.job_rating_layout);
        rating= (RatingBar) findViewById(R.id.job_rating_bar);
        ratingFeedback= (TextView) findViewById(R.id.job_rating_feedback);

        if (job.getStatusId()>=17&&job.getStatusId()<=22){

            if (job.getRating()>0||!job.getFeedback().equals("")){
                ratingLayout.setVisibility(View.VISIBLE);
                rating.setRating(job.getRating());
                ratingFeedback.setText(job.getFeedback());
            }
            else
                ratingLayout.setVisibility(View.GONE);
        }
        else {
            ratingLayout.setVisibility(View.GONE);
        }

    }

    private void initRoutData() {
        originAddress= (TextView) findViewById(R.id.job_origin_address);
        destinationAddress= (TextView) findViewById(R.id.job_destination_address);
        pickupDate= (TextView) findViewById(R.id.job_pickup_date);
        dropoffDate= (TextView) findViewById(R.id.job_dropoff_date);

        contactOriginLayout= (LinearLayout) findViewById(R.id.job_contact_origin_layout);
        contactOriginName= (TextView) findViewById(R.id.job_contact_origin_name);
        contactOriginPhone= (TextView) findViewById(R.id.job_contact_origin_phone);

        contactDestLayout= (LinearLayout) findViewById(R.id.job_contact_dest_layout);
        contactDestName= (TextView) findViewById(R.id.job_contact_dest_name);
        contactDestPhone= (TextView) findViewById(R.id.job_contact_dest_phone);

        originAddress.setText(job.getOriginAddress());
        destinationAddress.setText(job.getDestinationAddress());
        pickupDate.setText(job.getPickupDate() + "  " + job.getPickupFromTime() + "-" + job.getPickupTillTime());
        dropoffDate.setText(job.getDropoffDate() + "  " + job.getDropoffFromTime() + "-" + job.getDropoffTillTime());

        contactOriginName.setText(job.getOriginContactName());

        SpannableString originPhone = new SpannableString(job.getOriginContactPhone());
        originPhone.setSpan(new UnderlineSpan(), 0, originPhone.length(), 0);
        contactOriginPhone.setText(originPhone);
        contactOriginPhone.setOnClickListener(this);

        contactDestName.setText(job.getDestinationContactName());

        SpannableString destPhone = new SpannableString(job.getDestinationContactPhone());
        destPhone.setSpan(new UnderlineSpan(), 0, destPhone.length(), 0);
        contactDestPhone.setText(destPhone);
        contactDestPhone.setOnClickListener(this);

        if (!canSeeContactDetails()){
            contactOriginLayout.setVisibility(View.GONE);
            contactDestLayout.setVisibility(View.GONE);
        }
        else {
            contactOriginLayout.setVisibility(View.VISIBLE);
            contactDestLayout.setVisibility(View.VISIBLE);
        }

        specialInstructionOrigin= (TextView) findViewById(R.id.job_special_instruction_origin);
        specialInstructionDest= (TextView) findViewById(R.id.job_special_instruction_dest);
        if (!job.getOriginSpecialSiteInstructions().equals(""))
            specialInstructionOrigin.setText(job.getOriginSpecialSiteInstructions());
        else
            specialInstructionOrigin.setVisibility(View.GONE);
        if (!job.getDestinationSpecialSiteInstructions().equals(""))
            specialInstructionDest.setText(job.getDestinationSpecialSiteInstructions());
        else
            specialInstructionDest.setVisibility(View.GONE);
    }

    private void initShipperDetails() {
        shipperDetailsLayout= (RelativeLayout) findViewById(R.id.job_shipper_layout);
        shipperName= (TextView) findViewById(R.id.job_shipper_name);
        shipperPhone= (TextView) findViewById(R.id.job_shipper_phone);

        shipperName.setText(job.getShipperName());

        SpannableString shipperPhoneTxt = new SpannableString(job.getShipperPhone());
        shipperPhoneTxt.setSpan(new UnderlineSpan(), 0, shipperPhoneTxt.length(), 0);
        shipperPhone.setText(shipperPhoneTxt);
        shipperPhone.setOnClickListener(this);

        if (!canSeeContactDetails())
            shipperDetailsLayout.setVisibility(View.GONE);
        else
            shipperDetailsLayout.setVisibility(View.VISIBLE);
    }

    private boolean
    canSeeContactDetails() {

        String pickupDateStr=job.getOriginalPickupDate()+" "+job.getPickupFromTime()+":00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date pickupDat=null;
        try {
            pickupDat = format.parse(pickupDateStr);
            Calendar pickupCalendar=Calendar.getInstance();
            pickupCalendar.setTime(pickupDat);
            Calendar today=Calendar.getInstance();
            pickupCalendar.add(Calendar.HOUR_OF_DAY,-SharedPreferenceManager.getInstance(this).getTimeShowShipperInfo());
            if (today.equals(pickupCalendar)||today.after(pickupCalendar)){
               return true;
            }
            return false;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initLoads() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        loadsContainer= (LinearLayout) findViewById(R.id.job_load_items_container);
        loadsContainer.removeAllViews();
        String content=job.getTruckloadName();
        String weightTxt=" ";
        if (job.getTotalWeightType().equalsIgnoreCase("t")) {
            weightTxt+=new DecimalFormat(".#").format(job.getTotalLoadWeightText())+ getString(R.string.ton);
        }
        else {
            weightTxt+=job.getTotalLoadWeightText() + getString(R.string.kg);
        }
        content+=weightTxt;

        LinearLayout loadLayout=new LinearLayout(this);
//        layoutParams.setMargins(0,(int) getResources().getDimension(R.dimen.small_margin),0,0);

        TextView text=new TextView(this);
        text.setText(content);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_normal));
        text.setTextColor(getResources().getColor(R.color.text_grey));
        textParams.setMargins(0, 0, 0, 0);
        text.setLayoutParams(textParams);

        loadLayout.addView(text);

        loadsContainer.addView(loadLayout);
        loadLayout.setLayoutParams(layoutParams);

//        int imageSize=(int)getResources().getDimension(R.dimen.xx_small_icon);
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
//        boolean isFirst=true;
//        for (String load:job.getLoads()){
//
//            String[] separated = load.split(":");
//            String type=separated[0];
//            String content=separated[1].trim();
//            int index=content.lastIndexOf(' ');
//


            //calculate weight
//            double weightInTon=job.getTotalLoadWeightText()/1000.0;



//            if (!isFirst)
//
//            else
//                isFirst=false;


//            ImageView image=new ImageView(this);
//            image.setLayoutParams(imageParams);
//
//            if (type.equals("box"))
//                image.setImageResource(R.drawable.box);
//            else if (type.equals("pallet"))
//                image.setImageResource(R.drawable.pallet);
//            else
//                image.setImageResource(R.drawable.truckload);
//            image.setVisibility(View.GONE);
//            loadLayout.addView(image);


//        }
    }

    private void initSpecialsRequests() {
        specialRequestsLayout= (RelativeLayout) findViewById(R.id.job_specials_layout);
        bigSpecialRequest= (LinearLayout) findViewById(R.id.job_specials_big_images);
        if (job.getSpecials().size()>0) {
            bigSpecialRequest.removeAllViews();
            boolean isFirst = true;
            for (final SpecialRequest specialRequest : job.getSpecials()) {
                LinearLayout linearLayout=new LinearLayout(this);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (isFirst)
                    isFirst = false;
                else
                    params.setMargins((int) getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                ImageView imageView = new ImageView(this);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(StartApplication.START_URL + specialRequest.getImageResource(), imageView);
                int size = (int) getResources().getDimension(R.dimen.small_icon);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(size, size);
                imageView.setLayoutParams(imageParams);
                linearLayout.addView(imageView);

                if (!specialRequest.getImageLoafFile().equals("")) {
                    TextView textView = new TextView(this);
                    textView.setText(getString(R.string.view));
                    textView.setTextColor(getResources().getColor(R.color.blue_bg));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_normal));
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(textParams);
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent();
                            intent.setClass(JobActivity.this,SpecialLoadActivity.class);
                            intent.putExtra("url", specialRequest.getImageResource());
                            startActivity(intent);
                        }
                    });
                }

                bigSpecialRequest.addView(linearLayout);

            }
        }
        else
            specialRequestsLayout.setVisibility(View.GONE);
    }

    private void initComments() {
        commentsLayout= (RelativeLayout) findViewById(R.id.job_comments_layout);
        comments= (TextView) findViewById(R.id.job_comments_txt);
        comments.setText(job.getComments());
        if (job.getComments().equals(""))
            commentsLayout.setVisibility(View.GONE);
    }

    private void initSignatureBolBtns(){
        signaturePBtn= (Button) findViewById(R.id.job_get_signature_p);
        signatureDBtn= (Button) findViewById(R.id.job_get_signature_d);
        bolButton= (Button) findViewById(R.id.job_get_bol);

        if (job.getStatusId()>=11){
            signaturePBtn.setVisibility(View.VISIBLE);

            signaturePBtn.setBackgroundResource(R.drawable.style_tab_full_on);
        }
        else
            signaturePBtn.setVisibility(View.GONE);
        if (job.getStatusId()>15){
            signatureDBtn.setVisibility(View.VISIBLE);

            signaturePBtn.setBackgroundResource(R.drawable.style_tab_left_on);
            signatureDBtn.setBackgroundResource(R.drawable.style_tab_right_on);
        }
        else
            signatureDBtn.setVisibility(View.GONE);
        if (job.getStatusId()>16){
            bolButton.setVisibility(View.VISIBLE);

            signaturePBtn.setBackgroundResource(R.drawable.style_tab_left_on);
            signatureDBtn.setBackgroundResource(R.drawable.style_tab_middle_on);
            bolButton.setBackgroundResource(R.drawable.style_tab_right_on);
        }
        else
            bolButton.setVisibility(View.GONE);

        signaturePBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                signatureLayer.setVisibility(View.VISIBLE);
//                changeStatus.setVisibility(View.GONE);
                openConfirmationsActivity(CONFIRMATIONS_SHOW_PICKUP);
                bolLayer.setVisibility(View.GONE);
                if (changeStatusVisibility)
                    changeStatus.setVisibility(View.VISIBLE);

            }
        });

        signatureDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                signatureLayer.setVisibility(View.VISIBLE);
//                changeStatus.setVisibility(View.GONE);
                openConfirmationsActivity(CONFIRMATIONS_SHOW_DROPOFF);
                bolLayer.setVisibility(View.GONE);
                if (changeStatusVisibility)
                    changeStatus.setVisibility(View.VISIBLE);
            }
        });
        bolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                bolLayer.setVisibility(View.VISIBLE);
//                changeStatus.setVisibility(View.GONE);
                openBol();
//                signatureLayer.setVisibility(View.GONE);
            }
        });
    }

    private void initGeneralDetails(){

        generalDateIcon= (ImageView) findViewById(R.id.jobs_general_date_icon);
        generalStatusIcon=findViewById(R.id.jobs_general_status_icon);
        generalText= (TextView) findViewById(R.id.job_general_date);
        payment= (TextView) findViewById(R.id.job_payment);
        boxIcon= (ImageView) findViewById(R.id.jobs_general_boxes_icon);
        boxText= (TextView) findViewById(R.id.jobs_general_boxes_text);
        palletIcon= (ImageView) findViewById(R.id.jobs_general_pallets_icon);
        palletsText= (TextView) findViewById(R.id.jobs_general_pallets_text);

        smallSpecialRequest= (LinearLayout) findViewById(R.id.job_special_images_layout);
        smallSpecialRequest.setVisibility(View.GONE);
        if (job.getStatusId()>=5&&job.getStatusId()<11){
            generalDateIcon.setImageResource(R.drawable.pickup);
            generalText.setText(getString(R.string.pickup)+" "+job.getPickupDate()+" "+job.getPickupFromTime()+"-"+job.getPickupTillTime());
            if (job.getPickupInTime().contains("late by"))
                generalText.setTextColor(getResources().getColor(R.color.red));
            else if (job.getPickupInTime().contains("1 hour")&&!job.getPickupInTime().contains("day"))
                generalText.setTextColor(getResources().getColor(R.color.orange));

        }
        else if (job.getStatusId()>=11&&job.getStatusId()<15){
            generalDateIcon.setImageResource(R.drawable.dropoff);
            generalText.setText(getString(R.string.dropoff)+" "+job.getDropoffDate()+" "+job.getDropoffFromTime()+"-"+job.getDropoffTillTime());
            if (job.getDropoffInTime().contains("late by"))
                generalText.setTextColor(getResources().getColor(R.color.red));
            else if (job.getDropoffInTime().contains("1 hour")&&!job.getDropoffInTime().contains("day"))
                generalText.setTextColor(getResources().getColor(R.color.orange));
        }
        else if (job.getStatusId()>=15&&job.getStatusId()<18){
            generalDateIcon.setVisibility(View.GONE);
            generalStatusIcon.setVisibility(View.VISIBLE);
            utils.drawCircle(generalStatusIcon.getWidth(),
                    generalStatusIcon.getHeight(), job.getCircleColor(), generalStatusIcon);
            generalText.setText(job.getStatusName());
        }
        else {
            generalDateIcon.setVisibility(View.GONE);
            generalStatusIcon.setVisibility(View.VISIBLE);
            int index=job.getCompletedDate().lastIndexOf(":");
            String dateCompleted="";
            if (index>0)
                 dateCompleted=job.getCompletedDate().substring(0,index);
            generalText.setText(getString(R.string.completed_at) + " " +dateCompleted);
            utils.drawCircle(generalStatusIcon.getWidth(),
                    generalStatusIcon.getHeight(),"#1D993B", generalStatusIcon);
        }

        payment.setText(job.getShipmentShipperPaymentText());
        if (SharedPreferenceManager.getInstance(this).getCanSeePrice()==0)
            payment.setVisibility(View.GONE);
        else
            payment.setVisibility(View.VISIBLE);


//        if (job.getNumberTruckload()>0){
            palletIcon.setVisibility(View.GONE);
            palletsText.setVisibility(View.GONE);
            boxIcon.setImageResource(R.drawable.blue_truckload);
            boxIcon.setVisibility(View.GONE);
            boxText.setEllipsize(TextUtils.TruncateAt.END);
            boxText.setMaxLines(1);
            boxText.setText(job.getTruckloadName());

//        }
//        else {
//            if (job.getNumberBoxes()>0) {
//                boxText.setText(job.getNumberBoxes()+" "+getString(R.string.boxes));
//            }
//            if (job.getNumberPallets()>0) {
//                palletsText.setText(job.getNumberPallets()+" "+getString(R.string.pallets));
//            }
//            else {
//                palletIcon.setVisibility(View.GONE);
//                palletsText.setVisibility(View.GONE);
//            }
//
//        }
        smallSpecialRequest.removeAllViews();
        boolean isFirst=true;
        for (SpecialRequest specialRequest:job.getSpecials()){

            ImageView imageView=new ImageView(this);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(StartApplication.START_URL+specialRequest.getImageResource(), imageView);
            int size=(int)getResources().getDimension(R.dimen.xx_small_icon);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);

            if (isFirst)
                isFirst=false;
            else
                params.setMargins((int)getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
            imageView.setLayoutParams(params);
            smallSpecialRequest.addView(imageView);

        }
    }

//    private void initSignatureObjects() {
//        saveSignature= (Button) findViewById(R.id.job_save_signature);
//        signaturePad= (SignaturePad) findViewById(R.id.job_signature_pad);
//        receiverName= (EditText) findViewById(R.id.job_receiver_name);
//        if (!job.getReceiverName().equals("")&&!job.getReceiverName().equals("null")&&!job.getReceiverName().equals(null))
//            receiverName.setText(job.getReceiverName());
//        signatureLayer= (RelativeLayout) findViewById(R.id.job_signature_layer);
//        signatureImage= (ImageView) findViewById(R.id.job_signature_image);
//        if (job.getSignatureUrl().equals("")) {
//            signatureImage.setVisibility(View.GONE);
////            saveSignature.setVisibility(View.GONE);
//        }
//        else {
//            initImage(signatureImage,StartApplication.START_URL+"/"+ job.getSignatureUrl());
////            saveSignature.setVisibility(View.GONE);
//
//        }
//    }

    private void initBolObjects(){
        bolLayer= (RelativeLayout) findViewById(R.id.job_bol_layer);
        bolImage= (ImageView) findViewById(R.id.job_bol_image);
        saveBol= (Button) findViewById(R.id.job_save_bol);
        captureBolBtn=(Button)findViewById(R.id.job_capture_bol);
        if (job.getBolUrl().equals("")||job.getBolUrl()==null)
            saveBol.setVisibility(View.GONE);
//        else {
//
//        }
        initImage(bolImage, StartApplication.START_URL + "/" + job.getBolUrl());
    }

    private void initSensorManager() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.job_map);
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);
        mapFragment.newInstance(options);
        mapFragment.getMapAsync(this);
    }

    private void initArcMenu() {


        arcLayout= (ArcLayout) findViewById(R.id.arcLayout);

        fab= (ImageView) findViewById(R.id.job_fab);
//        hideMenu();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShown) {
                    fab.setImageResource(R.drawable.round_menu);
                    hideMenu();
                    isShown = false;
                } else {
                    fab.setImageResource(R.drawable.round_menu_close);
                    showMenu();
                    isShown = true;
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady=true;
        mMap = googleMap;


        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }


        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setTrafficEnabled(false);

        centerCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastLocation != null && mMap != null) {
                    if (!isCenterOnDriver) {
                        isCenterOnDriver=true;
                        isMapLockOnLocation = true;
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                .zoom(18)
                                .bearing(bearing)
                                .tilt(60)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    else {
                        isCenterOnDriver=false;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(sourceMarker.getPosition());
                        builder.include(destinationMarker.getPosition());
                        builder.include(currentMarker.getPosition());
                        LatLngBounds bounds = builder.build();

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                    }
                }
            }
        });
       onMapReadyLoad();

    }

    private void onMapReadyLoad(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (locationService.getmCurrentLocation()!=null)
            lastLocation=locationService.getmCurrentLocation();

        int width= (int) getResources().getDimension(R.dimen.map_icon_width);
        int height= (int) getResources().getDimension(R.dimen.map_icon_height);
        int truckHeight= (int) getResources().getDimension(R.dimen.map_truck_icon);

        final LatLng sourceLatLng=new LatLng(job.getOriginLat(), job.getOriginLng());
        sourceMarker = mMap.addMarker(new MarkerOptions().position(sourceLatLng)
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(utils.resizeMapIcons(this, "from", width, height))));
        builder.include(sourceMarker.getPosition());

        final LatLng destLatLng=new LatLng(job.getDestinationLat(), job.getDestinationLng());
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destLatLng)
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(utils.resizeMapIcons(this, "to", width,height))));
        builder.include(destinationMarker.getPosition());

        if (job.getStatusId()<17){
            if (lastLocation!=null) {
                final LatLng currentLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng)
                        .title("")
                        .icon(BitmapDescriptorFactory.fromBitmap(utils.resizeMapIcons(this, "truck_icon", truckHeight, truckHeight)))
                        .anchor(0.5f,0.5f));
//                        .flat(true));

                builder.include(currentMarker.getPosition());

                if (job.getStatusId()<11)
                    marksPath(currentLatLng, sourceLatLng);
                else
                    marksPath(sourceLatLng,destLatLng);
            }
        }

        LatLngBounds bounds = builder.build();
        padding = height; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void marksPath(LatLng sourcePoint,LatLng destPoint){
        //final String[] directions = new String[1];
        if (sourcePoint!=null&&destPoint!=null) {
            String urlToGoogle = utils.makeURLtoGetPath(sourcePoint.latitude, sourcePoint.longitude
                    , destPoint.latitude, destPoint.longitude);
            GetPathAsyncTask getPathAsyncTask = new GetPathAsyncTask(this, urlToGoogle, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {

                    Polyline newPolyline= utils.drawOnePath(result.getResult(),mMap,getResources().getColor(R.color.blue_bg));
                    if (newPolyline!=null)
                        polyline=newPolyline;
                }
            });
            getPathAsyncTask.execute();

        }
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    public void close(View view){
        finish();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);

            bearing = (float) (Math.toDegrees(orientation[0]) + mDeclination);
//            updateCamera(bearing);
        }
    }

    private void updateCamera(float bearing) {
        if (mMap!=null&&lastLocation!=null&&isMapLockOnLocation) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .zoom(18)
                    .bearing(bearing)
                    .tilt(60)
                    .build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 70, new GoogleMap.CancelableCallback() {
//                @Override
//                public void onFinish() {
//
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });


//            CameraPosition oldPos = mMap.getCameraPosition();
//            CameraPosition pos = CameraPosition.builder(oldPos).target(oldPos.target).bearing(bearing).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        if (currentMarker!=null){
            currentMarker.setRotation(bearing);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onLocationChanged(Location location) {

        Log.e("onLocationChanged", "onLocationChanged  " + location);
        final LatLng currentLatLng=new LatLng(location.getLatitude(), location.getLongitude());
        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        // getDeclination returns degrees
        mDeclination = field.getDeclination();


        if ( isMapLockOnLocation) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .zoom(18)
                    .bearing(bearing)
                    .tilt(60)
                    .build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 70, new GoogleMap.CancelableCallback() {
//                @Override
//                public void onFinish() {
//
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        if (currentMarker != null) {
            MarkerAnimation.animateMarkerToGB(currentMarker, currentLatLng, mLatLngInterpolator);
        }
        lastLocation=location;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dataUpdateReceiver == null)
            dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(LocationService.CHANGE_LOCATION_BROADCAST);
        registerReceiver(dataUpdateReceiver, intentFilter);

        if (userDetailsChangedReceiver == null)
            userDetailsChangedReceiver = new UserDetailsChangedReceiver();
        IntentFilter intentFilter2 = new IntentFilter(AlarmReceiver.APPLICATION_DETAILS_CHANGED);
        registerReceiver(userDetailsChangedReceiver, intentFilter2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dataUpdateReceiver != null)
            unregisterReceiver(dataUpdateReceiver);
        if (userDetailsChangedReceiver != null)
            unregisterReceiver(userDetailsChangedReceiver);

    }

    @Override
    public void onClick(View view) {
        if (view==contactOriginPhone){
            openDialer(contactOriginPhone.getText().toString());
        }
        else if (view==contactDestPhone){
            openDialer(contactDestPhone.getText().toString());
        }
        else if (view==shipperPhone){
            openDialer(shipperPhone.getText().toString());
        }
        else if (view==changeStatus)
            if (SharedPreferenceManager.getInstance(JobActivity.this).getLastVersion().equals(BuildConfig.VERSION_NAME)
                    ||SharedPreferenceManager.getInstance(JobActivity.this).getLastTestingVersion().equals(BuildConfig.VERSION_NAME))
                changeStatus();
            else
                utils.showVersionError(JobActivity.this);

    }

    private void openDialer(String phoneNumber){
        this.phoneNumber=phoneNumber;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE,},
                    Utils.MY_PERMISSIONS_REQUEST_DIALER);

        } else {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);
        }
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocationService.CHANGE_LOCATION_BROADCAST)) {
                //intent.get
                Location location = intent.getParcelableExtra("location");
                onLocationChanged(location);
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

    if (ev.getAction()==MotionEvent.ACTION_DOWN){
        pressStartTime = System.currentTimeMillis();
        pressedX = ev.getX();
        pressedY = ev.getY();
    }
    else if (ev.getAction()==MotionEvent.ACTION_MOVE){
        long pressDuration = System.currentTimeMillis() - pressStartTime;
        Log.v("aaa MOVE dis presDur", distance(pressedX, pressedY, ev.getX(), ev.getY()) + " " + pressDuration);
        if (pressDuration > MAX_CLICK_DURATION && distance(pressedX, pressedY, ev.getX(), ev.getY()) > MAX_CLICK_DISTANCE) {
            if(touchLayer!=null&&pressedX>touchLayer.getX()&&pressedX<(touchLayer.getX()+touchLayer.getWidth())
                    &&pressedY > touchLayer.getY() && pressedY < (touchLayer.getY() + touchLayer.getHeight())) {
                Log.v("aaa dispatchTouchEvent ", "action map drag");
                isMapLockOnLocation=false;
            }
            else
                Log.v("aaa dispatchTouchEvent ","action drag");

        }

    }
    else if (ev.getAction()==MotionEvent.ACTION_UP){

    }
    return super.dispatchTouchEvent(ev);
}

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

    class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private final int maxHeight =calculateMaxHeight();

        private View view;

        public OnViewGlobalLayoutListener(View view) {
            this.view = view;
        }

        @Override
        public void onGlobalLayout() {

            if (isFirstTime) {
                Log.e("newonGlobalLayout","view.getHeight() > maxHeight");
                if (view.getHeight() > maxHeight) {
                    ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                    params.height=maxHeight;
                    scrollView.setLayoutParams(params);
                    //scrollView.getLayoutParams().height = maxHeight;
                    //scrollMainLayout.getLayoutParams().height=maxHeight;
                    Log.e("newonGlobalLayout", view.getLayoutParams().height + "");
                    isFirstTime=false;
                }
            }


//            if (view.getHeight() > maxHeight)
//                view.getLayoutParams().height = maxHeight;
        }

        private int calculateMaxHeight(){
            // int screenHeight=utils.getScreenHeight(OfferActivity.this);
            int panelHeight=(int) getResources().getDimension(R.dimen.panel_height);
            int buttonHeight=(int) getResources().getDimension(R.dimen.button_height);
            int toolBarHeight=(int)getResources().getDimension(R.dimen.toolbar_height);
            int margins=(int)(getResources().getDimension(R.dimen.medium_margin)*4);
            return screenHeight-(toolBarHeight+panelHeight+buttonHeight+margins);
        }

    }

    private void changeStatus(){
        if (changeStatus.getText().toString().equals(getString(R.string.get_signature)))
        {//TODO  new confirmation dropoff
//            signatureLayer.setVisibility(View.VISIBLE);
            openConfirmationsActivity(CONFIRMATIONS_DROPOFF);
            bolLayer.setVisibility(View.GONE);
            if (changeStatusVisibility)
                changeStatus.setVisibility(View.VISIBLE);
            //changeStatus.setVisibility(View.GONE);
        }
        else if (changeStatus.getText().toString().equals(getString(R.string.get_bol))){
//            bolLayer.setVisibility(View.VISIBLE);
////            signatureLayer.setVisibility(View.GONE);
//            changeStatus.setVisibility(View.GONE);
            openBol();
        }
        else if (statusToChange==11){
            //TODO to open job confirmation
            openConfirmationsActivity(CONFIRMATIONS_PICKUP);
        }
        else {
            String title=getString(R.string.confirm);
            String content="";
            List<View> contentViews=null;
            String okStr=getResources().getString(R.string.yes);
            String cancelStr=getString(R.string.cancel);
            String customTxt="";
            Drawable cancelIcon=null;
            Drawable okIcon=null;
            Drawable customIcon=null;

            switch (statusToChange){
                case 7:content=getString(R.string.change_status_message_5_7);
                    break;
                case 9:
                    content=getString(R.string.change_status_message_7_9);
                    break;
//                case 11:content=getString(R.string.change_status_message_7_11);
//                        //this is show all the shipment details
//                        //contentViews=getLoadsVies();
//                        customTxt=getString(R.string.call_support);
//                        customIcon=getResources().getDrawable(R.drawable.call);
//                    break;
                case 12:content=getString(R.string.change_status_message_11_12);
                    break;
//                case 14:content=getString(R.string.change_status_message_12_14);
//                    break;
                case 15:content=getString(R.string.change_status_message_12_15);

                    break;
            }
            //final String finalCancelStr = cancelStr;
            final String finalCustomTxt = customTxt;
            changeStatus.setEnabled(false);
            utils.openPopupWindow(JobActivity.this,
                    false,
                    title,
                    content,
                    okStr,
                    cancelStr,
                    customTxt,
                    contentViews,
                    new PopupInterface() {
                        @Override
                        public void onOk() {
                            super.onOk();
                            changeStatusOnServer();
                        }

                        @Override
                        public void onCancel() {
                            super.onCancel();
                            changeStatus.setEnabled(true);
                        }

                        @Override
                        public void customBtn() {
                            super.customBtn();
                            if (finalCustomTxt.equals(getString(R.string.call_support))) {
                                changeStatus.setEnabled(true);
                                openDialer(SharedPreferenceManager.getInstance(JobActivity.this).getSupportPhone());
                            }
                        }
                    },okIcon,cancelIcon,customIcon);
        }

    }

    private void openConfirmationsActivity(int confirmationType) {

       // boolean isPickup=confirmationType==CONFIRMATIONS_PICKUP;

        Intent intent=new Intent();
        intent.setClass(this,JobConfirmation.class);
        intent.putExtra("status", confirmationType);
        intent.putExtra("job",job);
        startActivityForResult(intent, confirmationType);

    }

//    private List<View> getLoadsVies(){
//
//        List<View>loadsView=new ArrayList<>();
//        int imageSize=(int)getResources().getDimension(R.dimen.xx_small_icon);
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
//
//        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        for (String load:job.getLoads()){
//
//            LinearLayout loadLayout=new LinearLayout(this);
//            String[] separated = load.split(":");
//            String type=separated[0];
//            String content=separated[1].trim();
//
//
//            ImageView image=new ImageView(this);
//            image.setLayoutParams(imageParams);
//
//            if (type.equals("box"))
//                image.setImageResource(R.drawable.box);
//            else if (type.equals("pallet"))
//                image.setImageResource(R.drawable.pallet);
//            else
//                image.setImageResource(R.drawable.truckload);
//
//            loadLayout.addView(image);
//
//            TextView text=new TextView(this);
//            text.setText(content);
//            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_sub_title));
//            text.setTextColor(getResources().getColor(R.color.text_grey));
//            textParams.setMargins((int) getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
//            text.setLayoutParams(textParams);
//
//            loadLayout.addView(text);
//
//            loadsView.add(loadLayout);
//
//        }
//        return loadsView;
//    }

    private void changeStatusOnServer(){
        statusProgress.setVisibility(View.VISIBLE);

        UpdateJobStatus updateJobStatus = new UpdateJobStatus(this, job.getId(), statusToChange, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                statusProgress.setVisibility(View.GONE);
                changeStatus.setEnabled(true);
                JSONObject jsonFullObject = null;
                try {
                    jsonFullObject = new JSONObject(result.getResult());
                    boolean err=jsonFullObject.getBoolean("err");
                    if (err){
                        String errDescription=jsonFullObject.getString("errType");
                        if (errDescription.equals("update shipment")){
                            utils.openPopupWindow(JobActivity.this,
                                    false,
                                    getString(R.string.notice),
                                    getString(R.string.job_status_changed_error),
                                    getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                        @Override
                                        public void onOk() {
                                            JobActivity.this.finish();
                                        }
                                    });
                        }
                        else {
                            utils.openPopupWindow(JobActivity.this,
                                    false,
                                    getString(R.string.notice),
                                    getString(R.string.driver_changed_text),
                                    getString(R.string.ok),
                                    "",
                                    null,
                                    new PopupInterface() {
                                        @Override
                                        public void onOk() {
                                            JobActivity.this.finish();
                                        }
                                    });
                        }
                    }
                    else {
                        JSONObject shipmentJson = jsonFullObject.getJSONObject("shipment");
                        fillJobFromJson(shipmentJson);


                        getStatusToChange();
//                        if (statusToChange==11){
//                            openConfirmationsActivity(CONFIRMATIONS_PICKUP);
//                        }
                         if (job.getStatusId() == 15) {
                            changeStatus.setText(getString(R.string.get_signature));
//                            signatureLayer.setVisibility(View.VISIBLE);
                            openConfirmationsActivity(CONFIRMATIONS_DROPOFF);
                            //changeStatus.setVisibility(View.GONE);

                            arcLayout.removeView(shareWrapper);
                            //                    arcLayout.setRadius((int) getResources().getDimension(R.dimen.radius_1));
                            //                    arcMenu.removeView(shareWrapper);
                            //                    arcMenu.setRadius(getResources().getDimension(R.dimen.radius_1));

                        } else if (job.getStatusId() == 16) {
                            changeStatus.setText(getString(R.string.get_bol));
                             openBol();
//                            bolLayer.setVisibility(View.VISIBLE);
//                            changeStatus.setVisibility(View.GONE);
                        }//&&job.getStatusId()<=22
                        else if (job.getStatusId() == 17) {
                            changeStatusOnServer();
                            //changeStatus.setText(getString(R.string.shipment_completed));
                            //changeStatus.setClickable(false);
                            //signatureBtn.setVisibility(View.GONE);
                            //bolButton.setVisibility(View.GONE);
                         }
                        else if (job.getStatusId() > 17) {
                            String title = getString(R.string.notice);
                            String content = getString(R.string.job_completed_successfully);
                            String okStr = getString(R.string.ok);

                            utils.openPopupWindow(JobActivity.this,
                                    false,
                                    title,
                                    content,
                                    okStr,
                                    ""
                                    , null
                                    , new PopupInterface() {
                                        @Override
                                        public void onOk() {
                                            super.onOk();
                                            JobActivity.this.finish();
                                        }
                                    });
                        } else {
                            String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
                            changeStatus.setText(nextStatusName);
                        }

                        if (job.getStatusId() == 11) {
                            if (polyline != null)
                                polyline.remove();
                            marksPath(sourceMarker.getPosition(), destinationMarker.getPosition());
                            signaturePBtn.setVisibility(View.VISIBLE);

                            signaturePBtn.setBackgroundResource(R.drawable.style_tab_full_on);
                        }
                        if (job.getStatusId() > 15) {
                            signatureDBtn.setVisibility(View.VISIBLE);

                            signaturePBtn.setBackgroundResource(R.drawable.style_tab_left_on);
                            signatureDBtn.setBackgroundResource(R.drawable.style_tab_right_on);
                        }
                        if (job.getStatusId() > 16) {
                            bolButton.setVisibility(View.VISIBLE);

                            signaturePBtn.setBackgroundResource(R.drawable.style_tab_left_on);
                            signatureDBtn.setBackgroundResource(R.drawable.style_tab_middle_on);
                            bolButton.setBackgroundResource(R.drawable.style_tab_right_on);
                        }

                        locationService.updateJobIfExist(job);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                changeStatus.setEnabled(true);
                statusProgress.setVisibility(View.GONE);
//                utils.showInfoPopup(JobActivity.this.getString(R.string.server_error)
//                        , resultEntity.getResult(), JobActivity.this);
//                utils.openPopupWindow(JobActivity.this,
//                        true,
//                        getString(R.string.server_error),
//                        resultEntity.getResult(),
//                        getString(R.string.ok),
//                        "",
//                        null,
//                        new PopupInterface() {});
                utils.showServerError(JobActivity.this,resultEntity.getResult());
            }
        });

        updateJobStatus.execute();
    }

    private void getStatusToChange(){
        statusToChange=job.getStatusId();
        switch (job.getStatusId()){
            case 5:statusToChange=7;
                break;
            case 7 :statusToChange=9;//11;//to 9
                break;
            case 8:statusToChange=9;//11;//to 9
                break;
            case 9:statusToChange=11;
                break;
            case 11:statusToChange=12;
                break;
            case 12:statusToChange=15;//to 14
                break;
            case 13:statusToChange=15;//to 14
                break;
            case 14:statusToChange=15;
                break;
            case 15:
                statusToChange = 16;
                break;
            case 16:
                statusToChange=17;
                break;
            case 17:statusToChange=18;
                break;
        }
    }

    private Jobs fillJobFromJson(JSONObject jobJson){
        try {
            if (jobJson.getInt("status_id")!=0) {
                job.setStatusId(jobJson.getInt("status_id"));
                job.setStatusName(jobJson.getString("status_name"));
                if (job.getStatusId() == 12) {
                    job.setIsLessThreeKm(false);
                    job.setIsLessHundredMeters(false);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return job;
    }

//    public void saveSignature(View view){
//        String receiverNameStr = receiverName.getText().toString();
//        if (receiverNameStr.equals("")){
//            receiverName.requestFocus();
//            //utils.showInfoPopup(getString(R.string.missed_details),getString(R.string.no_receiver_name),this);
//            utils.openPopupWindow(JobActivity.this,
//                    true,
//                    getString(R.string.notice),
//                    getString(R.string.no_receiver_name),
//                    getString(R.string.ok),
//                    "",
//                    null,
//                    new PopupInterface() {});
//        }
////        else if (){
////
////        }
//        else if ((job.getSignatureUrl().equals("")||job.getSignatureUrl()==null)&&signaturePad.isEmpty()) {
//           // utils.showInfoPopup(getString(R.string.missed_details),getString(R.string.no_signature),this);
//            utils.openPopupWindow(JobActivity.this,
//                    true,
//                    getString(R.string.notice),
//                    getString(R.string.no_signature),
//                    getString(R.string.ok),
//                    "",
//                    null,
//                    new PopupInterface() {});
//        }
//        else if (!signaturePad.isEmpty()){
//            signatureBitmap = signaturePad.getSignatureBitmap();
//
//            File file=utils.getFileFromBitmap(signatureBitmap,"signature",this);
//            progressLayout.setVisibility(View.VISIBLE);
//            // Context context, long shipmentId, String imageType,String fileName, IResultsInterface iResultsInterface
//            UploadImageAsyncTask uploadImageAsyncTask=new UploadImageAsyncTask(file, this, job.getId(), "signature", "signature"+job.getId()+".png"
//                    ,receiverNameStr, new IResultsInterface() {
//                @Override
//                public void onCompleteWithResult(ResultEntity result) {
//                    signatureLayer.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    if (changeStatusVisibility)
//                        changeStatus.setVisibility(View.VISIBLE);
//                    signatureImage.setImageBitmap(signatureBitmap);
//                    //get signature url set visibility visible
//                    if (changeStatus.getText().toString().equals(getString(R.string.get_signature))) {
//                        String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
//                        changeStatus.setText(nextStatusName);
//                        changeStatusOnServer();
//                    }
//                }
//
//                @Override
//                public void onErrorWithResult(ResultEntity resultEntity) {
//                    progressLayout.setVisibility(View.GONE);
//                    utils.showServerError(JobActivity.this,resultEntity.getResult());
//
//                }
//            });
//
//            uploadImageAsyncTask.execute();
//        }
//        else {
//            signatureLayer.setVisibility(View.GONE);
//            if (changeStatusVisibility)
//                changeStatus.setVisibility(View.VISIBLE);
//            //signatureImage.setImageBitmap(signatureBitmap);
//            if (changeStatus.getText().toString().equals(getString(R.string.get_signature))) {
//                String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
//                changeStatus.setText(nextStatusName);
//                changeStatusOnServer();
//            }
//        }
//    }

//    public void clearSignature(View view){
//        signaturePad.clear();
//        signatureImage.setVisibility(View.GONE);
////        saveSignature.setVisibility(View.VISIBLE);
//    }
//
//    public void closeSignatureLayout(View view){
//        signatureLayer.setVisibility(View.GONE);
//
//        if (changeStatusVisibility)
//            changeStatus.setVisibility(View.VISIBLE);
//        //initImage(signatureImage,job.getSignatureUrl());
//        if (!job.getSignatureUrl().equals(""))
//            signatureImage.setVisibility(View.VISIBLE);
////        saveSignature.setVisibility(View.GONE);
//        //utils.hideKeyboard(this);
//    }

    public void openBol(){
        bolLayer.setVisibility(View.VISIBLE);
        changeStatus.setVisibility(View.GONE);
        if (!job.getBolUrl().equals("")&&job.getBolUrl()!=null){
            captureBolBtn.setText(getString(R.string.recapture));
        }
        else {
            captureBolBtn.setText(getString(R.string.capture));
        }
    }

    public void saveBol(View view){

        if (bolCaptureImage!=null&&bolCaptureImage.exists()) {
//            ExifInterface exifInterface = null;
//            int degree=0;
//            try {
//                exifInterface = new ExifInterface(bolCaptureImage.getPath());
//                degree = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
            final File lowQualityImage=utils.saveBitmapToFile(bolCaptureImage,this);
//            utils.setOrientation(utils.getContentUriForFilePath(lowQualityImage.getPath(),this),degree,this);
            progressLayout.setVisibility(View.VISIBLE);
            UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(lowQualityImage, this, job.getId(), "bol", "bol"+job.getId()+".jpg"
                    ,"", new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    bolLayer.setVisibility(View.GONE);
                    if (changeStatusVisibility)
                        changeStatus.setVisibility(View.VISIBLE);
                    String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
                    changeStatus.setText(nextStatusName);
                    progressLayout.setVisibility(View.GONE);
                    //TODO get bol url
                    changeStatusOnServer();
                    lowQualityImage.delete();
                }
                @Override
                public void onErrorWithResult(ResultEntity resultEntity) {
                    progressLayout.setVisibility(View.GONE);
                    if (changeStatusVisibility)
                        changeStatus.setVisibility(View.VISIBLE);
                    //utils.showInfoPopup(getString(R.string.server_error), resultEntity.getResult().toString(), JobActivity.this);
//                    utils.openPopupWindow(JobActivity.this,
//                            true,
//                            getString(R.string.server_error),
//                            resultEntity.getResult(),
//                            getString(R.string.ok),
//                            "",
//                            null,
//                            new PopupInterface() {});
                    utils.showServerError(JobActivity.this,resultEntity.getResult());
                }
            });
            uploadImageAsyncTask.execute();
        }
        else if (!job.getBolUrl().equals("")&&job.getBolUrl()!=null){
            bolLayer.setVisibility(View.GONE);
            if (changeStatusVisibility)
                changeStatus.setVisibility(View.VISIBLE);
            String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
            changeStatus.setText(nextStatusName);
            changeStatusOnServer();
        }
        else {
           // utils.showInfoPopup(getString(R.string.missed_details),getString(R.string.no_image),this);
            utils.openPopupWindow(JobActivity.this,
                    true,
                    getString(R.string.notice),
                    getString(R.string.no_image),
                    getString(R.string.ok),
                    "",
                    null,
                    new PopupInterface() {});
        }
    }

    public void captureBol(View view){
        isCaptureBol=true;
//        utils.getPickImageIntent(this);
        openCameraIntent();
    }

    private void openCameraIntent() {

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
            initImageUri();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(takePictureIntent, Utils.TAKE_IMAGE_FROM_CAMERA);

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

                    initImageUri();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(takePictureIntent, utils.TAKE_IMAGE_FROM_CAMERA);
                } else {


                }
                return;
            }
            case Utils.MY_PERMISSIONS_REQUEST_DIALER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+phoneNumber));
                    startActivity(intent);
                } else {


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void initImageUri(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void closeBolLayout(View view){
        bolLayer.setVisibility(View.GONE);
        if (changeStatusVisibility)
            changeStatus.setVisibility(View.VISIBLE);
        initImage(bolImage, StartApplication.START_URL + "/" + job.getBolUrl());
        //utils.hideKeyboard(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK&&requestCode==Utils.TAKE_IMAGE_FROM_CAMERA){
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//            Uri tempUri = getImageUri(getApplicationContext(), photo);
//            // CALL THIS METHOD TO GET THE ACTUAL PATH
//            String fullPath="file://"+getRealPathFromURI(tempUri);
            String path = "";
            String fullPath = "";
            path = getRealPathFromURI(imageUri);
            fullPath = "file://" + path;
            File finalFile = new File(path);
            if (isCaptureBol) {
                initImage(bolImage, fullPath);
                job.setBolUrl(fullPath);
                bolCaptureImage = finalFile;
                saveBol.setVisibility(View.VISIBLE);
                captureBolBtn.setText(getString(R.string.recapture));
//            try {
//                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
//                        getContentResolver(), imageUri);

//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            }

            else {
                uploadDriverImage(path,fullPath,finalFile);
            }
        }
        else if (resultCode == Activity.RESULT_OK&&requestCode==CONFIRMATIONS_PICKUP){
            boolean isSavedSuccessfully=data.getBooleanExtra("isSavedSuccessfully",false);
            if (isSavedSuccessfully){
                job= (Jobs) data.getSerializableExtra("job");
                changeStatusOnServer();
            }
        }
        else if (resultCode == Activity.RESULT_OK&&requestCode==CONFIRMATIONS_DROPOFF){
            boolean isSavedSuccessfully=data.getBooleanExtra("isSavedSuccessfully",false);
            if (isSavedSuccessfully){
                job= (Jobs) data.getSerializableExtra("job");
                if (changeStatus.getText().toString().equals(getString(R.string.get_signature))) {
                        String nextStatusName = ((StartApplication) getApplicationContext()).getStatusNameById(statusToChange);
                        changeStatus.setText(nextStatusName);
                        changeStatusOnServer();
                    }
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void initImage(final ImageView imageView, final String url){

        ImageLoader imageLoader=ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnFail(null)
                .showImageForEmptyUri(null)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();

        imageLoader.displayImage(url, imageView, defaultOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                if (imageView == signatureImage) {
//                    imageView.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    //bitmap=utils.changeFileOrientation(url.replace("file://",""),bitmap);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationBroadcastReceiver);
        unregisterReceiver(jobArrivedPickupBroadcastReceiver);
    }

    BroadcastReceiver notificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String idS=intent.getStringExtra("idS");
            String[] ids=idS.split("/");
            for (String id:ids) {
                if ((job.getId() + "").equals(id))
                    refreshJobFromServer();
            }
        }
    };

    BroadcastReceiver jobArrivedPickupBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long jobId=intent.getLongExtra("job_id",-1);
            if (job.getId()==jobId)
                refreshJobFromServer();
        }
    };

    private void refreshJobFromServer() {
        statusProgress.setVisibility(View.VISIBLE);
        GetSpecificJobAsyncTask getSpecificJobAsyncTask=new GetSpecificJobAsyncTask(job.getId(), this, new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                statusProgress.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result.getResult());
                    int err = jsonObject.getInt("err");
                    //String jobObjStr = jsonObject.getString("job");
                    if (err == 0) {
                        JSONObject jobJson = jsonObject.getJSONObject("job");
                        job = utils.fillJob(jobJson);
                        initAllData();
                        if (isMapReady)
                            onMapReadyLoad();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    utils.showServerError(JobActivity.this,e.getMessage());
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                statusProgress.setVisibility(View.GONE);
                utils.showServerError(JobActivity.this,resultEntity.getResult());
            }
        });

        getSpecificJobAsyncTask.execute();
    }

    public void goToSupport(View view){
        this.phoneNumber= SharedPreferenceManager.getInstance(this).getSupportPhone();
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
                utils.openPopupWindow(JobActivity.this,
                        false,
                        getString(R.string.notice),
                        getString(R.string.user_details_changed_message),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {

                            @Override
                            public void onOk() {
                                utils.closeApplication(JobActivity.this);
                            }
                        });
            }
        }
    }

    public void shareLink(View view){
        shareProgress.setVisibility(View.VISIBLE);
        GetShareLinkAsyncTask getShareLinkAsyncTask=new GetShareLinkAsyncTask(this, job.getId(), new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                shareProgress.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(result.getResult());
                    String shareLink=jsonObject.getString("share_url");

                    String shareBody = "Hi there,\n\n" +
                            SharedPreferenceManager.getInstance(JobActivity.this).getUserFirstName() +
                            " "+
                            SharedPreferenceManager.getInstance(JobActivity.this).getUserLastName()+
                            " has shared with you this shipment data.\n" +
                            "You can track and see the shipment data in this link\n"+
                            shareLink;
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                shareProgress.setVisibility(View.GONE);
                utils.showServerError(JobActivity.this,resultEntity.getResult());
            }
        });
        getShareLinkAsyncTask.execute();
    }

    public void openCamera(View view){

        isCaptureBol=false;
        openCameraIntent();
    }

    private void uploadDriverImage(String path,String fullPath,File imageFile){

        final File lowQualityImage=utils.saveBitmapToFile(imageFile,this);
        takImageProgress.setVisibility(View.VISIBLE);
        cameraImg.setEnabled(false);
        UploadJobImagesAsyncTask uploadJobImagesAsyncTask=new UploadJobImagesAsyncTask(lowQualityImage, job.getId(), this, "job" + job.getId()+".jpg", new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                takImageProgress.setVisibility(View.GONE);
                cameraImg.setEnabled(true);
                lowQualityImage.delete();

                utils.openPopupWindow(JobActivity.this,
                        true,
                        getString(R.string.notice),
                        getString(R.string.upload_message_content),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {
                        });
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                takImageProgress.setVisibility(View.GONE);
                cameraImg.setEnabled(true);
                utils.showServerError(JobActivity.this, resultEntity.getResult());
            }
        });

        uploadJobImagesAsyncTask.execute();

//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.popup_capture_image);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
//
//        ImageView imageView= (ImageView) dialog.findViewById(R.id.job_c_capture_image);
//
//        initImage(imageView, fullPath);
//
//
//        Button capture = (Button) dialog.findViewById(R.id.popup_c_capture_btn);
//        capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                isCaptureBol=false;
//                openCameraIntent();
//            }
//        });
//
//        Button upload = (Button) dialog.findViewById(R.id.popup_c_upload_btn);
//
//        Button close = (Button) dialog.findViewById(R.id.popup_c_close_btn);
//
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();


    }

    public void openNotes(View view){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_notes);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        final EditText editText= (EditText) dialog.findViewById(R.id.popup_notes_txt);
        editText.setText(Html.fromHtml(job.getDriverNotes()));

        Button save = (Button) dialog.findViewById(R.id.popup_notes_ok_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                job.setDriverNotes(Html.toHtml(editText.getText()));
                noteProgress.setVisibility(View.VISIBLE);
                UpdateJobNotesAsyncTask updateJobNotesAsyncTask=new UpdateJobNotesAsyncTask(JobActivity.this, job.getId(), job.getDriverNotes(), new IResultsInterface() {
                    @Override
                    public void onCompleteWithResult(ResultEntity result) {
                        noteProgress.setVisibility(View.GONE);
                        utils.openPopupWindow(JobActivity.this,
                                true,
                                getString(R.string.notice),
                                getString(R.string.notes_message_content),
                                getString(R.string.ok),
                                "",
                                null,
                                new PopupInterface() {
                                });
                    }

                    @Override
                    public void onErrorWithResult(ResultEntity resultEntity) {
                        noteProgress.setVisibility(View.GONE);
                        utils.showServerError(JobActivity.this, resultEntity.getResult());
                    }
                });

                updateJobNotesAsyncTask.execute();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.popup_notes_cancel_btn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

//    public void record(View view){
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.popup_record);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
//
//        final Chronometer chronometer= (Chronometer) dialog.findViewById(R.id.popup_rec_chronometer);
//
//        final ImageView playPauseBtn= (ImageView) dialog.findViewById(R.id.popup_rec_play_pause);
//        playPauseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isRecording)
//                    stopRecord(playPauseBtn,chronometer);
//                else
//                    startRecord(playPauseBtn, chronometer);
//            }
//        });
//
//        Button save = (Button) dialog.findViewById(R.id.popup_rec_ok_btn);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (mediaRecorder!=null){
//                    if(isRecording)
//                        stopRecord(playPauseBtn,chronometer);
//                    mediaRecorder=null;
//                    //TODO upload record
////                    String path= Environment.getExternalStorageDirectory().getAbsolutePath();
////                    String recordDefaultName="job_record";
//                    File file=new File(path + File.separator + Utils.MAIN_DIRECTORY +
//                            File.separator + Utils.RECORD_DIRECTORY + File.separator + recordDefaultName + ".mp3");
//                    UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(file, JobActivity.this, job.getId(), "record", "record"+job.getId()+".mp3","",
//                            new IResultsInterface() {
//                        @Override
//                        public void onCompleteWithResult(ResultEntity result) {
//
//                        }
//                        @Override
//                        public void onErrorWithResult(ResultEntity resultEntity) {
//
//                        }
//                    });
//                    uploadImageAsyncTask.execute();
//
//                }
//            }
//        });
//
//        Button cancel = (Button) dialog.findViewById(R.id.popup_rec_cancel_btn);
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//
//        dialog.show();
//    }
//
//    private void startRecord(ImageView playPauseBtn,Chronometer chronometer) {
//        playPauseBtn.setImageResource(R.drawable.pause_record);
////        stop.setImageResource(R.drawable.record_stop);
////        recordButton.setEnabled(false);
////        stop.setEnabled(true);
//
//
//        File mFolder = new File(path +File.separator+ Utils.MAIN_DIRECTORY);
//        if (!mFolder.exists()) {
//            mFolder.mkdir();
//        }
//        File rFolder = new File(path +File.separator+ Utils.MAIN_DIRECTORY+File.separator+Utils.RECORD_DIRECTORY);
//        if (!rFolder.exists()) {
//            rFolder.mkdir();
//        }
//        try {
//            mediaRecorder = new MediaRecorder();
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mediaRecorder.setOutputFile(path + File.separator + Utils.MAIN_DIRECTORY +
//                    File.separator + Utils.RECORD_DIRECTORY + File.separator + recordDefaultName + ".mp3");
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.prepare();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mediaRecorder.start();
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//        isRecording=true;
//    }
//
//    private void stopRecord(ImageView playPauseBtn,Chronometer chronometer){
//        mediaRecorder.stop();
//        mediaRecorder.reset();
//        mediaRecorder.release();
//
//        chronometer.stop();
//
//        isRecording=false;
//        playPauseBtn.setImageResource(R.drawable.play_record);
//
//
//    }

    private void showMenu() {
        arcLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arcLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

}
