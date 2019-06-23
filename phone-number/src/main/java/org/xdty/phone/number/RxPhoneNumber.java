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
import org.xdty.phone.number.model.caller.Status;
import org.xdty.phone.number.model.cloud.CloudNumber;
import org.xdty.phone.number.model.cloud.CloudService;
import org.xdty.phone.number.model.leancloud.LeanCloudHandler;
import org.xdty.phone.number.util.OkHttp;

import java.util.Collection;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class RxPhoneNumber {
    public final static String API_TYPE = "api_type";
    private static final String TAG = RxPhoneNumber.class.getSimpleName();
    private Context mContext;
    private SharedPreferences mPref;
    private CloudService mCloudService;

    public RxPhoneNumber() {
    }

    public RxPhoneNumber(Context context) {

        init(context);

        mPref = provideSharedPreferences();
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }

        mPref = provideSharedPreferences();

        NumberProvider.init(mContext);

        OkHttpClient mOkHttpClient = OkHttp.get().client();

        mCloudService = new LeanCloudHandler(mContext, mOkHttpClient);
    }

    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
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

    public Flowable<INumber> getNumber(final String number) {
        return getNumber(number, NumberProvider.providers());
    }

    public Flowable<INumber> getOfflineNumber(final String number) {
        return getNumber(number, NumberProvider.providers(false));
    }

    private Flowable<INumber> getNumber(final String number, Collection<NumberHandler> providers) {
        return Flowable.fromIterable(providers)
                .parallel(providers.size())
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

    public Single<Boolean> put(final CloudNumber cloudNumber) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mCloudService.put(cloudNumber);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Status> checkUpdate() {
        return Single.fromCallable(new Callable<Status>() {
            @Override
            public Status call() throws Exception {
                for (NumberHandler handler : NumberProvider.providers()) {
                    if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                        CallerHandler callerHandler = (CallerHandler) handler;
                        return callerHandler.checkUpdate();
                    }
                }
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> upgradeData() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (NumberHandler handler : NumberProvider.providers()) {
                    if (handler.isOnline() && handler.getApiId() == INumber.API_ID_CALLER) {
                        CallerHandler callerHandler = (CallerHandler) handler;
                        return callerHandler.upgradeData();
                    }
                }
                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}