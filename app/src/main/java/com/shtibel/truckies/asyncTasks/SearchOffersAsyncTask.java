package com.shtibel.truckies.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shtibel.truckies.StartApplication;
import com.shtibel.truckies.generalClasses.SharedPreferenceManager;
import com.shtibel.truckies.jsonParser.IResultsInterface;
import com.shtibel.truckies.jsonParser.JsonParser;
import com.shtibel.truckies.jsonParser.ResultEntity;

/**
 * Created by Shtibel on 06/09/2016.
 */
public class SearchOffersAsyncTask extends AsyncTask<Void,Void,ResultEntity> {

    //&carrier_id=4
    // &pallets=
    // &boxes=1
    // &truckload=
    // &street_name=HaRav Yeshayahu Meshorer
    // &city=Petah Tikva
    // &country=Israel
    private static final String URL= StartApplication.START_URL+"/tr-api/?action=searchOffer";

    IResultsInterface iResultsInterface;
    Context context;

   // String keyWord;
    String fromDate="";
    String toDate="";
    String street="";
    String city="";
    String country="";

    boolean boxes=true;
    boolean pallets=true;
    boolean truckloads=true;

    boolean isFullSearch=false;

    String fullUrl;

    public SearchOffersAsyncTask(String fromDate,String toDate,String street,
                          String city,String country,boolean boxes,boolean pallets,boolean truckloads,
                          Context context,IResultsInterface iResultsInterface){
       // this.keyWord=keyWord;
        this.fromDate=fromDate;
        this.toDate=toDate;
        this.street=street;
        this.city=city;
        this.country=country;
        this.boxes=boxes;
        this.pallets=pallets;
        this.truckloads=truckloads;
        this.context=context;
        this.iResultsInterface=iResultsInterface;
        isFullSearch=true;
    }

    public SearchOffersAsyncTask(String keyWord,Context context,IResultsInterface iResultsInterface){
       // this.keyWord=keyWord;
        this.context=context;
        this.iResultsInterface=iResultsInterface;
        isFullSearch=false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fullUrl=URL;
        String carrierIdTxt="&carrier_id="+ SharedPreferenceManager.getInstance(context).getCarrierId();
        //String keywordTxt="&keyword="+keyWord.trim();

        fullUrl+=carrierIdTxt;

        if (isFullSearch) {
            String palletsTxt="&pallets="+(pallets?1:0);
            String boxesTxt="&boxes="+(boxes?1:0);
            String truckloadTxt="&truckload="+(truckloads?1:0);

            fullUrl+=palletsTxt+boxesTxt+truckloadTxt;

            if (!street.equals("")) {
                String streetNameTxt = "&street_name=" + street;
                fullUrl+=streetNameTxt;
            }
            if (!city.equals("")){
                String cityTxt = "&city=" + city.trim();
                fullUrl+=cityTxt;
            }
            if (!country.equals("")) {
                String countryTxt = "&country=" + country.trim();
                fullUrl+=countryTxt;
            }
            if (!fromDate.equals("")){
                String fromDateTxt="&from_date="+fromDate;
                fullUrl+=fromDateTxt;
            }
            if (!toDate.equals("")){
                String toDateTxt="&till_date="+toDate;
                fullUrl+=toDateTxt;
            }
        }
    }

    @Override
    protected ResultEntity doInBackground(Void... voids) {
        ResultEntity resultEntity= JsonParser.requestGetOkHttp(fullUrl);
        return resultEntity;
    }

    @Override
    protected void onPostExecute(ResultEntity resultEntity) {
        super.onPostExecute(resultEntity);
        if (resultEntity.getIsOk())
            iResultsInterface.onCompleteWithResult(resultEntity);
        else
            iResultsInterface.onErrorWithResult(resultEntity);
    }
}
