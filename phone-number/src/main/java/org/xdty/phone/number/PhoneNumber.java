package org.xdty.phone.number;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.NumberInfo;

import java.util.Arrays;

public class PhoneNumber {

    private final static String API_URL = "http://apis.baidu.com/" +
            "baidu_mobile_security/phone_number_service/" +
            "phone_information_query?location=true&tel=";
    private final static String META_DATA_KEY_URI = "org.xdty.phone.number.API_KEY";
    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";
    private String mApiKey;
    private OkHttpClient mOkHttpClient;
    private Callback mCallback;
    private Context mContext;
    private Handler mMainHandler;
    private Handler mHandler;
    public PhoneNumber(Context context, Callback callback) {
        mOkHttpClient = new OkHttpClient();
        mContext = context;
        mApiKey = getMetadata(META_DATA_KEY_URI);
        mCallback = callback;
        mMainHandler = new Handler(context.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public void setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
    }

    public String get(String... numbers) {
        return getNumberInfo(numbers).toString();
    }

    public void fetch(final String... numbers) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final NumberInfo numberInfo = getNumberInfo(numbers);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            if (numberInfo != null &&
                                    numberInfo.getResponse() != null &&
                                    numberInfo.getResponseHeader() != null) {
                                mCallback.onResponse(numberInfo);
                            } else {
                                mCallback.onResponseFailed(numberInfo);
                            }
                        }
                    }
                });
            }
        });
    }

    public NumberInfo getNumberInfo(String... numbers) {
        String url = API_URL + Arrays.toString(numbers).replaceAll(" |\\[|\\]", "");
        NumberInfo numberInfo = null;
        Request.Builder request = new Request.Builder().url(url);
        request.header("apikey", mApiKey);
        try {
            com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            Gson gson = new Gson();
            numberInfo = gson.fromJson(s, NumberInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numberInfo;
    }

    private String getMetadata(String name) {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface Callback {
        void onResponse(NumberInfo numberInfo);

        void onResponseFailed(NumberInfo numberInfo);
    }
}