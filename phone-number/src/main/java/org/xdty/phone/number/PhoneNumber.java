package org.xdty.phone.number;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.model.caller.CallerHandler;
import org.xdty.phone.number.model.caller.CallerNumber;
import org.xdty.phone.number.model.caller.Status;
import org.xdty.phone.number.model.cloud.CloudNumber;
import org.xdty.phone.number.model.cloud.CloudService;
import org.xdty.phone.number.model.common.CommonHandler;
import org.xdty.phone.number.model.custom.CustomNumberHandler;
import org.xdty.phone.number.model.google.GoogleNumberHandler;
import org.xdty.phone.number.model.juhe.JuHeNumberHandler;
import org.xdty.phone.number.model.leancloud.LeanCloudHandler;
import org.xdty.phone.number.model.marked.MarkedHandler;
import org.xdty.phone.number.model.mvno.MvnoHandler;
import org.xdty.phone.number.model.offline.OfflineHandler;
import org.xdty.phone.number.model.soguo.SogouNumberHandler;
import org.xdty.phone.number.model.special.SpecialNumber;
import org.xdty.phone.number.model.special.SpecialNumberHandler;
import org.xdty.phone.number.model.web.WebFactory;
import org.xdty.phone.number.model.web.WebNumberHandler;
import org.xdty.phone.number.util.OkHttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;

// TODO: reconstruction

public class PhoneNumber {
    public final static String API_TYPE = "api_type";
    private static final String TAG = PhoneNumber.class.getSimpleName();
    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";
    private static Context sContext;
    private final Object lockObject = new Object();
    private final Object networkLockObject = new Object();
    private Callback mCallback;
    private Handler mMainHandler;
    private Handler mHandler;
    private SharedPreferences mPref;
    private List<NumberHandler> mSupportHandlerList;
    private boolean mOffline = false;
    private CloudService mCloudService;
    private List<Callback> mCallbackList;
    private List<CloudListener> mCloudListeners;
    private CheckUpdateCallback mCheckUpdateCallback;

    private PhoneNumber() {
        this(sContext);
    }

    public PhoneNumber(Context context) {
        this(context, false, null);
    }

    public PhoneNumber(Context context, Callback callback) {
        this(context, false, callback);
    }

