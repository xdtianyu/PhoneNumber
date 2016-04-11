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
import org.xdty.phone.number.model.baidu.BDNumberHandler;
import org.xdty.phone.number.model.common.CommonHandler;
import org.xdty.phone.number.model.custom.CustomNumberHandler;
import org.xdty.phone.number.model.google.GoogleNumberHandler;
import org.xdty.phone.number.model.juhe.JuHeNumberHandler;
import org.xdty.phone.number.model.marked.MarkedHandler;
import org.xdty.phone.number.model.offline.OfflineHandler;
import org.xdty.phone.number.model.special.SpecialNumber;
import org.xdty.phone.number.model.special.SpecialNumberHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneNumber {
    public final static String API_TYPE = "api_type";
    private static final String TAG = PhoneNumber.class.getSimpleName();
    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";
    private Callback mCallback;
    private Handler mMainHandler;
    private Handler mHandler;
    private SharedPreferences mPref;
    private List<NumberHandler> mSupportHandlerList;
    private Context mContext;
    private boolean mOffline = false;

    public PhoneNumber(Context context) {
        this(context, false, null);
    }

    public PhoneNumber(Context context, Callback callback) {
        this(context, false, callback);
    }

    public PhoneNumber(Context context, boolean offline, Callback callback) {
        mContext = context.getApplicationContext();
        mOffline = offline;
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mCallback = callback;
        mMainHandler = new Handler(mContext.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OkHttpClient mOkHttpClient = new OkHttpClient();
                mOkHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
                addNumberHandler(new SpecialNumberHandler(mContext));
                addNumberHandler(new CommonHandler(mContext));
                addNumberHandler(new MarkedHandler(mContext));
                addNumberHandler(new OfflineHandler(mContext));
                addNumberHandler(new GoogleNumberHandler(mContext));
                addNumberHandler(new CustomNumberHandler(mContext, mOkHttpClient));
                addNumberHandler(new BDNumberHandler(mContext, mOkHttpClient));
                addNumberHandler(new JuHeNumberHandler(mContext, mOkHttpClient));
            }
        });
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

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void addNumberHandler(NumberHandler handler) {
        if (mSupportHandlerList == null) {
            mSupportHandlerList = Collections.synchronizedList(new ArrayList<NumberHandler>());
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
                                } else {
                                    mCallback.onResponseFailed(offlineNumber, false);
                                }
                            }
                        }
                    });

                    if (offlineNumber instanceof SpecialNumber) {
                        return;
                    }

                    if (mPref.getBoolean(mContext.getString(R.string.only_offline_key),
                            false) || mOffline) {
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
                                    mCallback.onResponseFailed(onlineNumber, true);
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
                        break;
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

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.getLooper().quit();
        mCallback = null;
    }

    public interface Callback {
        void onResponseOffline(INumber number);

        void onResponse(INumber number);

        void onResponseFailed(INumber number, boolean isOnline);
    }
}