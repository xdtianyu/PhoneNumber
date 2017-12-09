package org.xdty.phone.number;

import android.content.Context;
import android.os.Handler;

import org.xdty.phone.number.local.google.GoogleNumberHandler;
import org.xdty.phone.number.local.mvno.MvnoHandler;
import org.xdty.phone.number.local.special.SpecialNumber;
import org.xdty.phone.number.local.special.SpecialNumberHandler;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.net.caller.CallerHandler;
import org.xdty.phone.number.net.caller.CallerNumber;
import org.xdty.phone.number.net.caller.Status;
import org.xdty.phone.number.net.cloud.CloudHandler;
import org.xdty.phone.number.net.cloud.CloudNumber;
import org.xdty.phone.number.net.cloud.ICloudService;
import org.xdty.phone.number.net.custom.CustomNumberHandler;
import org.xdty.phone.number.net.juhe.JuHeNumberHandler;
import org.xdty.phone.number.net.leancloud.LeanCloudHandler;
import org.xdty.phone.number.net.soguo.SogouNumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Database;
import org.xdty.phone.number.util.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

// TODO: reconstruction

public class PhoneNumber {

    private static final String TAG = PhoneNumber.class.getSimpleName();

    private final Object lockObject = new Object();
    private final Object networkLockObject = new Object();

    @Inject
    Settings mSettings;

    @Inject @Named("main")
    Handler mMainHandler;

    @Inject @Named("worker")
    Handler mHandler;

    @Inject
    Database mDatabase;

    private Callback mCallback;

    private List<NumberHandler> mSupportHandlerList;
    private boolean mOffline = false;
    private ICloudService mCloudService;
    private List<Callback> mCallbackList;
    private List<CloudListener> mCloudListeners;
    private CheckUpdateCallback mCheckUpdateCallback;

    public PhoneNumber() {
        this(false, null);
    }

    public PhoneNumber(Callback callback) {
        this(false, callback);
    }

    public PhoneNumber(boolean offline, Callback callback) {
        mOffline = offline;
        mCallback = callback;
    }

    public static PhoneNumber getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void init(Context context) {
        App.get().install(context.getApplicationContext());

        App.getAppComponent().inject(this);

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                synchronized (lockObject) {
                    synchronized (networkLockObject) {

                        addNumberHandler(new SpecialNumberHandler());
                        addNumberHandler(new CallerHandler());
                        addNumberHandler(new MvnoHandler());
                        addNumberHandler(new GoogleNumberHandler());

                        addNumberHandler(new CustomNumberHandler());
                        addNumberHandler(new JuHeNumberHandler());
                        addNumberHandler(new SogouNumberHandler());
                        addNumberHandler(new LeanCloudHandler());
                        mCloudService = new CloudHandler();
                    }
                }
            }
        });
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
            fetch(number);
        }
    }

    public void fetch(final String number) {
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

                if (mSettings.isOnlyOffline() || mOffline) {
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

    public INumber getNumber(String number) {
        synchronized (networkLockObject) {
            int apiType = mSettings.getApiType();

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

    public void get(final String number) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CloudNumber cloudNumber = mCloudService.get(number);
            }
        });
    }

    public void getAll(final String uid) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                List<CloudNumber> cloudNumbers = mCloudService.getAll(uid);
            }
        });
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

    public void patch(final CloudNumber cloudNumber) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCloudService.patch(cloudNumber);
            }
        });
    }

    public void delete(final CloudNumber cloudNumber) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCloudService.delete(cloudNumber);
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