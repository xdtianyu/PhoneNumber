package org.xdty.phone.number.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;

import javax.inject.Inject;

public final class Settings {

    private final static String API_TYPE = "api_type";

    @Inject
    Context mContext;

    @Inject
    SharedPreferences mPref;

    private Settings() {
        App.getAppComponent().inject(this);
    }

    public static Settings getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public boolean isOnlyOffline() {
        return mPref.getBoolean(mContext.getString(R.string.only_offline_key), false);
    }

    public int getApiType() {
        return mPref.getInt(API_TYPE, INumber.API_ID_JH);
    }

    private final static class SingletonHelper {
        @SuppressLint("StaticFieldLeak")
        private final static Settings INSTANCE = new Settings();
    }
}
