package org.xdty.phone.number.net.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

public class CustomNumberHandler implements NumberHandler<CustomNumber> {

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public CustomNumberHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
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
        if (!TextUtils.isEmpty(url)) {
            url = url + "?tel=" + number + "&key=" + key;
            Request.Builder request = new Request.Builder().url(url);
            try {
                com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                        request.build()).execute();
                String s = response.body().string();
                CustomNumber n = Utils.gson().fromJson(s, CustomNumber.class);
                n.setNumber(number);
                return n;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
