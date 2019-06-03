package org.xdty.phone.number.model.soguo;

import android.content.Context;
import android.util.Log;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SogouNumberHandler implements NumberHandler<SogouNumber> {

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public SogouNumberHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String url() {
        return "https://data.haoma.sogou.com/vrapi/query_number.php?type=json&callback=show&number=";
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
        Response response = null;
        SogouNumber sogouNumber = null;
        String s = null;
        try {
            response = mOkHttpClient.newCall(request.build()).execute();
            s = response.body().string();
            s = s.replace("show(", "").replace(")", "")
                    .replaceAll("&quot;", "\"");
            sogouNumber = Utils.gson().fromJson(s, SogouNumber.class);
            sogouNumber.number = number;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SogouNumberHandler", "error: " + number + ":" + s);
        } finally {
            if (response != null && response.body() != null) {
                try {
                    response.body().close();
                } catch (Exception e) {
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
