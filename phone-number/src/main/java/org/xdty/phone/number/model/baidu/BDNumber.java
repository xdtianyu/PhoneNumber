package org.xdty.phone.number.model.baidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class BDNumber implements INumber<BDNumber> {

    public transient final static String META_DATA_KEY_URI =
            "org.xdty.phone.number.API_KEY";
    public transient final static String API_KEY = "baidu_api_key";

    private BDResponse mBDResponse;
    private BDLocation mBDLocation;

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public BDNumber(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    private BDNumber(BDResponse bdResponse, String number) {
        mBDResponse = bdResponse;
        mBDLocation = mBDResponse.getLocation();
        mBDResponse.setNumber(number);
    }

    @Override
    public String getName() {
        return mBDResponse.getName();
    }

    @Override
    public String getProvince() {
        return mBDLocation == null ? "" : mBDLocation.province;
    }

    @Override
    public Type getType() {
        return mBDResponse.getType();
    }

    @Override
    public String getCity() {
        return mBDLocation == null ? "" : mBDLocation.city;
    }

    @Override
    public String getNumber() {
        return mBDResponse.getNumber();
    }

    @Override
    public String getProvider() {
        return mBDLocation == null ? "" : mBDLocation.operators;
    }

    @Override
    public String url() {
        return "http://apis.baidu.com/baidu_mobile_security/phone_number_service/" +
                "phone_information_query?location=true&tel=";
    }

    @Override
    public String key() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = pref.getString(API_KEY, "");
        if (apiKey.isEmpty()) {
            apiKey = PhoneNumber.getMetadata(mContext, META_DATA_KEY_URI);
        }
        return apiKey;
    }

    @Override
    public BDNumber find(String number) {
        String url = url() + number;
        Request.Builder request = new Request.Builder().url(url);
        request.header("apikey", key());
        try {
            com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            Gson gson = new Gson();
            BDNumberInfo numberInfo = gson.fromJson(s, BDNumberInfo.class);
            if (numberInfo.getNumbers().size() > 0) {
                return new BDNumber(numberInfo.getNumbers().get(0), number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mBDResponse.getCount();
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isValid() {
        return mBDResponse != null;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_BD;
    }
}
