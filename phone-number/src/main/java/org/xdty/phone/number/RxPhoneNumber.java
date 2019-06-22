package org.xdty.phone.number;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

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

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class RxPhoneNumber {
    public final static String API_TYPE = "api_type";
    private static final String TAG = RxPhoneNumber.class.getSimpleName();
    private Context sContext;
    private Callback mCallback;
    private SharedPreferences mPref;
    private List<NumberHandler> mSupportHandlerList;
    private boolean mOffline = false;
    private CloudService mCloudService;
    private List<Callback> mCallbackList;
    private List<CloudListener> mCloudListeners;
    private CheckUpdateCallback mCheckUpdateCallback;

    public RxPhoneNumber() {
    }

    public RxPhoneNumber(Context context) {
        this(context, false, null);
    }

    public RxPhoneNumber(Context context, Callback callback) {
        this(context, false, callback);
    }

    public RxPhoneNumber(Context context, boolean offline, Callback callback) {

        init(context);

        mOffline = offline;
        mPref = provideSharedPreferences();
        mCallback = callback;
    }

    public void init(Context context) {
        if (sContext == null) {
            sContext = context.getApplicationContext();
        }

        mPref = provideSharedPreferences();

        OkHttpClient mOkHttpClient = OkHttp.get().client();

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

    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
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
            final INumber offlineNumber = getOfflineNumber(number);

            if (offlineNumber != null && offlineNumber.isValid()) {
                onResponseOffline(offlineNumber);
            } else {
                onResponseFailed(offlineNumber, false);
            }

            if (offlineNumber instanceof SpecialNumber
                    || offlineNumber instanceof CallerNumber) {
                return;
            }

            if (mPref.getBoolean(sContext.getString(R.string.only_offline_key),
                    false) || mOffline) {
                return;
            }

            final INumber onlineNumber = null;
            //final INumber onlineNumber = getNumber(number)
            //        .parallel(10)
            //        .runOn(Schedulers.io())
            //        .flatMap(new Function<INumber, Publisher<?>>() {
            //            @Override
            //            public Publisher<?> apply(INumber iNumber) throws Exception {
            //                return null;
            //            }
            //        }).sequential().subscribe();

            if (onlineNumber != null && onlineNumber.isValid()) {
                onResponse(onlineNumber);
            } else {
                onResponseFailed(onlineNumber, true);
            }
        }
    }

    public Flowable<INumber> getNumber(final String number) {
        return Flowable.fromIterable(mSupportHandlerList)
                .parallel(16)
                .runOn(Schedulers.io())
                .map(new Function<NumberHandler, INumber>() {
                    @Override
                    public INumber apply(NumberHandler numberHandler) throws Exception {
                        Log.e(TAG, "apply: " + numberHandler);
                        try {
                            return numberHandler.find(number);
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }, ParallelFailureHandling.SKIP)
                .sequential()
                .observeOn(AndroidSchedulers.mainThread());
    }

    public INumber getOfflineNumber(String number) {
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

    public void put(final CloudNumber cloudNumber) {
        final boolean result = mCloudService.put(cloudNumber);
        onPutResult(cloudNumber, result);
    }

    public void checkUpdate() {
        for (NumberHandler handler : mSupportHandlerList) {
            if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                CallerHandler callerHandler = (CallerHandler) handler;
                final Status status = callerHandler.checkUpdate();
                onCheckResult(status);
            }
        }
    }

    private void onCheckResult(Status status) {
        if (mCheckUpdateCallback != null) {
            mCheckUpdateCallback.onCheckResult(status);
        }
    }

    public void upgradeData() {
        for (NumberHandler handler : mSupportHandlerList) {
            if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                CallerHandler callerHandler = (CallerHandler) handler;
                final boolean result = callerHandler.upgradeData();
                onUpgradeData(result);
            }
        }
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
}