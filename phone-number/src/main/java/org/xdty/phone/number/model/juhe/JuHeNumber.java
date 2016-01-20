package org.xdty.phone.number.model.juhe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.model.Type;

public class JuHeNumber implements INumber, NumberHandler<JuHeNumber> {

    public transient final static String META_DATA_KEY_URI =
            "org.xdty.phone.number.JUHE_API_KEY";
    public transient final static String API_KEY = "juhe_api_key";

    String reason;
    Result result;
    int error_code;

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public JuHeNumber(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String getName() {
        return result.hy == null ? result.rpt_type : result.hy.name;
    }

    @Override
    public String getProvince() {
        return result.province;
    }

    @Override
    public Type getType() {
        return result.iszhapian == 0 ? Type.NORMAL : Type.REPORT;
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
        return result.sp;
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
        String url = url() + number + "&key=" + key();
        Request.Builder request = new Request.Builder().url(url);
        try {
            com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(s, JuHeNumber.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return result.rpt_cnt;
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
        return INumber.API_ID_JH;
    }


    class Result {
        int iszhapian;
        String province;
        String city;
        String sp;
        String phone;
        String rpt_type;
        String rpt_comment;
        int rpt_cnt;
        Hy hy;
        String countDesc;

    }

    class Hy {
        String city;
        String lng;
        String lat;
        String name;
        String addr;
        String tel;
    }

}
