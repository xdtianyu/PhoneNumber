package org.xdty.phone.number;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import com.squareup.okhttp.OkHttpClient;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.model.baidu.BDNumber;
import org.xdty.phone.number.model.custom.CustomNumber;
import org.xdty.phone.number.model.google.GooglePhoneNumber;
import org.xdty.phone.number.model.juhe.JuHeNumber;
import org.xdty.phone.number.model.offline.OfflineRecord;
import org.xdty.phone.number.model.special.SpecialNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneNumber {

    public final static String API_TYPE = "api_type";

    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";

    private Callback mCallback;
    private Handler mMainHandler;
    private Handler mHandler;
    private SharedPreferences mPref;

    private List<NumberHandler> mSupportHandlerList;

    public PhoneNumber(Context context, Callback callback) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mCallback = callback;
        mMainHandler = new Handler(context.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        addNumberHandler(new SpecialNumber(context));
        addNumberHandler(new OfflineRecord(context));
        addNumberHandler(new GooglePhoneNumber());
        addNumberHandler(new CustomNumber(context, mOkHttpClient));
        addNumberHandler(new BDNumber(context, mOkHttpClient));
        addNumberHandler(new JuHeNumber(context, mOkHttpClient));
    }

    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addNumberHandler(NumberHandler handler) {
        if (mSupportHandlerList == null) {
            mSupportHandlerList = new ArrayList<>();
        }
        mSupportHandlerList.add(handler);
    }

    public void fetch(String... numbers) {
        for (final String number : numbers) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final INumber offlineNumber = getOfflineNumber(number);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                if (offlineNumber != null && offlineNumber.isValid()) {
                                    mCallback.onResponseOffline(offlineNumber);
                                }
                            }
                        }
                    });

                    if (offlineNumber instanceof SpecialNumber) {
                        return;
                    }

                    final INumber onlineNumber = getNumber(number);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                if (onlineNumber != null && onlineNumber.isValid()) {
                                    mCallback.onResponse(onlineNumber);
                                } else {
                                    mCallback.onResponseFailed(onlineNumber);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private INumber getNumber(String number) {

        int apiType = mPref.getInt(API_TYPE, INumber.API_ID_BD);

        INumber result = null;

        for (NumberHandler handler : mSupportHandlerList) {
            if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CUSTOM) {
                INumber i = handler.find(number);
                if (i != null && i.isValid()) {
                    result = i;
                }
            }
        }

        if (result == null || !result.isValid()) {
            for (NumberHandler handler : mSupportHandlerList) {
                if (handler.isOnline() && handler.getApiId() == apiType) {
                    INumber i = handler.find(number);
                    if (i != null && i.isValid()) {
                        result = i;
                    }
                }
            }
        }

        if (result == null || !result.isValid()) {
            for (NumberHandler handler : mSupportHandlerList) {
                if (handler.isOnline() && handler.getApiId() != apiType) {
                    INumber i = handler.find(number);
                    if (i != null && i.isValid()) {
                        result = i;
                    }
                }
            }
        }

        return result;
    }

    private INumber getOfflineNumber(String number) {

        for (NumberHandler handler : mSupportHandlerList) {
            if (!handler.isOnline()) {
                INumber i = handler.find(number);
                if (i != null && i.isValid()) {
                    return i;
                }
            }
        }
        return null;
    }

    public interface Callback {
        void onResponseOffline(INumber number);

        void onResponse(INumber number);

        void onResponseFailed(INumber number);
    }
}