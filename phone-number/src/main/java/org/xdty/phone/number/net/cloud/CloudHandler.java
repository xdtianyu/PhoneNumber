package org.xdty.phone.number.net.cloud;

import android.content.Context;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class CloudHandler implements NumberHandler<CloudNumber>, CloudService {

    private final static String API_URL = "https://backend.xdty.org/api/v1/";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    public final MediaType JSON;
    @Inject OkHttpClient mOkHttpClient;
    @Inject Context mContext;

    public CloudHandler() {
        App.getAppComponent().inject(this);
        JSON = MediaType.parse("application/json; charset=utf-8");
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private static String hmac(String data, String key) {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            return toHexString(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
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
        String data = Utils.gson().toJson(number);
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", userId() + ":" + hmac(data, key()))
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
