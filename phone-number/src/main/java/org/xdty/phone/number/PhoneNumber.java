package org.xdty.phone.number;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.NumberInfo;
import org.xdty.phone.number.model.ResponseHeader;
import org.xdty.phone.number.model.google.GooglePhoneNumber;
import org.xdty.phone.number.model.juhe.JuHeNumber;
import org.xdty.phone.number.model.offline.OfflineRecord;
import org.xdty.phone.number.model.special.SpecialNumber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneNumber {

    public final static String API_TYPE = "api_type";
    public final static int API_TYPE_BD = 0;
    public final static int API_TYPE_JH = 1;

    private final static String API_URL = "http://apis.baidu.com/" +
            "baidu_mobile_security/phone_number_service/" +
            "phone_information_query?location=true&tel=";

    private final static String JUHE_API_URL = "https://op.juhe.cn/" +
            "onebox/phone/" +
            "query?tel=";

    private final static String META_DATA_KEY_URI = "org.xdty.phone.number.API_KEY";
    private final static String META_DATA_JUHE_KEY_URI = "org.xdty.phone.number.JUHE_API_KEY";

    private final static String API_KEY = "baidu_api_key";
    private final static String JUHE_API_KEY = "juhe_api_key";

    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";
    private String mBDApiKey;
    private String mJHApiKey;
    private OkHttpClient mOkHttpClient;
    private Callback mCallback;
    private Context mContext;
    private Handler mMainHandler;
    private Handler mHandler;

    public PhoneNumber(Context context, Callback callback) {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
        mContext = context;
        mBDApiKey = getBDApiKey();
        mJHApiKey = getJHApiKey();
        mCallback = callback;
        mMainHandler = new Handler(context.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public String get(String... numbers) {
        return getNumberInfo(numbers).toString();
    }

    public void fetch(final String... numbers) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final NumberInfo offlineNumberInfo = getOfflineOrSpecialInfo(numbers);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            if (isValid(offlineNumberInfo)) {
                                mCallback.onResponseOffline(offlineNumberInfo);
                            }
                        }
                    }
                });

                final NumberInfo numberInfo = getNumberInfo(numbers);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            if (isValid(numberInfo)) {
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int apiType = preferences.getInt(API_TYPE, API_TYPE_BD);

        NumberInfo numberInfo = getSpecialNumberInfo(numbers);
        if (!isValid(numberInfo)) {

            switch (apiType) {
                case API_TYPE_BD:
                    numberInfo = getBDNumberInfo(numbers);
                    break;
                case API_TYPE_JH:
                    numberInfo = getJHNumberInfo(numbers);
                    break;
                default:
                    numberInfo = getBDNumberInfo(numbers);
                    break;
            }
        }

        if (!isValid(numberInfo)) {
            if (apiType == API_TYPE_BD) {
                numberInfo = getJHNumberInfo(numbers);
            } else {
                numberInfo = getBDNumberInfo(numbers);
            }
        }

        if (!isValid(numberInfo)) {
            numberInfo = getOfflineNumberInfo(numbers);
        }

        return numberInfo;
    }

    private NumberInfo getSpecialNumberInfo(String... numbers) {
        NumberInfo numberInfo = new NumberInfo();
        Map<String, Number> r = new HashMap<>();
        ResponseHeader header = new ResponseHeader();
        SpecialNumber specialNumber = new SpecialNumber(mContext);
        for (String n : numbers) {
            SpecialNumber.Zone zone = specialNumber.find(n);
            if (zone != null) {
                Number number = zone.toNumber();
                if (number != null) {
                    r.put(n, number);
                }
            }
        }

        numberInfo.setResponse(r);
        numberInfo.setResponseHeader(header);
        return numberInfo;
    }

    private NumberInfo getBDNumberInfo(String... numbers) {
        String url = API_URL + Arrays.toString(numbers).replaceAll(" |\\[|\\]", "");
        NumberInfo numberInfo = null;
        Request.Builder request = new Request.Builder().url(url);
        request.header("apikey", mBDApiKey);
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

    private NumberInfo getJHNumberInfo(String... numbers) {
        NumberInfo numberInfo = new NumberInfo();
        Map<String, Number> r = new HashMap<>();
        ResponseHeader header = null;

        for (String number : numbers) {
            String url = JUHE_API_URL + number + "&key=" + mJHApiKey;
            Request.Builder request = new Request.Builder().url(url);
            try {
                com.squareup.okhttp.Response response = mOkHttpClient.newCall(
                        request.build()).execute();
                String s = response.body().string();
                Gson gson = new Gson();
                JuHeNumber juHeNumber = gson.fromJson(s, JuHeNumber.class);
                Number n = juHeNumber.toNumber();
                if (n != null) {
                    r.put(number, n);

                    if (header == null) {
                        header = new ResponseHeader();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        numberInfo.setResponse(r);
        numberInfo.setResponseHeader(header);

        return numberInfo;
    }

    private NumberInfo getOfflineOrSpecialInfo(String... numbers) {
        NumberInfo numberInfo = getSpecialNumberInfo(numbers);
        if (!isValid(numberInfo)) {
            numberInfo = getOfflineNumberInfo(numbers);
        }
        return numberInfo;
    }

    private NumberInfo getOfflineNumberInfo(String... numbers) {
        NumberInfo numberInfo = new NumberInfo();
        Map<String, Number> r = new HashMap<>();
        ResponseHeader header = new ResponseHeader();
        OfflineRecord offlineRecord = new OfflineRecord(mContext);
        for (String n : numbers) {
            n = n.replaceAll("\\+", "");
            OfflineRecord.Record record = offlineRecord.find(n);
            if (record != null) {
                r.put(n, record.toNumber());
            } else {
                Number number = GooglePhoneNumber.getNumber(n);
                if (number != null) {
                    r.put(n, number);
                }
            }
        }
        numberInfo.setOffline(true);
        numberInfo.setResponse(r);
        numberInfo.setResponseHeader(header);
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

    private String getBDApiKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = preferences.getString(API_KEY, "");
        if (apiKey.isEmpty()) {
            apiKey = getMetadata(META_DATA_KEY_URI);
        }
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.mBDApiKey = apiKey;
    }

    public void setApiKey(String apiKey, int type) {
        switch (type) {
            case API_TYPE_BD:
                this.mBDApiKey = apiKey;
                break;
            case API_TYPE_JH:
                this.mJHApiKey = apiKey;
                break;
            default:
                this.mBDApiKey = apiKey;
        }
    }

    private String getJHApiKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = preferences.getString(JUHE_API_KEY, "");
        if (apiKey.isEmpty()) {
            apiKey = getMetadata(META_DATA_JUHE_KEY_URI);
        }
        return apiKey;
    }

    public boolean isValid(NumberInfo numberInfo) {
        return numberInfo != null &&
                numberInfo.getResponse() != null &&
                numberInfo.getResponseHeader() != null &&
                numberInfo.getNumbers().size() > 0;
    }

    public interface Callback {
        void onResponseOffline(NumberInfo numberInfo);

        void onResponse(NumberInfo numberInfo);

        void onResponseFailed(NumberInfo numberInfo);
    }
}