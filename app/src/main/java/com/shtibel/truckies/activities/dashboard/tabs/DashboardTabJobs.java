package com.shtibel.truckies.activities.dashboard.tabs;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.shtibel.truckies.R;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.Jobs;
import com.shtibel.truckies.activities.dashboard.tabs.jobs.JobsAdapter;
import com.shtibel.truckies.activities.job.JobActivity;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.asyncTasks.GetJobsDataAsyncTask;
import com.shtibel.truckies.asyncTasks.SearchJobsAsyncTask;
import com.shtibel.truckies.generalClasses.HidingScrollListener;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.SpecialRequest;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.pushNotifications.MyFcmListenerService;
import com.shtibel.truckies.pushNotifications.NotificationReceiver;
import com.shtibel.truckies.servicesAndBroadCasts.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Shtibel on 02/08/2016.
 */
public class DashboardTabJobs extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    public static final int TODAY=0;
    public static final int SCHEDULE=1;
    public static final int COMPLETE=2;
    public static final int SEARCH=3;

    TextView noShipments;
    TextView today;
    TextView schedualed;
    TextView complete;
    RelativeLayout search;
    ImageView searchImage;

//    RelativeLayout search2;
//    ImageView searchImage2;
//    RelativeLayout closeSearch;
//    TextView openAdvance;
//    EditText searchKeyword;

    ImageView advanceClose;
   // ProgressBar advancedProgress;

    List<Jobs> jobsToday=new ArrayList<>();
    List<Jobs> jobsScheduled=new ArrayList<>();
    List<Jobs> jobsComplete=new ArrayList<>();
    List<Jobs> jobsSearch=new ArrayList<>();

    RecyclerView jobsList;
    JobsAdapter jobsAdapter;
    RelativeLayout tabsLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    int pressedButton;
    int prevPressedButton;
    Utils utils=new Utils();
    boolean isFirstTime=true;

    LinearLayout header;
    //LinearLayout searchHeader;
    LinearLayout searchAdvanceHeader;
    EditText advancedSearchKeyWord;
    Button advancedSearch;

    //View.OnClickListener searchClickListener;
    View.OnClickListener searchAdvanceClickListener;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    Button fromDate;
    Button toDate;
    //String searchString="";

    Calendar fromDateCalendar=null;
    Calendar toDateCalendar=null;
    String[] placeArr=null;
    ProgressBar advancedProgress;

