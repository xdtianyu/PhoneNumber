package org.xdty.phone.number.model.soguo;

import android.content.Context;
import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;

public class SogouNumberHandler implements NumberHandler<SogouNumber> {

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public SogouNumberHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String url() {
        return "http://data.haoma.sogou.com/vrapi/query_number.php?type=json&callback=show&number=";
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public SogouNumber find(String number) {
        String url = url();
        if (!TextUtils.isEmpty(url)) {
            url = url + number;
            Request.Builder request = new Request.Builder().url(url);
            try {
                com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                        request.build()).execute();
                String s = response.body().string();
                s = s.replace("show(", "").replace(")", "");
                SogouNumber sogouNumber = GSON.fromJson(s, SogouNumber.class);
                sogouNumber.number = number;
                return sogouNumber;
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
        return INumber.API_ID_SG;
    }
}
