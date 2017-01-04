package org.xdty.phone.number.net.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.OkHttp;

import javax.inject.Inject;

public class CustomNumberHandler implements NumberHandler<CustomNumber> {

    @Inject Context mContext;
    @Inject OkHttp mOkHttp;

    public CustomNumberHandler() {
        App.getAppComponent().inject(this);
    }

    @Override
    public String url() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getString("custom_api_url", "");
    }

    @Override
    public String key() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getString("custom_api_key", "");
    }

    @Override
    public CustomNumber find(String number) {
        String url = url();
        String key = key();
        CustomNumber n = null;
        if (!TextUtils.isEmpty(url)) {
            url = url + "?tel=" + number + "&key=" + key;
            n = mOkHttp.get(url, CustomNumber.class);
            n.setNumber(number);
        }
        return n;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CUSTOM;
    }
}