//    CheckBox boxesCheckBox;
//    CheckBox palletsCheckBox;
//    CheckBox truckloadsCheckBox;
    HidingScrollListener hidingScrollListener;
   // List<Jobs>shipmentsCompleteFilter=new ArrayList<>();
   // boolean isFirstTime

    PlaceAutocompleteFragment autocompleteFragment;
    private LocationService locationService;
    private ServiceConnection serviceConnection =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            LocationService.ServiceBinder serviceBinder = (LocationService.ServiceBinder)binder;
            locationService = serviceBinder.getService();
            locationService.setContext(getActivity());
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_tab_jobs, container, false);

        initHeaders(rootView);

        noShipments= (TextView) rootView.findViewById(R.id.shipments_no_shipments);
        progressBar= (ProgressBar) rootView.findViewById(R.id.tab_jobs_progress);

        pressedButton=TODAY;

        initRefreshLayout(rootView);
        initJobsList(rootView);

        getActivity().bindService(new Intent(getActivity(), LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        return rootView;
    }



    private void initHeaders(View rootView) {
        header= (LinearLayout) rootView.findViewById(R.id.jobs_tab_header);

        today= (TextView) rootView.findViewById(R.id.jobs_tab_today);
        schedualed= (TextView) rootView.findViewById(R.id.jobs_tab_schedualed);
        complete= (TextView) rootView.findViewById(R.id.jobs_tab_complete);
        search= (RelativeLayout) rootView.findViewById(R.id.jobs_tab_search);
        searchImage= (ImageView) rootView.findViewById(R.id.jobs_tab_search_image);

        today.setOnClickListener(this);
        schedualed.setOnClickListener(this);
        complete.setOnClickListener(this);
        search.setOnClickListener(this);

        initSearchClickListener();
        //-----
//        searchHeader= (LinearLayout) rootView.findViewById(R.id.jobs_tab_header_search);
//
//        search2= (RelativeLayout) rootView.findViewById(R.id.jobs_tab_search_2);
//        search2.setOnClickListener(searchClickListener);
//        searchImage2= (ImageView) rootView.findViewById(R.id.jobs_tab_search_image_2);
//        closeSearch= (RelativeLayout) rootView.findViewById(R.id.jobs_tab_close_search);
//        closeSearch.setOnClickListener(searchClickListener);
//        openAdvance= (TextView) rootView.findViewById(R.id.jobs_tab_advance);
//        openAdvance.setOnClickListener(searchClickListener);
//        searchKeyword= (EditText) rootView.findViewById(R.id.jobs_tab_search_edit_txt);
        //-----

        searchAdvanceHeader= (LinearLayout) rootView.findViewById(R.id.jobs_tab_header_advance_search);

        advanceClose= (ImageView) rootView.findViewById(R.id.jobs_tab_advance_close);
        advanceClose.setOnClickListener(searchAdvanceClickListener);
        advancedProgress= (ProgressBar) rootView.findViewById(R.id.tab_jobs_advanced_progress);
        advancedSearchKeyWord = (EditText) rootView.findViewById(R.id.jobs_tab_search_edit_txt_advance);
//        boxesCheckBox= (CheckBox) rootView.findViewById(R.id.tab_jobs_boxes);
//        palletsCheckBox= (CheckBox) rootView.findViewById(R.id.tab_jobs_pallets);
//        truckloadsCheckBox= (CheckBox) rootView.findViewById(R.id.tab_jobs_truckloads);
        advancedSearch= (Button) rootView.findViewById(R.id.tab_jobs_advanced_search);
        advancedSearch.setOnClickListener(searchAdvanceClickListener);

        initLocationComplete(rootView);
        initSearchDatePickers(rootView);
    }

    private void initLocationComplete(View rootView) {
        autocompleteFragment = (PlaceAutocompleteFragment)
            getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint(getString(R.string.locations));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeArr = place.getAddress().toString().split(",");
            }

            @Override
            public void onError(Status status) {

            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        });

        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                view.setVisibility(View.GONE);
                placeArr=null;
            }
        });
    }

    private void initSearchClickListener() {
//        searchClickListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()){
//                    case R.id.jobs_tab_close_search:
//                       // header.setVisibility(View.VISIBLE);
//                        searchHeader.setVisibility(View.GONE);
//                        refreshJobsList();
//                        utils.hideKeyboard(getActivity());
//                        break;
//                    case R.id.jobs_tab_advance:
//                        searchAdvanceHeader.setVisibility(View.VISIBLE);
//                        jobsList.setVisibility(View.GONE);
//                        break;
//                    case R.id.jobs_tab_search_2:
//                        searchJobs(false);
//                        break;
//                }
//            }
//        };

        searchAdvanceClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.jobs_tab_advance_close:
                        searchAdvanceHeader.setVisibility(View.GONE);
                        jobsList.setVisibility(View.VISIBLE);
                        pressedButton=prevPressedButton;
                        refreshJobsList();
                        break;
                    case R.id.tab_jobs_advanced_search:
                        searchJobs(true);
                        break;
                }
            }
        };
    }

    private void initRefreshLayout(View rootView) {

        int offset=(int)(getContext().getResources().getDimension(R.dimen.tabs_title_height)+
                   getResources().getDimension(R.dimen.medium_margin)*3);
        swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list);
        swipeRefreshLayout.setProgressViewOffset(false, 0, offset);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void initJobsList(View rootView) {

        jobsList= (RecyclerView) rootView.findViewById(R.id.jobs_tab_jobs_list);
        jobsAdapter=new JobsAdapter(jobsToday,pressedButton,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        jobsList.setLayoutManager(linearLayoutManager);
        jobsList.setAdapter(jobsAdapter);

        tabsLayout= (RelativeLayout) rootView.findViewById(R.id.tab_jobs_tabs);
        hidingScrollListener=new HidingScrollListener(getContext()) {
            @Override
            public void onMoved(int distance) {
                tabsLayout.setTranslationY(-distance);

            }

            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {

            }
        };
        jobsList.setOnScrollListener(hidingScrollListener);

    }

    private void initSearchDatePickers(View rootView) {

        fromDate= (Button) rootView.findViewById(R.id.tab_jobs_from_date);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });
        toDate= (Button) rootView.findViewById(R.id.tab_jobs_to_date);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDatePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromDateCalendar = Calendar.getInstance();
                fromDateCalendar.set(year, monthOfYear, dayOfMonth);
                fromDate.setText(dateFormatter.format(fromDateCalendar.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toDateCalendar = Calendar.getInstance();
                toDateCalendar.set(year, monthOfYear, dayOfMonth);
                toDate.setText(dateFormatter.format(toDateCalendar.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {

        //setButtonsOffStyle();

        switch (view.getId()){
            case R.id.jobs_tab_today:
                setButtonsOffStyle();
                today.setBackgroundResource(R.drawable.style_tab_left_on);
                today.setTextColor(Color.WHITE);
                pressedButton=TODAY;
                refreshJobsList();
                break;
            case R.id.jobs_tab_schedualed:
                setButtonsOffStyle();
                schedualed.setBackgroundResource(R.drawable.style_tab_middle_on);
                schedualed.setTextColor(Color.WHITE);
                pressedButton=SCHEDULE;
                refreshJobsList();
                break;
            case R.id.jobs_tab_complete:
                setButtonsOffStyle();
                complete.setBackgroundResource(R.drawable.style_tab_right_on);
                complete.setTextColor(Color.WHITE);
                pressedButton=COMPLETE;
                refreshJobsList();
                break;
            case R.id.jobs_tab_search:
//                search.setBackgroundResource(R.drawable.style_tab_right_on);
//                searchImage.setImageResource(R.drawable.search_white);
                searchAdvanceHeader.setVisibility(View.VISIBLE);
                noShipments.setVisibility(View.GONE);
                jobsList.setVisibility(View.GONE);
                prevPressedButton=pressedButton;
                pressedButton=SEARCH;
                break;
        }

//        refreshJobsList();

    }

    private void setButtonsOffStyle() {
        today.setBackgroundResource(R.drawable.style_tab_left_off);
        schedualed.setBackgroundResource(R.drawable.style_tab_middle_off);
        complete.setBackgroundResource(R.drawable.style_tab_right_off);
        search.setBackgroundResource(R.drawable.style_tab_right_off);

        today.setTextColor(getResources().getColor(R.color.text_grey));
        schedualed.setTextColor(getResources().getColor(R.color.text_grey));
        complete.setTextColor(getResources().getColor(R.color.text_grey));
        searchImage.setImageResource(R.drawable.search_blue);
    }

    public void refreshJobsListsFromService() {

        if (pressedButton==SEARCH){
            searchJobs(true);
        }
        GetJobsDataAsyncTask getShipmentsDataAsyncTask=new GetJobsDataAsyncTask(getContext(),new IResultsInterface(){

            @Override
            public void onCompleteWithResult(ResultEntity result) {

                try {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    jobsToday.clear();
                    jobsScheduled.clear();
                    jobsComplete.clear();

                    JSONObject jsonObject = new JSONObject(result.getResult());
                    if (!jsonObject.getString("today").equals("null")){
                        JSONArray shipmentsTodayJsonArray = jsonObject.getJSONArray("today");
                        fillJobsArrayFromJsonArray(jobsToday, shipmentsTodayJsonArray);
                    }

                    if (!jsonObject.getString("schedule").equals("null")){
                        JSONArray shipmentsScheduleJsonArray = jsonObject.getJSONArray("schedule");
                        fillJobsArrayFromJsonArray(jobsScheduled, shipmentsScheduleJsonArray);
                       // fillShipmentsArrayFromJsonArray(shipmentsCompleteFilter,shipmentsScheduleJsonArray);

                    }

                    if (!jsonObject.getString("complete").equals("null")){
                        JSONArray shipmentsCompleteJsonArray = jsonObject.getJSONArray("complete");
                        fillJobsArrayFromJsonArray(jobsComplete, shipmentsCompleteJsonArray);

                    }
                    if (isFirstTime){
                        if (jobsToday.size()>0) {
                            pressedButton = TODAY;
                            onClick(today);
                        }
                        else if (jobsScheduled.size()>0) {
                            //isListEmpty(jobsToday,getString(R.string.no_jobs_today));
                            pressedButton = SCHEDULE;
                            onClick(schedualed);
                        }
                        else if (jobsComplete.size()>0) {
                            //isListEmpty(jobsToday,getString(R.string.no_scheduled_jobs));
                            pressedButton = COMPLETE;
                            onClick(complete);
                        }
                        else {
                            pressedButton = TODAY;
                            onClick(today);
                        }

                    }
                    else
                        refreshJobsList();

                    if (isFirstTime&&getActivity()!=null&&getActivity().getIntent()!=null){
                        isFirstTime=false;
                        String action=getActivity().getIntent().getStringExtra("action");
                        boolean isFromOpenNotification=getActivity().getIntent().getBooleanExtra("isFromOpenNotification", false);
                        if (action!=null&&action.equals(NotificationReceiver.NOTIFICATION_OPEN)&&isFromOpenNotification){
                            Notification notification= (Notification) getActivity().getIntent().getSerializableExtra("notification");
                            if (notification.getType().equals("job")){
                                Jobs job= getJobById(notification.getMessageId());
                                if (job!=null) {
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), JobActivity.class);
                                    intent.putExtra("job",job );
                                    intent.putExtra("pressedButton", SCHEDULE);
                                    getActivity().startActivity(intent);
                                }
                                else {
                                    utils.openPopupWindow(getActivity(),
                                            true,
                                            getString(R.string.notice),
                                            getString(R.string.job_error),
                                            getString(R.string.ok),
                                            "",
                                            null,
                                            new PopupInterface() {});
                                }
                            }
                        }
                    }
//                    if (((MainActivity)getActivity()).isFromAcceptOffer){
//                        openAcceptedJob();
//                    }
                    locationService.setJobs(getJobsToLocation());

                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    utils.showServerError(getActivity(), e.getMessage());
                }

            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                utils.showServerError(getActivity(), resultEntity.getResult());
//                utils.openPopupWindow(getActivity(),
//                        true,
//                        getString(R.string.server_error),
//                        resultEntity.getResult(),
//                        getString(R.string.ok),
//                        "",
//                        null,
//                        new PopupInterface() {});
            }
        });
        getShipmentsDataAsyncTask.execute();
    }

    private void fillJobsArrayFromJsonArray(List<Jobs> jobsList,JSONArray jsonArray){

        for (int i=0;i<jsonArray.length();i++){

            try {
                JSONObject jsonObject=jsonArray.getJSONObject(i);

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
                String specialRequestsIcons=jsonObject.getString("shipment_special_request_with_document");

                if (!specialRequestsIcons.equals("null")&&!specialRequestsIcons.equals("")) {
                    List<String> fullSpecials=Arrays.asList(specialRequestsIcons.split(","));
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
                String loads=jsonObject.getString("shipment_load");
                if (!loads.equals("null")) {
                    jobs.setLoads(Arrays.asList(loads.split(",")));
                }

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

                jobs.setIsPopupShow(jsonObject.getInt("arrived_at_pickup_popup_show")==1);

                jobsList.add(jobs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void refreshJobsList(){
        tabsLayout.setTranslationY(0);
        hidingScrollListener.setmToolbarOffset(0);

        if (pressedButton==TODAY) {
            isListEmpty(jobsToday,getString(R.string.no_jobs_today));
            jobsAdapter.refreshList(jobsToday, pressedButton);
        }
        else if (pressedButton==SCHEDULE){
            isListEmpty(jobsScheduled,getString(R.string.no_scheduled_jobs));
            jobsAdapter.refreshList(jobsScheduled,pressedButton);
        }
        else if (pressedButton==COMPLETE){
            isListEmpty(jobsComplete,getString(R.string.no_completed_jobs));
            jobsAdapter.refreshList(jobsComplete,pressedButton);
        }
        else if (pressedButton==SEARCH){
            isListEmpty(jobsSearch,getString(R.string.search_no_results));
            jobsAdapter.refreshList(jobsSearch,pressedButton);
        }
    }

    private boolean isListEmpty(List<Jobs>jobsList,String error){

        if (jobsList.size()>0) {
            noShipments.setVisibility(View.GONE);
            return false;
        }
        else {
            noShipments.setVisibility(View.VISIBLE);
            noShipments.setText(error);
            return true;
        }

    }


    @Override
    public void onRefresh() {
        progressBar.setVisibility(View.GONE);
        refreshJobsListsFromService();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        refreshJobsListsFromService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter(MyFcmListenerService.CGM_BROADCAST));
        getActivity().registerReceiver(this.jobArrivedPickupBroadcastReceiver,
                new IntentFilter(LocationService.CHANGE_STATUS_PICKUP_ARRIVED));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(notificationBroadcastReceiver);
        getActivity().unregisterReceiver(jobArrivedPickupBroadcastReceiver);
    }
    BroadcastReceiver notificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.VISIBLE);
            refreshJobsListsFromService();
        }
    };
    BroadcastReceiver jobArrivedPickupBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.VISIBLE);
            refreshJobsListsFromService();
        }
    };

    private Jobs getJobById(long jobId){
        for (Jobs jobs:jobsToday){
            if (jobs.getId()==jobId)
                return jobs;
        }
        for (Jobs jobs:jobsScheduled){
            if (jobs.getId()==jobId)
                return jobs;
        }
        for (Jobs jobs:jobsComplete){
            if (jobs.getId()==jobId)
                return jobs;
        }
        return null;
    }

