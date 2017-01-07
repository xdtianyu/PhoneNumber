package org.xdty.phone.number.net.cloud;

import android.content.Context;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Utils;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class CloudHandler implements NumberHandler<CloudNumber>, CloudService {

    private final static String API_URL = "https://backend.xdty.org/api/v1/";
    public final MediaType JSON;

    @Inject OkHttpClient mOkHttpClient;

    @Inject Context mContext;

    public CloudHandler() {
        App.getAppComponent().inject(this);
        JSON = MediaType.parse("application/json; charset=utf-8");
    }

    @Override
    public String url() {
        return API_URL;
    }

    public String userId() {
        return "hPMOqSPMu4";
    }

    @Override
    public String key() {
        return "vHc7RS5f66uIeUu5z95B";
    }

    @Override
    public CloudNumber find(String number) {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CLOUD;
    }

    @Override
    public boolean put(CloudNumber number) {
        String url = API_URL + "caller";
        RequestBody body = RequestBody.create(JSON, Utils.gson().toJson(number));
        Request.Builder request = new Request.Builder()
                .url(url)
                .addHeader("X-LC-Id", userId())
                .addHeader("X-LC-Key", key())
                .post(body);
        try {
            Response response = mOkHttpClient.newCall(request.build()).execute();
            return response.code() == 201;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CloudNumber get(String number) {
        return null;
    }
}
