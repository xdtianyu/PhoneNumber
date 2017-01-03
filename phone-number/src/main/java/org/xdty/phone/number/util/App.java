package org.xdty.phone.number.util;

import android.annotation.SuppressLint;
import android.content.Context;

import org.xdty.phone.number.di.AppComponent;
import org.xdty.phone.number.di.DaggerAppComponent;
import org.xdty.phone.number.di.modules.AppModule;

public final class App {

    private static AppComponent sAppComponent;
    private Context mContext;

    public static App get() {
        return SingletonHelper.INSTANCE;
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    public void app(Context context) {
        mContext = context;
        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(context)).build();
    }

    public Context app() {
        if (mContext == null) {
            throw new IllegalStateException("App context is not set.");
        }
        return mContext;
    }

    private final static class SingletonHelper {
        @SuppressLint("StaticFieldLeak")
        private final static App INSTANCE = new App();
    }

}