//    public LinearLayout getHeader() {
//        return header;
//    }

//    public LinearLayout getSearchHeader() {
//        return searchHeader;
//    }

    public LinearLayout getSearchAdvanceHeader() {
        return searchAdvanceHeader;
    }

    private void searchJobs(boolean isFullSearch){

        IResultsInterface iResultsInterface=new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                advancedProgress.setVisibility(View.GONE);
                searchAdvanceHeader.setVisibility(View.GONE);
                jobsList.setVisibility(View.VISIBLE);
                //searchKeyword.setText(searchString);
//                searchHeader.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray=new JSONArray(result.getResult());
                    jobsSearch.clear();
                    fillJobsArrayFromJsonArray(jobsSearch, jsonArray);
                    setButtonsOffStyle();
                    search.setBackgroundResource(R.drawable.style_tab_right_on);
                    searchImage.setImageResource(R.drawable.search_white);
                    refreshJobsList();
//                    isListEmpty(jobsSearch,getString(R.string.search_no_results));
//                    jobsAdapter.refreshList(jobsSearch,pressedButton);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setButtonsOffStyle();
                    search.setBackgroundResource(R.drawable.style_tab_right_on);
                    searchImage.setImageResource(R.drawable.search_white);
                    jobsSearch.clear();
                    refreshJobsList();
//                    utils.showServerError(getActivity(), e.getMessage());
//                    isListEmpty(jobsSearch,getString(R.string.search_no_results));
//                    jobsAdapter.refreshList(jobsSearch, pressedButton);
                }

            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                advancedProgress.setVisibility(View.GONE);
                utils.showServerError(getActivity(),resultEntity.getResult());
            }
        };
