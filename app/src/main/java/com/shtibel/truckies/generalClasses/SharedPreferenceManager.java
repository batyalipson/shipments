package com.shtibel.truckies.generalClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shtibel on 21/06/2015.
 */
public class SharedPreferenceManager {
    static SharedPreferences preferences;
    public static final int AVAILABLE_STATUS_OFFLINE=1;
    public static final int AVAILABLE_STATUS_AVAILABLE=2;
    public static final int AVAILABLE_STATUS_DISPATCH=3;
    public static final int AVAILABLE_STATUS_UNAVAILABLE=4;

    private static SharedPreferenceManager sharedPreferenceManager=null;

    private Context context;

    private SharedPreferenceManager(Context context)
    {
        this.context=context;
        preferences=context.getSharedPreferences("AppPreference",0);
    }

    public static SharedPreferenceManager getInstance(Context context){
        if(sharedPreferenceManager==null){
            sharedPreferenceManager=new SharedPreferenceManager(context);
        }
        return sharedPreferenceManager;
    }

    public void clear()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void saveUserId(long id){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("userId", id);
        editor.commit();
    }

    public void saveUserName(String name){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", name);
        editor.commit();
    }

    public long getUserId(){
        return preferences.getLong("userId", -1);
    }
    public String getUserName(){
        return preferences.getString("userName", "");
    }

    public void saveAvailableStatus(int availableStatus){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("availableStatus", availableStatus);
        editor.commit();
    }

    public int getAvailableStatus(){
        return preferences.getInt("availableStatus", AVAILABLE_STATUS_UNAVAILABLE);
    }

    public void saveUserFirstName(String firstName){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userFirstName", firstName);
        editor.commit();
    }
    public String getUserFirstName(){
        return preferences.getString("userFirstName", "");
    }

    public void saveUserLastName(String lastName){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userLastName", lastName);
        editor.commit();
    }
    public String getUserLastName(){
        return preferences.getString("userLastName","");
    }
    public void saveUserPhone(String phone){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", phone);
        editor.commit();
    }
    public String getUserPhone(){
        return preferences.getString("phone","");
    }

    public void saveUseEmail(String email){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("email",email);
        editor.commit();
    }

    public String getUserEmail(){
        return preferences.getString("email","");
    }

    public void saveUserPassword(String password){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", password);
        editor.commit();
    }
    public String getUserPassword(){
        return preferences.getString("password","");
    }

    public void saveUserTruckType(String type){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("truckType", type);
        editor.commit();
    }
    public String getUserTruckType(){
        return preferences.getString("truckType","");
    }

    public void saveUserImageUrl(String url){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userImageUrl", url);
        editor.commit();
    }
    public String getUserImageUrl(){
        return preferences.getString("userImageUrl","");
    }

    public void saveUserStatus(int userStatus){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userStatus", userStatus);
        editor.commit();
    }
    public int getUserStatus(){
        return preferences.getInt("userStatus", 0);
    }


    public void saveCarrierId(long carrierId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("carrierId", carrierId);
        editor.commit();
    }

    public long getCarrierId(){
        return preferences.getLong("carrierId", 0);
    }
    public void saveCanAcceptOffers(int canAcceptOffers){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("canAcceptOffers", canAcceptOffers);
        editor.commit();
    }
    public int getCanAcceptOffers(){
       return preferences.getInt("canAcceptOffers", 0);
    }

    public void saveCanSeePrice(int canSeePrice){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("canSeePrice", canSeePrice);
        editor.commit();
    }
    public int getCanSeePrice(){
        return preferences.getInt("canSeePrice",0);
    }

    public void saveCanSeeOffers(int canSeeOffers){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("canSeeOffers", canSeeOffers);
        editor.commit();
    }
    public int getCanSeeOffers(){
        return preferences.getInt("canSeeOffers",0);
    }

    public void saveTruckType(long id){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("truckId", id);
        editor.commit();
    }

    public long getTruckType(){
        return preferences.getLong("truckId", 0);
    }

    public void saveIsLogin(boolean isLogin){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", isLogin);
        editor.commit();
    }

    public boolean getIsLogin(){
        return preferences.getBoolean("isLogin", false);
    }

    public void saveIsMute(boolean isMute){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isMute",isMute);
        editor.commit();
    }
    public boolean isMute(){
        return preferences.getBoolean("isMute",false);
    }
    public void saveIsFirstTime(boolean isFirstTime){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isFirstTime",isFirstTime);
        editor.commit();
    }
    public boolean isFirstTime(){
        return preferences.getBoolean("isFirstTime",true);
    }
    public void saveSupportPhone(String supportPhone){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("supportPhone", supportPhone);
        editor.commit();
    }

    public String getSupportPhone(){
        return preferences.getString("supportPhone", "");
    }

    public void saveTimeShowShipperInfo(int timeShowShipperInfo){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("timeShowShipperInfo", timeShowShipperInfo);
        editor.commit();
    }
    public int getTimeShowShipperInfo(){
        return preferences.getInt("timeShowShipperInfo", 0);
    }



    public void saveLastVersion(String lastVersion){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("lastVersion", lastVersion);
        editor.commit();
    }
    public String getLastVersion(){
        return preferences.getString("lastVersion", "");
    }

    public void saveLastTestingVersion(String lastVersion){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("lastTestingVersion", lastVersion);
        editor.commit();
    }
    public String getLastTestingVersion(){
        return preferences.getString("lastTestingVersion","");
    }

    public void clearUserData(){
        saveUserId(-1);
        saveUserName("");
        saveUserFirstName("");
        saveUserLastName("");
        saveUserPhone("");
        saveUseEmail("");
        saveUserPassword("");
        saveUserTruckType("");
        saveCarrierId(0);
        saveCanAcceptOffers(0);
        saveCanSeePrice(0);
        saveTruckType(0);
        saveUserImageUrl("");
        saveIsLogin(false);
        saveAvailableStatus(AVAILABLE_STATUS_UNAVAILABLE);
    }
}
