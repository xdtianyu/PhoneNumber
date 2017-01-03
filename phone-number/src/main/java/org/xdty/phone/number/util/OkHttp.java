package org.xdty.phone.number.util;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class OkHttp {

    private OkHttpClient mOkHttpClient;

    public static OkHttp get() {
        return SingletonHelper.INSTANCE;
    }

    public OkHttpClient client() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
            mOkHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
        }
        return mOkHttpClient;
    }

    public void setClient(OkHttpClient client) {
        mOkHttpClient = client;
    }

    private final static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }
}