//        if (!isFullSearch){
//            String keyWord=searchKeyword.getText().toString();
//            searchString=keyWord;
//            SearchJobsAsyncTask searchOffersAsyncTask=new SearchJobsAsyncTask(keyWord, getActivity(), iResultsInterface);
//            searchOffersAsyncTask.execute();
//        }
//        else {
            /*String keyWord,String fromDate,String toDate,String street,
                          String city,String country,boolean boxes,boolean pallets,boolean truckloads,
                          Context context,IResultsInterface iResultsInterface*/
            String keyWord=advancedSearchKeyWord.getText().toString();
            //searchString=keyWord;
            String fromDateTxt="";
            String toDateTxt="";
            String streetTxt="";
            String cityTxt="";
            String countryTxt="";
//            boolean boxes=boxesCheckBox.isChecked();
//            boolean pallets=palletsCheckBox.isChecked();
//            boolean truckloads=truckloadsCheckBox.isActivated();
            if (fromDateCalendar!=null) {
                fromDateTxt = utils.convertDateToString(fromDateCalendar.getTime(), "dd/MM/yyyy");
               // searchString+=" "+fromDateTxt;
            }
            if (toDateCalendar!=null) {
                toDateTxt = utils.convertDateToString(toDateCalendar.getTime(), "dd/MM/yyyy");
                //searchString+=" "+toDateTxt;
            }
            if (placeArr!=null){
                if (placeArr.length>2){
                    streetTxt=placeArr[0];
                    cityTxt=placeArr[1];
                    countryTxt=placeArr[2];
//                    searchString+=" "+streetTxt;
//                    searchString+=" "+cityTxt;
//                    searchString+=" "+countryTxt;
                }
                else {
                    cityTxt=placeArr[0];
                    countryTxt=placeArr[1];
//                    searchString+=" "+cityTxt;
//                    searchString+=" "+countryTxt;
                }
//                if (boxes)
//                    searchString+=" box";
//                if (pallets)
//                    searchString+=" pallets";
//                if (truckloads)
//                    searchString+=" truckloads";
            }
            advancedProgress.setVisibility(View.VISIBLE);
            SearchJobsAsyncTask searchOffersAsyncTask=new SearchJobsAsyncTask(keyWord,fromDateTxt,toDateTxt,streetTxt,
                    cityTxt,countryTxt,true,true,true,getActivity(), iResultsInterface);
            searchOffersAsyncTask.execute();
