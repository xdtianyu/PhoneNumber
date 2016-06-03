package org.xdty.phone.number.model.soguo;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.IOException;

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
        String url = url() + number;

        Request.Builder request = new Request.Builder().url(url)
                .header("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        com.squareup.okhttp.Response response = null;
        SogouNumber sogouNumber = null;
        String s = null;
        try {
            response = mOkHttpClient.newCall(request.build()).execute();
            s = response.body().string();
            s = s.replace("show(", "").replace(")", "");
            sogouNumber = Utils.gson().fromJson(s, SogouNumber.class);
            sogouNumber.number = number;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SogouNumberHandler", "error: " + number + ":" + s);
        } finally {
            if (response != null && response.body() != null) {
                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sogouNumber;
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
