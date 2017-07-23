package com.shtibel.truckies.activities.offer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shtibel.truckies.BuildConfig;
import com.shtibel.truckies.R;
import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.activities.dashboard.tabs.DashboardTabJobs;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.activities.dashboard.tabs.offers.Offer;
import com.shtibel.truckies.activities.job.JobActivity;
import com.shtibel.truckies.asyncTasks.AcceptOfferAsyncTask;
import com.shtibel.truckies.asyncTasks.GetPathAsyncTask;
import com.shtibel.truckies.asyncTasks.GetSpecificJobAsyncTask;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.servicesAndBroadCasts.AlarmReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.LocationService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class OfferActivity extends AppCompatActivity  implements OnMapReadyCallback {

    Offer offer;
    ProgressBar progressBar;
    Utils utils=new Utils();
    int screenWidth;
    int screenHeight;

    SupportMapFragment mapFragment;
    private GoogleMap mMap=null;
    Marker sourceMarker=null;
    Marker destinationMarker=null;
    ScrollView scrollView;
    SlidingUpPanelLayout slidingUpPanelLayout;

    int availableStatus;

    //TextView originAddressName;
    TextView originAddress;
    //TextView destinationAddressName;
    TextView destinationAddress;
    TextView pickupDate;
    TextView dropoffDate;

    TextView specialInstructionOrigin;
    TextView specialInstructionDest;

//    TextView pickupInTxt;
//    TextView pickupIn;
//    TextView distance;
//    TextView weight;
    //ImageView boxes;
   // ImageView pallets;
//
    TextView generalPickup;
    TextView generalDropoff;
    ImageView generalBoxIcon;
    TextView generalBoxText;
    ImageView generalPalletIcon;
    TextView generalPalletText;
    LinearLayout smallSpecialRequest;
    TextView payment;


    LinearLayout bigSpecialRequest;

    TextView comments;

    RelativeLayout routLayout;
    RelativeLayout loadLayout;
    RelativeLayout specialRequestsLayout;
    RelativeLayout commentsLayout;

    LinearLayout loadsContainer;
    Button acceptOffer;//TODO

    TextView offerTitle;
    UserDetailsChangedReceiver userDetailsChangedReceiver;
    private LocationService locationService;
    private ServiceConnection serviceConnection =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            LocationService.ServiceBinder serviceBinder = (LocationService.ServiceBinder)binder;
            locationService = serviceBinder.getService();
            locationService.setContext(OfferActivity.this);
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        offer= (Offer) getIntent().getSerializableExtra("offer");
        initViews();
        initRoutData();
        initLoads();
        initSpecialsRequests();
        initComments();
        initGeneralDetails();
        initMap();

        bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        offerTitle= (TextView) findViewById(R.id.offer_title);
        offerTitle.setText(getString(R.string.offer_t) + "#" + offer.getId());
        progressBar= (ProgressBar) findViewById(R.id.offer_progress);
        screenHeight=utils.getScreenHeight(this);
        screenWidth=utils.getScreenWidth(this);
        scrollView= (ScrollView) findViewById(R.id.offer_details_scroll);
        scrollView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnViewGlobalLayoutListener(scrollView));
        slidingUpPanelLayout= (SlidingUpPanelLayout) findViewById(R.id.offer_sliding_layout);
    }


    private void initRoutData() {
        //originAddressName= (TextView) findViewById(R.id.offer_origin_address_name);
        originAddress= (TextView) findViewById(R.id.offer_origin_address);
        //destinationAddressName= (TextView) findViewById(R.id.offer_destination_address_name);
        destinationAddress= (TextView) findViewById(R.id.offer_destination_address);
        pickupDate= (TextView) findViewById(R.id.offer_pickup_date);
        dropoffDate= (TextView) findViewById(R.id.offer_dropoff_date);

       // originAddressName.setText(offer.getOriginAddressName());
        originAddress.setText(offer.getOriginAddress());
        //destinationAddressName.setText(offer.getDestinationAddressName());
        destinationAddress.setText(offer.getDestinationAddress());
        pickupDate.setText(offer.getPickupDate() + "  " + offer.getPickupFromTime() + "-" + offer.getPickupTillTime());
        dropoffDate.setText(offer.getDropoffDate() + "  " + offer.getDropoffFromTime() + "-" + offer.getDropoffTillTime());

        specialInstructionOrigin= (TextView) findViewById(R.id.job_special_instruction_origin);
        specialInstructionDest= (TextView) findViewById(R.id.job_special_instruction_dest);
        if (!offer.getOriginSpecialSiteInstructions().equals(""))
            specialInstructionOrigin.setText(offer.getOriginSpecialSiteInstructions());
        else
            specialInstructionOrigin.setVisibility(View.GONE);
        if (!offer.getDestinationSpecialSiteInstructions().equals(""))
            specialInstructionDest.setText(offer.getDestinationSpecialSiteInstructions());
        else
            specialInstructionDest.setVisibility(View.GONE);
    }

    private void
    initLoads() {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        loadsContainer= (LinearLayout) findViewById(R.id.offer_load_otems_container);
        loadsContainer.removeAllViews();
        String content=offer.getTruckloadName();
        String weightTxt=" ";
        if (offer.getTotalWeightType().equalsIgnoreCase("t")) {
            weightTxt+=new DecimalFormat(".#").format(offer.getTotalLoadWeightText())+ getString(R.string.ton);
        }
        else {
            weightTxt+=offer.getTotalLoadWeightText() + getString(R.string.kg);
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

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        int imageSize=(int)getResources().getDimension(R.dimen.xx_small_icon);
//        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
//
//        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        loadsContainer= (LinearLayout) findViewById(R.id.offer_load_otems_container);
//        loadsContainer.removeAllViews();
//        boolean isFirst=true;
//        for (String load:offer.getLoads()){
//
//            String[] separated = load.split(":");
//            String type=separated[0];
//            String content=separated[1].trim();
//            int index=content.lastIndexOf(' ');
//            content=content.substring(0,index);
//
//            //calculate weight
//            double weightInTon=offer.getTotalLoadWeightText()/1000.0;
//            String weightTxt=" ";
//            if (weightInTon>=1) {
//                weightTxt+=new DecimalFormat(".#").format(weightInTon)+ getString(R.string.ton);
//            }
//            else {
//                weightTxt+=offer.getTotalLoadWeightText() + getString(R.string.kg);
//            }
//            content+=weightTxt;
//
//            LinearLayout loadLayout=new LinearLayout(this);
//            if (!isFirst)
//                layoutParams.setMargins(0,(int) getResources().getDimension(R.dimen.small_margin),0,0);
//            else
//                isFirst=false;
//            loadLayout.setLayoutParams(layoutParams);
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
//            image.setVisibility(View.GONE);
//            loadLayout.addView(image);
//
//            TextView text=new TextView(this);
//            text.setText(content);
//            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_normal));
//            text.setTextColor(getResources().getColor(R.color.text_grey));
//            textParams.setMargins(0, 0, 0, 0);
//            text.setLayoutParams(textParams);
//
//            loadLayout.addView(text);
//
//            loadsContainer.addView(loadLayout);
//        }
    }

    private void initSpecialsRequests() {
        specialRequestsLayout= (RelativeLayout) findViewById(R.id.offer_specials_layout);
        bigSpecialRequest= (LinearLayout) findViewById(R.id.offer_specials_big_images);
        if (offer.getSpecialRequest().size()>0) {
            bigSpecialRequest.removeAllViews();
            boolean isFirst = true;
            for (String url : offer.getSpecialRequest()) {

                ImageView imageView = new ImageView(this);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(StartApplication.START_URL + url, imageView);
                int size = (int) getResources().getDimension(R.dimen.small_icon);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);

                if (isFirst)
                    isFirst = false;
                else
                    params.setMargins((int) getResources().getDimension(R.dimen.small_margin), 0, 0, 0);
                imageView.setLayoutParams(params);
                bigSpecialRequest.addView(imageView);

            }
        }
        else
            specialRequestsLayout.setVisibility(View.GONE);
    }

    private void initComments() {
        commentsLayout= (RelativeLayout) findViewById(R.id.offer_comments_layout);
        comments= (TextView) findViewById(R.id.offer_comments_txt);
        comments.setText(offer.getComments());
        if (offer.getComments().equals(""))
            commentsLayout.setVisibility(View.GONE);
    }

    private void initGeneralDetails() {

        generalPickup= (TextView) findViewById(R.id.offer_general_pickup_date);
        generalDropoff= (TextView) findViewById(R.id.offer_general_dropoff_date);
        generalBoxIcon= (ImageView) findViewById(R.id.offers_general_boxes_icon);
        generalBoxText= (TextView) findViewById(R.id.offers_general_boxes_text);
        generalPalletIcon= (ImageView) findViewById(R.id.offers_general_pallets_icon);
        generalPalletText= (TextView) findViewById(R.id.offers_general_pallets_text);
        smallSpecialRequest= (LinearLayout) findViewById(R.id.offer_special_images_layout);
        payment= (TextView) findViewById(R.id.offer_payment);


        generalPickup.setText(getString(R.string.pickup)+" "+offer.getPickupDate()+" "+offer.getPickupFromTime()+"-"+offer.getPickupTillTime());
        generalDropoff.setText(getString(R.string.dropoff)+" "+offer.getDropoffDate()+" "+offer.getDropoffFromTime()+"-"+offer.getDropoffTillTime());

//        if (offer.getNumberTruckload()>0){
            generalPalletIcon.setVisibility(View.GONE);
            generalPalletText.setVisibility(View.GONE);
            generalBoxIcon.setImageResource(R.drawable.blue_truckload);
            generalBoxIcon.setVisibility(View.GONE);
            generalBoxText.setEllipsize(TextUtils.TruncateAt.END);
            generalBoxText.setMaxLines(1);
            generalBoxText.setText(offer.getTruckloadName());

//        }
//        else {
//            if (offer.getNumberBoxes()>0) {
//                generalBoxText.setText(offer.getNumberBoxes()+" "+getString(R.string.boxes));
//            }
//            if (offer.getNumberPallets()>0) {
//                generalPalletText.setText(offer.getNumberPallets()+" "+getString(R.string.pallets));
//            }
//            else {
//                generalPalletIcon.setVisibility(View.GONE);
//                generalPalletText.setVisibility(View.GONE);
//            }
//
//        }

        payment.setText(offer.getShipmentShipperPaymentText());
        if (SharedPreferenceManager.getInstance(this).getCanSeePrice()==0)
            payment.setVisibility(View.GONE);
        else
            payment.setVisibility(View.VISIBLE);

        smallSpecialRequest.setVisibility(View.GONE);
        smallSpecialRequest.removeAllViews();
        boolean isFirst=true;
        for (String url:offer.getSpecialRequest()){

            ImageView imageView=new ImageView(this);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(StartApplication.START_URL+url, imageView);
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

    private void initMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.offer_map);
        mapFragment.getMapAsync(this);
    }

    public void acceptOffer(View view){
        //SharedPreferenceManager.getInstance(this).saveCanAcceptOffers(0);
        if(!SharedPreferenceManager.getInstance(OfferActivity.this).getLastVersion().equals(BuildConfig.VERSION_NAME)
                &&!SharedPreferenceManager.getInstance(OfferActivity.this).getLastTestingVersion().equals(BuildConfig.VERSION_NAME))
        {
            utils.showVersionError(OfferActivity.this);
        }
        else if (SharedPreferenceManager.getInstance(this).getCanAcceptOffers()==0){
            utils.openPopupWindow(OfferActivity.this,
                    true,
                    getString(R.string.notice),
                    getString(R.string.offer_error),
                    getString(R.string.ok),
                    "",
                    null,
                    new PopupInterface(){});
        }
        else {
            utils.openPopupWindow(OfferActivity.this,
                    true,
                    getString(R.string.accept_offer_title),
                    getString(R.string.accept_offer_txt)+": \n"+offer.getTruckloadName()+"?",
                    getString(R.string.yes),
                    getString(R.string.no),
                    null,
                    new PopupInterface() {
                        @Override
                        public void onOk() {
                            super.onOk();
                            acceptOfferInServer();
                        }
                    });
        }
    }

    private void acceptOfferInServer() {
        progressBar.setVisibility(View.VISIBLE);
        AcceptOfferAsyncTask acceptOfferAsyncTask = new AcceptOfferAsyncTask(this, offer.getId(), new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                super.onCompleteWithResult(result);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(result.getResult());
                    String accept = jsonObject.getString("accept");
                    if (accept.equals("yes")) {

                        openOfferAsJob();



//                        Intent intent=new Intent();
//                        intent.putExtra("offer_id",offer.getId());
//                        setResult(RESULT_OK, intent);
//                        utils.openPopupWindow(OfferActivity.this,
//                                false,
//                                getString(R.string.success),
//                                getString(R.string.offer_accept_success),
//                                getString(R.string.ok),
//                                "",
//                                null,
//                                new PopupInterface() {
//                                    @Override
//                                    public void onOk() {
//                                        super.onOk();
//                                        OfferActivity.this.finish();
//                                    }
//                                });
                    } else {
                        utils.openPopupWindow(OfferActivity.this,
                                true,
                                OfferActivity.this.getString(R.string.notice),
                                OfferActivity.this.getString(R.string.offer_error),
                                getString(R.string.ok),
                                "",
                                null,
                                new PopupInterface() {
                                    @Override
                                    public void onOk() {
                                        super.onOk();
                                        OfferActivity.this.finish();
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    utils.showServerError(OfferActivity.this, e.getMessage());
                }
            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                super.onErrorWithResult(resultEntity);
                progressBar.setVisibility(View.GONE);
//                utils.showInfoPopup(getString(R.string.server_error),
//                        resultEntity.getResult(), OfferActivity.this);
//                utils.openPopupWindow(OfferActivity.this,
//                        true,
//                        getString(R.string.server_error),
//                        resultEntity.getResult(),
//                        getString(R.string.ok),
//                        "",
//                        null,
//                        new PopupInterface() {});
                utils.showServerError(OfferActivity.this,resultEntity.getResult());
            }
        });
        acceptOfferAsyncTask.execute();
    }

    private void openOfferAsJob() {
            progressBar.setVisibility(View.VISIBLE);
            GetSpecificJobAsyncTask getSpecificJobAsyncTask=new GetSpecificJobAsyncTask(offer.getId(), OfferActivity.this, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    progressBar.setVisibility(View.GONE);
                    if (utils.isWifiOpen(OfferActivity.this)) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.getResult());
                            int err = jsonObject.getInt("err");
                            if (err == 0 ) {
                                JSONObject jobJson = jsonObject.getJSONObject("job");
                                Jobs job = utils.fillJob(jobJson);

                                        Intent intent = new Intent();
                                        intent.setClass(OfferActivity.this, JobActivity.class);
                                        intent.putExtra("job", job);
                                        intent.putExtra("pressedButton",  DashboardTabJobs.SCHEDULE);
                                        intent.putExtra("isFromAcceptOffer",true);
                                        startActivity(intent);
                                        OfferActivity.this.finish();
                            } else {
                                utils.openPopupWindow(OfferActivity.this,
                                        true,
                                        OfferActivity.this.getString(R.string.notice),
                                        OfferActivity.this.getString(R.string.job_error),
                                        OfferActivity.this.getString(R.string.ok),
                                        "",
                                        null,
                                        new PopupInterface() {
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            utils.showServerError(OfferActivity.this, e.getMessage());
                        }
                    }
                    else
                        utils.locationClosePopup(OfferActivity.this);
                }

                @Override
                public void onErrorWithResult(ResultEntity resultEntity) {
                    progressBar.setVisibility(View.GONE);
                    utils.showServerError(OfferActivity.this,resultEntity.getResult());
                }
            });

            getSpecificJobAsyncTask.execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        int width= (int) getResources().getDimension(R.dimen.map_icon_width);
        int height= (int) getResources().getDimension(R.dimen.map_icon_height);
        int truckHeight= (int) getResources().getDimension(R.dimen.map_truck_icon);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final LatLng sourceLatLng=new LatLng(offer.getOriginLat(), offer.getOriginLng());
        sourceMarker = mMap.addMarker(new MarkerOptions().position(sourceLatLng)
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(utils.resizeMapIcons(this,"from",width,height))));
        builder.include(sourceMarker.getPosition());

        final LatLng destLatLng=new LatLng(offer.getDestinationLat(), offer.getDestinationLng());
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destLatLng)
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(utils.resizeMapIcons(this,"to",width,height))));
        builder.include(destinationMarker.getPosition());

        LatLngBounds bounds = builder.build();
        final int padding = height; // offset from edges of the map in pixels
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        try {
            mMap.animateCamera(cu);
            marksPath(sourceLatLng, destLatLng);
        }catch (Exception e){
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(cu);
                    marksPath(sourceLatLng, destLatLng);
                }
            });
        }



