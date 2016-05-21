package org.xdty.phone.number.model.juhe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

public class JuHeNumberHandler implements NumberHandler<JuHeNumber> {

    public transient final static String META_DATA_KEY_URI =
            "org.xdty.phone.number.JUHE_API_KEY";
    public transient final static String API_KEY = "juhe_api_key";

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public JuHeNumberHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String url() {
        return "https://op.juhe.cn/onebox/phone/query?tel=";
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
    public JuHeNumber find(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }

        if (number.startsWith("+") && !number.startsWith("+86")) {
            return null;
        }
        String url = url() + number + "&key=" + key();
        Request.Builder request = new Request.Builder().url(url);
        try {
            com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            return Utils.gson().fromJson(s, JuHeNumber.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_JH;
    }
}
