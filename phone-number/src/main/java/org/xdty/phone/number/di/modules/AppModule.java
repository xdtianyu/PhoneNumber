package org.xdty.phone.number.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;

import org.xdty.phone.number.util.Database;
import org.xdty.phone.number.util.OkHttp;
import org.xdty.phone.number.util.Settings;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class AppModule {

    private final static String HANDLER_THREAD_NAME = "org.xdty.phone.number";

    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mContext;
    }

    @Singleton
    @Provides
    OkHttpClient provideOKHttpClient(OkHttp okHttp) {
        return okHttp.client();
    }

    @Singleton
    @Provides
    OkHttp provideOKHttp() {
        return OkHttp.get();
    }

    @Singleton
    @Provides
    Settings provideSettings() {
        return Settings.getInstance();
    }

    @Singleton
    @Provides
    SharedPreferences providePreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Singleton
    @Provides
    @Named("main")
    Handler provideMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Singleton
    @Provides
    @Named("worker")
    Handler provideWorkerHandler() {
        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    @Singleton
    @Provides
    Database provideDatabase() {
        return Database.getInstance();
    }
}
