package org.xdty.phone.number.net.soguo;

import android.util.Log;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Utils;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SogouNumberHandler implements NumberHandler<SogouNumber> {

    @Inject OkHttpClient mOkHttpClient;

    public SogouNumberHandler() {
        App.getAppComponent().inject(this);
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
        okhttp3.Response response = null;
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
                response.body().close();
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