//        CameraPosition cameraPosition =
//                new CameraPosition.Builder()
//                        .target(currentLatLng)
//                        .zoom(17)
//                        .bearing(locationService.getmCurrentLocation().getBearing())
//                        .tilt(60)
//                        .build();
//
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

//    public Bitmap resizeMapIcons(String iconName,int width, int height){
//        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
//        return resizedBitmap;
//    }

    private void marksPath(LatLng sourcePoint,LatLng destPoint){
        //final String[] directions = new String[1];
        if (sourcePoint!=null&&destPoint!=null) {
            String urlToGoogle = utils.makeURLtoGetPath(sourcePoint.latitude, sourcePoint.longitude
                    , destPoint.latitude, destPoint.longitude);
            GetPathAsyncTask getPathAsyncTask = new GetPathAsyncTask(this, urlToGoogle, new IResultsInterface() {
                @Override
                public void onCompleteWithResult(ResultEntity result) {
                    utils.drawOnePath(result.getResult(),mMap,getResources().getColor(R.color.blue_bg));
                }
            });
            getPathAsyncTask.execute();
        }
    }

    public void close(View view){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


     class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private final int maxHeight =calculateMaxHeight();

        private View view;

        public OnViewGlobalLayoutListener(View view) {
            this.view = view;
        }

        @Override
        public void onGlobalLayout() {
            if (view.getHeight() > maxHeight)
                view.getLayoutParams().height = maxHeight;
        }

       private int calculateMaxHeight(){
          // int screenHeight=utils.getScreenHeight(OfferActivity.this);
           int panelHeight=(int) getResources().getDimension(R.dimen.offer_panel_height);
           int buttonHeight=(int) getResources().getDimension(R.dimen.button_height);
           int toolBarHeight=(int)getResources().getDimension(R.dimen.toolbar_height);
           int margins=(int)(getResources().getDimension(R.dimen.medium_margin)*4);
           return screenHeight-(toolBarHeight+panelHeight+buttonHeight+margins);
       }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

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

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                utils.openPopupWindow(OfferActivity.this,
                        false,
                        getString(R.string.notice),
                        getString(R.string.user_details_changed_message),
                        getString(R.string.ok),
                        "",
                        null,
                        new PopupInterface() {

                            @Override
                            public void onOk() {
                                utils.closeApplication(OfferActivity.this);
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

//    private void initImageMap(final Offer offer) {
//        progressBar.setVisibility(View.VISIBLE);
//        if (offer.getLatLngPoints().equals("")) {
//            String urlToGoogle = utils.makeURLtoGetPath(offer.getOriginLat(), offer.getOriginLng()
//                    , offer.getDestinationLat(), offer.getDestinationLng());
//
//
//            GetPathAsyncTask getPathAsyncTask = new GetPathAsyncTask(this, urlToGoogle, new IResultsInterface() {
//                @Override
//                public void onCompleteWithResult(ResultEntity result) {
//                    String latLngPoints=utils.getPointsOfPath(result.getResult());
//                    loadImage( offer, latLngPoints);
//                }
//            });
//            getPathAsyncTask.execute();
//        }
//        else
//            loadImage(offer,offer.getLatLngPoints());
//    }

//    private void loadImage(final Offer offer,String latLngPoints){
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheOnDisk(true)
//                .cacheInMemory(true)
//                .showImageOnFail(null)
//                .showImageForEmptyUri(null)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300))
//                .considerExifParams(true).build();
//
//        //String latLngPoints=utils.getPointsOfPath(result.getResult());
//        offer.setLatLngPoints(latLngPoints);
//        //          my_location_button1=" +shipment.getOriginLat() + "," + shipment.getOriginLng() + " "&zoom=17" +
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        String url="http://maps.googleapis.com/maps/api/staticmap?" +
//                "&size="+screenWidth+"x"+screenHeight+
//                "&markers=icon:"+StartApplication.START_URL+"/template/application/from.png|"+offer.getOriginLat() + "," + offer.getOriginLng()+"" +
//                "&markers=icon:"+StartApplication.START_URL+"/template/application/to.png|"+offer.getDestinationLat() + "," + offer.getDestinationLng()+
//                "&key=AIzaSyAr8t2iKU1zyA2MxIqLAhiSzOKYoO5f9jk"+
//                "&path=color:0x378EB9ff|weight:4|enc:"+latLngPoints+
//                "&sensor=false";
//        imageLoader.loadImage(url, defaultOptions, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////                            FadeInBitmapDisplayer.animate(holder.map,500);
//                map.setImageBitmap(loadedImage);
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                super.onLoadingFailed(imageUri, view, failReason);
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//
//
//
//    }