package org.xdty.phone.number.util;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class OkHttp {

    private static final String TAG = OkHttp.class.getSimpleName();

    private OkHttpClient mOkHttpClient;

    public static OkHttp get() {
        return SingletonHelper.INSTANCE;
    }

    public OkHttpClient client() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(5, TimeUnit.SECONDS);
            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }

    public <T> T get(String url, Class<T> numberClass) {
        Request.Builder request = new Request.Builder().url(url)
                .header("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        okhttp3.Response response = null;
        T t = null;
        String s = null;
        try {
            response = client().newCall(request.build()).execute();
            s = response.body().string();
            s = s.replace("show(", "").replace(")", "");
            t = Utils.gson().fromJson(s, numberClass);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error: " + url + ":" + s);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
        return t;
    }

    public void setClient(OkHttpClient client) {
        mOkHttpClient = client;
    }

    private final static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }
}
