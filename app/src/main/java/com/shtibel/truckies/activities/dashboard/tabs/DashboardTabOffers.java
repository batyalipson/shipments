package com.shtibel.truckies.activities.dashboard.tabs;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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
import com.shtibel.truckies.activities.dashboard.MainActivity;
import com.shtibel.truckies.activities.dashboard.tabs.offers.Offer;
import com.shtibel.truckies.activities.dashboard.tabs.offers.OffersAdapter;
import com.shtibel.truckies.activities.notifications.Notification;
import com.shtibel.truckies.activities.offer.OfferActivity;
import com.shtibel.truckies.asyncTasks.GetOffersDataAsyncTask;
import com.shtibel.truckies.asyncTasks.SearchOffersAsyncTask;
import com.shtibel.truckies.generalClasses.HidingScrollListener;
import com.shtibel.truckies.generalClasses.PopupInterface;
import com.shtibel.truckies.generalClasses.Utils;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.ResultEntity;
import com.shtibel.truckies.pushNotifications.MyFcmListenerService;
import com.shtibel.truckies.pushNotifications.NotificationReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Shtibel on 03/08/2016.
 */
public class DashboardTabOffers extends Fragment  implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
   public static final int ACCEPT_OFFER_RESULT=123;
    public static final int TODAY=0;
    public static final int SCHEDULE=1;
    public static final int SEARCH=2;

    TextView noShipments;
    TextView today;
    TextView schedualed;
    RelativeLayout search;
    ImageView searchImage;

    List<Offer> offersToday=new ArrayList<>();
    List<Offer> offersScheduled=new ArrayList<>();
    List<Offer> offersSearch=new ArrayList<>();

    RecyclerView offerList;
    OffersAdapter offersAdapter;
    RelativeLayout tabsLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    int pressedButton;
    int prevPressedButton;

    Utils utils=new Utils();
    boolean isFirstTime=true;

    LinearLayout header;
    LinearLayout searchAdvanceHeader;

    View.OnClickListener searchAdvanceClickListener;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    Button fromDate;
    Button toDate;

    ImageView advanceClose;
    Button advancedSearch;
    Calendar fromDateCalendar=null;
    Calendar toDateCalendar=null;
    String[] placeArr=null;
    ProgressBar advancedProgress;

//    CheckBox boxesCheckBox;
//    CheckBox palletsCheckBox;
//    CheckBox truckloadsCheckBox;
    HidingScrollListener hidingScrollListener;
    PlaceAutocompleteFragment autocompleteFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_tab_offers, container, false);

        initHeaders(rootView);

        pressedButton=TODAY;
        //prevPressedButton=TODAY;
        initRefreshLayout(rootView);
        initOffersList(rootView);
        initLocationComplete(rootView);
        initSearchDatePickers(rootView);