    public PhoneNumber(Context context, boolean offline, Callback callback) {
        if (sContext == null) {
            sContext = context.getApplicationContext();
        }

        mOffline = offline;
        mPref = PreferenceManager.getDefaultSharedPreferences(sContext);
        mCallback = callback;
        mMainHandler = new Handler(sContext.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OkHttpClient mOkHttpClient = OkHttp.get().client();

                synchronized (lockObject) {
                    synchronized (networkLockObject) {
                        addNumberHandler(new SpecialNumberHandler(sContext));
                        addNumberHandler(new CommonHandler(sContext));
                        addNumberHandler(new CallerHandler(sContext, mOkHttpClient));
                        addNumberHandler(new MarkedHandler(sContext));
                        addNumberHandler(new OfflineHandler(sContext));
                        addNumberHandler(new MvnoHandler(sContext));
                        addNumberHandler(new GoogleNumberHandler(sContext));

                        addNumberHandler(new CustomNumberHandler(sContext, mOkHttpClient));
                        addNumberHandler(new WebNumberHandler(WebFactory.SEARCH_360));
                        addNumberHandler(new WebNumberHandler(WebFactory.SEARCH_BAIDU));
                        // remove Baidu api because it's dead.
                        //addNumberHandler(new BDNumberHandler(sContext, mOkHttpClient));
                        addNumberHandler(new JuHeNumberHandler(sContext, mOkHttpClient));
                        addNumberHandler(new SogouNumberHandler(sContext, mOkHttpClient));
                        addNumberHandler(new LeanCloudHandler(sContext, mOkHttpClient));
                        mCloudService = new LeanCloudHandler(sContext, mOkHttpClient);
                    }
                }
            }
        });
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static PhoneNumber getInstance() {
        if (sContext == null) {
            throw new IllegalStateException("init(Context) has not been called yet.");
        }
        return SingletonHelper.INSTANCE;
    }

    @Deprecated
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
                            if (offlineNumber != null && offlineNumber.isValid()) {
                                onResponseOffline(offlineNumber);
                            } else {
                                onResponseFailed(offlineNumber, false);
                            }
                        }
                    });

                    if (offlineNumber instanceof SpecialNumber
                            || offlineNumber instanceof CallerNumber) {
                        return;
                    }

                    if (mPref.getBoolean(sContext.getString(R.string.only_offline_key),
                            false) || mOffline) {
                        return;
                    }

                    final INumber onlineNumber = getNumber(number);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onlineNumber != null && onlineNumber.isValid()) {
                                onResponse(onlineNumber);
                            } else {
                                onResponseFailed(onlineNumber, true);
                            }
                        }
                    });
                }
            });
        }
    }

    public INumber getNumber(String number) {
        synchronized (networkLockObject) {
            int apiType = mPref.getInt(API_TYPE, INumber.API_ID_JH);

            INumber result = null;

            for (NumberHandler handler : mSupportHandlerList) {
                if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CUSTOM) {
                    INumber i = handler.find(number);
                    if (i != null && i.isValid()) {
                        result = i;
                    }
                    break;
                }
            }

            if (result == null || !result.isValid()) {
                for (NumberHandler handler : mSupportHandlerList) {
                    if (handler.isOnline() && handler.getApiId() == apiType) {
                        INumber i = handler.find(number);
                        if (i != null && i.isValid()) {
                            result = i;
                        }
                        break;
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
    }

    public INumber getOfflineNumber(String number) {
        synchronized (lockObject) {
            INumber iNumber = null;
            for (NumberHandler handler : mSupportHandlerList) {
                if (!handler.isOnline() || handler.getApiId() == INumber.API_ID_CALLER) {
                    INumber i = handler.find(number);
                    if (i != null && i.isValid()) {
                        if (i.hasGeo()) {
                            if (iNumber == null) { // return result
                                return i;
                            } else { // patch geo info to previous result
                                iNumber.patch(i);
                                return iNumber;
                            }
                        } else { // continue for geo info
                            iNumber = i;
                        }
                    }
                }
            }
            return iNumber;
        }
    }

    void onResponseOffline(INumber number) {
        if (mCallback != null) {
            mCallback.onResponseOffline(number);
        }

        if (mCallbackList != null) {
            final List<Callback> list = mCallbackList;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onResponseOffline(number);
            }
        }
    }

    void onResponse(INumber number) {
        if (mCallback != null) {
            mCallback.onResponse(number);
        }

        if (mCallbackList != null) {
            final List<Callback> list = mCallbackList;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onResponse(number);
            }
        }
    }

    void onResponseFailed(INumber number, boolean isOnline) {
        if (mCallback != null) {
            mCallback.onResponseFailed(number, isOnline);
        }

        if (mCallbackList != null) {
            final List<Callback> list = mCallbackList;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onResponseFailed(number, isOnline);
            }
        }
    }

    void onPutResult(CloudNumber number, boolean result) {

        if (mCloudListeners != null) {
            final List<CloudListener> list = mCloudListeners;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onPutResult(number, result);
            }
        }
    }

    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.getLooper().quit();
        mCallback = null;
    }

    public void put(final CloudNumber cloudNumber) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final boolean result = mCloudService.put(cloudNumber);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPutResult(cloudNumber, result);
                    }
                });
            }
        });
    }

    public void checkUpdate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (NumberHandler handler : mSupportHandlerList) {
                    if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                        CallerHandler callerHandler = (CallerHandler) handler;
                        final Status status = callerHandler.checkUpdate();
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onCheckResult(status);
                            }
                        });
                    }
                }
            }
        });
    }

    private void onCheckResult(Status status) {
        if (mCheckUpdateCallback != null) {
            mCheckUpdateCallback.onCheckResult(status);
        }
    }

    public void upgradeData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (NumberHandler handler : mSupportHandlerList) {
                    if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                        CallerHandler callerHandler = (CallerHandler) handler;
                        final boolean result = callerHandler.upgradeData();
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onUpgradeData(result);
                            }
                        });
                    }
                }
            }
        });
    }

    void onUpgradeData(boolean result) {
        if (mCheckUpdateCallback != null) {
            mCheckUpdateCallback.onUpgradeData(result);
        }
    }

    public void addCallback(Callback callback) {
        if (mCallbackList == null) {
            mCallbackList = new ArrayList<>();
        }
        mCallbackList.add(callback);
    }

    public void removeCallback(Callback callback) {
        if (mCallbackList != null) {
            int i = mCallbackList.indexOf(callback);
            if (i >= 0) {
                mCallbackList.remove(i);
            }
        }
    }

    public void addCloudListener(CloudListener listener) {
        if (mCloudListeners == null) {
            mCloudListeners = new ArrayList<>();
        }
        mCloudListeners.add(listener);
    }

    public void removeCloudListener(CloudListener listener) {
        if (mCloudListeners != null) {
            int i = mCloudListeners.indexOf(listener);
            if (i >= 0) {
                mCloudListeners.remove(i);
            }
        }
    }

    public void setCheckUpdateCallback(CheckUpdateCallback callback) {
        mCheckUpdateCallback = callback;
    }

    public interface Callback {
        void onResponseOffline(INumber number);

        void onResponse(INumber number);

        void onResponseFailed(INumber number, boolean isOnline);
    }

    public interface CloudListener {
        void onPutResult(CloudNumber number, boolean result);
    }

    public interface CheckUpdateCallback {
        void onCheckResult(Status status);

        void onUpgradeData(boolean result);
    }

    private static class SingletonHelper {
        private final static PhoneNumber INSTANCE = new PhoneNumber();
    }
}