//        }
    }

//    private void openAcceptedJob() {
//
//        int position=0;
//        Jobs acceptJobs=null;
//
//        for (Jobs jobs:jobsToday)
//        {
//            if (jobs.getId()==((MainActivity)getActivity()).offerId) {
//                acceptJobs = jobs;
//                break;
//            }
//            position++;
//        }
//        if (acceptJobs==null) {
//            position=0;
//            for (Jobs jobs : jobsScheduled) {
//                if (jobs.getId()==((MainActivity)getActivity()).offerId) {
//                    acceptJobs = jobs;
//                    break;
//                }
//                position++;
//            }
//        }
//
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), JobActivity.class);
//        intent.putExtra("job", acceptJobs);
//        intent.putExtra("position", position);
//        intent.putExtra("pressedButton", pressedButton);
//        intent.putExtra("isFromAcceptOffer",true);
//        startActivity(intent);
//
//        ((MainActivity)getActivity()).isFromAcceptOffer=false;
//        ((MainActivity)getActivity()).offerId=-1;
//    }

    public ArrayList<Jobs> getJobsToLocation() {
        ArrayList<Jobs>jobs=new ArrayList<>();
        for (Jobs job:jobsToday){
            if ((job.getStatusId()>=7&&job.getStatusId()<11)||(job.getStatusId()>=12&&job.getStatusId()<15)){
                jobs.add(job);
            }
        }
        for (Jobs job:jobsScheduled){
            if ((job.getStatusId()>=7&&job.getStatusId()<11)||(job.getStatusId()>=12&&job.getStatusId()<15)){
                jobs.add(job);
            }
        }
        return jobs;
    }
}
