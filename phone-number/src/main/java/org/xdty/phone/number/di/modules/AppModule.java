package org.xdty.phone.number.di.modules;

import android.content.Context;

import org.xdty.phone.number.util.OkHttp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

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
    OkHttp provideOKHttp() {
        return OkHttp.get();
    }
}
