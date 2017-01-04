package org.xdty.phone.number.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public final class OkHttp {

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

    public void setClient(OkHttpClient client) {
        mOkHttpClient = client;
    }

    private final static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }
}