//        refreshOffersListsFromService();

        return rootView;
    }

    private void initHeaders(View rootView) {
        header= (LinearLayout) rootView.findViewById(R.id.tab_offers_tabs);

        today= (TextView) rootView.findViewById(R.id.offers_tab_today);
        schedualed= (TextView) rootView.findViewById(R.id.offers_tab_schedualed);
        search= (RelativeLayout) rootView.findViewById(R.id.offers_tab_search);
        searchImage= (ImageView) rootView.findViewById(R.id.offers_tab_search_image);
        noShipments= (TextView) rootView.findViewById(R.id.shipments_no_shipments);
        progressBar= (ProgressBar) rootView.findViewById(R.id.tab_offers_progress);

        today.setOnClickListener(this);
        schedualed.setOnClickListener(this);
        search.setOnClickListener(this);

        initSearchClickListener();

//        searchHeader= (LinearLayout) rootView.findViewById(R.id.offers_tab_header_search);

//        search2= (RelativeLayout) rootView.findViewById(R.id.offers_tab_search_2);
//        search2.setOnClickListener(searchClickListener);
//        searchImage2= (ImageView) rootView.findViewById(R.id.offers_tab_search_image_2);
//        closeSearch= (RelativeLayout) rootView.findViewById(R.id.offers_tab_close_search);
//        closeSearch.setOnClickListener(searchClickListener);
//        openAdvance= (TextView) rootView.findViewById(R.id.offers_tab_advance);
//        openAdvance.setOnClickListener(searchClickListener);
//        searchKeyword= (EditText) rootView.findViewById(R.id.offers_tab_search_edit_txt);

        //-----

        searchAdvanceHeader= (LinearLayout) rootView.findViewById(R.id.offers_tab_header_advance_search);

        advanceClose= (ImageView) rootView.findViewById(R.id.offers_tab_advance_close);
        advanceClose.setOnClickListener(searchAdvanceClickListener);
        advancedSearch= (Button) rootView.findViewById(R.id.tab_offers_advanced_search);
        advancedSearch.setOnClickListener(searchAdvanceClickListener);
        //advancedSearchKeyWord= (EditText) rootView.findViewById(R.id.offers_tab_search_edittxt_advanced);
//        boxesCheckBox= (CheckBox) rootView.findViewById(R.id.tab_offers_boxes);
//        palletsCheckBox= (CheckBox) rootView.findViewById(R.id.tab_offers_pallets);
//        truckloadsCheckBox= (CheckBox) rootView.findViewById(R.id.tab_offers_truckloads);
        advancedProgress= (ProgressBar) rootView.findViewById(R.id.tab_offers_advanced_progress);
    }

    private void initSearchClickListener() {
//        searchClickListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()){
//                    case R.id.offers_tab_close_search:
//                        // header.setVisibility(View.VISIBLE);
//                        searchHeader.setVisibility(View.GONE);
//                        refreshOffersList();
//                        utils.hideKeyboard(getActivity());
//                        break;
//                    case R.id.offers_tab_advance:
//                        searchAdvanceHeader.setVisibility(View.VISIBLE);
//                        offerList.setVisibility(View.GONE);
//                        break;
//                    case R.id.offers_tab_search_2:
//                        searchOffers(false);
//                        break;
//                }
//            }
//        };

        searchAdvanceClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.offers_tab_advance_close:
                        searchAdvanceHeader.setVisibility(View.GONE);
                        offerList.setVisibility(View.VISIBLE);
                        pressedButton=prevPressedButton;
                        refreshOffersList();
                        break;
                    case R.id.tab_offers_advanced_search:
                        searchOffers(true);
                        break;
                }
            }
        };
    }

    private void initRefreshLayout(View rootView) {

        int offset=(int)(getContext().getResources().getDimension(R.dimen.tabs_title_height)+
                getResources().getDimension(R.dimen.medium_margin)*3);
        swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_list_offers);
        swipeRefreshLayout.setProgressViewOffset(false, 0, offset);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void initOffersList(View rootView) {

        offerList= (RecyclerView) rootView.findViewById(R.id.offer_tab_offers_list);
        offersAdapter=new OffersAdapter(offersToday,pressedButton,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        offerList.setLayoutManager(linearLayoutManager);
        offerList.setAdapter(offersAdapter);

        tabsLayout= (RelativeLayout) rootView.findViewById(R.id.tab_main_offers_tabs);
        hidingScrollListener = new HidingScrollListener(getContext()) {
            @Override
            public void onMoved(int distance) {
                tabsLayout.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
//                tabsLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }

            @Override
            public void onHide() {
//                int mToolbarHeight = (int) getResources().getDimension(R.dimen.tabs_title_height) +
//                        (int) (getResources().getDimension(R.dimen.medium_margin) * 2);
//                tabsLayout.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        };
        offerList.setOnScrollListener(hidingScrollListener);

    }

    private void initLocationComplete(View rootView) {
        autocompleteFragment = (PlaceAutocompleteFragment)
            getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_offers);

        autocompleteFragment.setHint(getString(R.string.locations));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeArr = place.getAddress().toString().split(",");
                //Toast.makeText(getActivity(),place.getAddress(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {

            }
        });
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText("");
                view.setVisibility(View.GONE);
                placeArr = null;
            }
        });
    }

    private void initSearchDatePickers(View rootView) {

        fromDate= (Button) rootView.findViewById(R.id.tab_offers_from_date);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });
        toDate= (Button) rootView.findViewById(R.id.tab_offers_to_date);
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



        switch (view.getId()){
            case R.id.offers_tab_today:
                setButtonsOffStyle();
                today.setBackgroundResource(R.drawable.style_tab_left_on);
                today.setTextColor(Color.WHITE);
               // prevPressedButton=pressedButton;
                pressedButton=TODAY;
                refreshOffersList();
                break;
            case R.id.offers_tab_schedualed:
                setButtonsOffStyle();
                schedualed.setBackgroundResource(R.drawable.style_tab_right_on);
                schedualed.setTextColor(Color.WHITE);
                //prevPressedButton=pressedButton;
                pressedButton=SCHEDULE;
                refreshOffersList();
                break;
            case R.id.offers_tab_search:
                searchAdvanceHeader.setVisibility(View.VISIBLE);
                offerList.setVisibility(View.GONE);
                noShipments.setVisibility(View.GONE);
                prevPressedButton=pressedButton;
                pressedButton=SEARCH;
                break;

        }

