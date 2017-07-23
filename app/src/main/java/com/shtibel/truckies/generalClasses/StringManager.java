package com.shtibel.truckies.generalClasses;


import android.support.v4.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Shtibel on 09/01/2017.
 */
public class StringManager {
    private static StringManager sInstance = null;
    private static String mDefaultLanguageCode = "en";

    private Map<String, JSONObject> mLanguageMap = new ArrayMap<>();

    private StringManager() {
    }

    public static StringManager getInstance() {
        if (sInstance == null) {
            synchronized (StringManager.class) {
                if (sInstance == null)
                    sInstance = new StringManager();
            }
        }
        return sInstance;
    }

    public static void setDefaultLanguageCode(String languageCode) {
        mDefaultLanguageCode = languageCode;
    }

    public void addLanguage(String languageCode, JSONObject json) {
        mLanguageMap.put(languageCode, json);
    }

    public String getString(String key) {
        try {
            return mLanguageMap.get(mDefaultLanguageCode).getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getString(String languageCode, String key) {
        try {
            return mLanguageMap.get(languageCode).getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}

