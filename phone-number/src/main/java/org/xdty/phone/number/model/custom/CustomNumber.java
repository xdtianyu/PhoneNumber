package org.xdty.phone.number.model.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.model.Type;

public class CustomNumber implements INumber, NumberHandler<CustomNumber> {
    String reason;
    int error_code;
    Result result;

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public CustomNumber(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String getName() {
        return result.company + " " + result.name;
    }

    @Override
    public String getProvince() {
        return result.province;
    }

    @Override
    public Type getType() {
        return Type.POI;
    }

    @Override
    public String getCity() {
        return result.city;
    }

    @Override
    public String getNumber() {
        return result.phone;
    }

    @Override
    public String getProvider() {
        return result.provider;
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
                Gson gson = new Gson();
                return gson.fromJson(s, CustomNumber.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isValid() {
        return result != null && error_code == 0;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CUSTOM;
    }

    class Result {
        String province;
        String city;
        String provider;
        String phone;
        String name;
        String company;
        String info;
    }
}