//        refreshOffersList();

    }

    private void setButtonsOffStyle() {
        today.setBackgroundResource(R.drawable.style_tab_left_off);
        schedualed.setBackgroundResource(R.drawable.style_tab_right_off);
        search.setBackgroundResource(R.drawable.style_tab_right_off);
        searchImage.setImageResource(R.drawable.search_blue);
        today.setTextColor(getResources().getColor(R.color.text_grey));
        schedualed.setTextColor(getResources().getColor(R.color.text_grey));
    }


    public void refreshOffersListsFromService() {

        if (pressedButton==SEARCH){
            searchOffers(true);
        }
        GetOffersDataAsyncTask getShipmentsDataAsyncTask=new GetOffersDataAsyncTask(getContext(),new IResultsInterface(){

            @Override
            public void onCompleteWithResult(ResultEntity result) {

                try {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    offersToday.clear();
                    offersScheduled.clear();
//                    offersComplete.clear();

                    JSONObject jsonObject = new JSONObject(result.getResult());
                    if (!jsonObject.getString("offers_today").equals("null")){
                        JSONArray shipmentsTodayJsonArray = jsonObject.getJSONArray("offers_today");
                        fillOffersArrayFromJsonArray(offersToday, shipmentsTodayJsonArray);
                    }

                    if (!jsonObject.getString("offers_scheduald").equals("null")){
                        JSONArray shipmentsScheduleJsonArray = jsonObject.getJSONArray("offers_scheduald");
                        fillOffersArrayFromJsonArray(offersScheduled, shipmentsScheduleJsonArray);
                        // fillShipmentsArrayFromJsonArray(shipmentsCompleteFilter,shipmentsScheduleJsonArray);

                    }

//                    if (!jsonObject.getString("complete").equals("null")){
//                        JSONArray shipmentsCompleteJsonArray = jsonObject.getJSONArray("complete");
//                        fillOffersArrayFromJsonArray(offersComplete, shipmentsCompleteJsonArray);
//
//                    }
                    if (getActivity()!=null&&((MainActivity) getActivity()).getNumberOfOffers()!=null) {
                        if ((offersToday.size() + offersScheduled.size()) > 0) {
                            ((MainActivity) getActivity()).getNumberOfOffers().setText((offersToday.size() + offersScheduled.size()) + "");
                            ((MainActivity) getActivity()).getNumberOfOffers().setVisibility(View.VISIBLE);
                        } else {
                            ((MainActivity) getActivity()).getNumberOfOffers().setVisibility(View.GONE);
                        }
                    }

                    if (isFirstTime){
                        if (offersToday.size()>0) {
                            //prevPressedButton=pressedButton;
                            pressedButton = TODAY;
                            onClick(today);
                        }
                        else if (offersScheduled.size()>0) {
                            //prevPressedButton=pressedButton;
                            pressedButton = SCHEDULE;
                            onClick(schedualed);
                        }
                        else {
                            pressedButton = TODAY;
                            onClick(today);
                        }
                    }
                    else
                        refreshOffersList();

                    if (isFirstTime&&getActivity()!=null&&getActivity().getIntent()!=null){
                        isFirstTime=false;
                        String action=getActivity().getIntent().getStringExtra("action");
                        boolean isFromOpenNotification=getActivity().getIntent().getBooleanExtra("isFromOpenNotification", false);
                        if (action!=null&&action.equals(NotificationReceiver.NOTIFICATION_OPEN)&&isFromOpenNotification){
                            Notification notification= (Notification) getActivity().getIntent().getSerializableExtra("notification");
                            if (notification.getType().equals("offer")){
                                Offer offer=getOfferById(notification.getMessageId());
                                if (offer!=null) {
                                    Intent intent=new Intent();
                                    intent.setClass(getActivity(), OfferActivity.class);
                                    intent.putExtra("offer", offer);
                                    getActivity().startActivity(intent);
                                }
                                else {
                                    utils.openPopupWindow(getActivity(),
                                            true,
                                            getString(R.string.notice),
                                            getString(R.string.offer_error),
                                            getString(R.string.ok),
                                            "",
                                            null,
                                            new PopupInterface() {});
                                    //utils.showInfoPopup(getActivity().getString(R.string.offer)
                                    //        ,getActivity().getString(R.string.offer_error),getActivity());
                                }
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    utils.showServerError(getActivity(), e.getMessage());
                }

            }
            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                swipeRefreshLayout.setRefreshing(false);
                //utils.showInfoPopup(getString(R.string.server_error), resultEntity.getResult(), getContext());
//                utils.openPopupWindow(getActivity(),
//                        true,
//                        getString(R.string.server_error),
//                        resultEntity.getResult(),
//                        getString(R.string.ok),
//                        "",
//                        null,
//                        new PopupInterface() {});
                progressBar.setVisibility(View.GONE);
                utils.showServerError(getActivity(),resultEntity.getResult());
            }
        });
        getShipmentsDataAsyncTask.execute();
    }

    private void fillOffersArrayFromJsonArray(List<Offer> offersList,JSONArray jsonArray){

        for (int i=0;i<jsonArray.length();i++){

            try {
                JSONObject jsonObject=jsonArray.getJSONObject(i);

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

                offersList.add(offer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void refreshOffersList(){

        tabsLayout.setTranslationY(0);
        hidingScrollListener.setmToolbarOffset(0);

        if (pressedButton==TODAY) {
            isListEmpty(offersToday,getString(R.string.no_offers));
            offersAdapter.refreshList(offersToday);
        }
        else if (pressedButton==SCHEDULE){
            isListEmpty(offersScheduled,getString(R.string.no_offers));
            offersAdapter.refreshList(offersScheduled);
        }
        //hidingScrollListener.setVisible();
        else if (pressedButton==SEARCH){
            isListEmpty(offersSearch,getString(R.string.search_no_results));
            offersAdapter.refreshList(offersSearch);
        }

    }

    private boolean isListEmpty(List<Offer>offers,String error){

        if (offers.size()>0) {
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
        refreshOffersListsFromService();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        refreshOffersListsFromService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(this.notificationBroadcastReceiver,
                new IntentFilter(MyFcmListenerService.CGM_BROADCAST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(notificationBroadcastReceiver);
    }

    BroadcastReceiver notificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.VISIBLE);
            refreshOffersListsFromService();
        }
    };

    private Offer getOfferById(long offerId){
        for (Offer offer:offersToday){
            if (offer.getId()==offerId)
                return offer;
        }
        for (Offer offer:offersScheduled){
            if (offer.getId()==offerId)
                return offer;
        }
        return null;
    }

    private void searchOffers(boolean isFullSearch){

        IResultsInterface iResultsInterface=new IResultsInterface() {
            @Override
            public void onCompleteWithResult(ResultEntity result) {
                advancedProgress.setVisibility(View.GONE);
                searchAdvanceHeader.setVisibility(View.GONE);
                offerList.setVisibility(View.VISIBLE);
//                searchKeyword.setText(searchString);
//                searchHeader.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray=new JSONArray(result.getResult());
                    offersSearch.clear();
                    fillOffersArrayFromJsonArray(offersSearch, jsonArray);
                    setButtonsOffStyle();
                    search.setBackgroundResource(R.drawable.style_tab_right_on);
                    searchImage.setImageResource(R.drawable.search_white);
//                    isListEmpty(offersSearch,getString(R.string.search_no_results));
//                    offersAdapter.refreshList(offersSearch);
                    refreshOffersList();
                } catch (JSONException e) {
                    e.printStackTrace();
                    setButtonsOffStyle();
                    search.setBackgroundResource(R.drawable.style_tab_right_on);
                    searchImage.setImageResource(R.drawable.search_white);
                    offersSearch.clear();
                    refreshOffersList();
//                    utils.showServerError(getContext(), e.getMessage());
//                    isListEmpty(offersSearch,getString(R.string.search_no_results));
//                    offersAdapter.refreshList(offersSearch);
                }

            }

            @Override
            public void onErrorWithResult(ResultEntity resultEntity) {
                advancedProgress.setVisibility(View.GONE);
                utils.showServerError(getContext(),resultEntity.getResult());
            }
        };
//        if (!isFullSearch){
//            String keyWord=searchKeyword.getText().toString();
//            searchString=keyWord;
//            SearchOffersAsyncTask searchOffersAsyncTask=new SearchOffersAsyncTask(keyWord, getActivity(), iResultsInterface);
//            searchOffersAsyncTask.execute();
//        }
//        else {
            /*String keyWord,String fromDate,String toDate,String street,
                          String city,String country,boolean boxes,boolean pallets,boolean truckloads,
                          Context context,IResultsInterface iResultsInterface*/
            //String keyWord=advancedSearchKeyWord.getText().toString();
//            searchString=keyWord;
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
//                searchString+=" "+fromDateTxt;
            }
            if (toDateCalendar!=null) {
                toDateTxt = utils.convertDateToString(toDateCalendar.getTime(), "dd/MM/yyyy");
//                searchString+=" "+toDateTxt;
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
            SearchOffersAsyncTask searchOffersAsyncTask=new SearchOffersAsyncTask(fromDateTxt,toDateTxt,streetTxt,
                    cityTxt,countryTxt,true,true,true,getActivity(), iResultsInterface);
            searchOffersAsyncTask.execute();
//        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode== Activity.RESULT_OK)
//            if (requestCode==ACCEPT_OFFER_RESULT){
//                ((MainActivity)getActivity()).isFromAcceptOffer=true;
//                ((MainActivity)getActivity()).offerId=data.getLongExtra("offer_id",-1);
//            }
//    }
}