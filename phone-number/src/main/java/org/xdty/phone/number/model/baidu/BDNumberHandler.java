package org.xdty.phone.number.model.baidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BDNumberHandler implements NumberHandler<BDNumber> {

    public transient final static String META_DATA_KEY_URI =
            "org.xdty.phone.number.API_KEY";
    public transient final static String API_KEY = "baidu_api_key";

    private Context mContext;
    private OkHttpClient mOkHttpClient;

    public BDNumberHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
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
            apiKey = Utils.getMetadata(mContext, META_DATA_KEY_URI);
        }

        if (apiKey != null && apiKey.contains(",")) {
            String[] keys = TextUtils.split(apiKey, ",");
            Random random = new Random();
            apiKey = keys[random.nextInt(keys.length)];
        }

        return apiKey;
    }

    @Override
    public BDNumber find(String number) {

        if (TextUtils.isEmpty(number)) {
            return null;
        }

        if (number.startsWith("+") && !number.startsWith("+86")) {
            return null;
        }

        String url = url() + number;
        Request.Builder request = new Request.Builder().url(url);
        request.header("apikey", key());
        BDNumber bdNumber = null;
        Response response = null;
        try {
            response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            BDNumberInfo numberInfo = Utils.gson().fromJson(s, BDNumberInfo.class);
            if (numberInfo.getNumbers().size() > 0) {
                bdNumber = new BDNumber(numberInfo.getNumbers().get(0), number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null && response.body() != null) {
                try {
                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bdNumber;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_BD_DEAD;
    }
}
