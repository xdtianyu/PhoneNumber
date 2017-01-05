package org.xdty.phone.number.di;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.di.modules.AppModule;
import org.xdty.phone.number.local.common.CommonHandler;
import org.xdty.phone.number.local.google.GoogleNumberHandler;
import org.xdty.phone.number.local.marked.MarkedHandler;
import org.xdty.phone.number.local.mvno.MvnoHandler;
import org.xdty.phone.number.local.offline.OfflineHandler;
import org.xdty.phone.number.local.special.SpecialNumberHandler;
import org.xdty.phone.number.net.caller.CallerHandler;
import org.xdty.phone.number.net.custom.CustomNumberHandler;
import org.xdty.phone.number.net.juhe.JuHeNumberHandler;
import org.xdty.phone.number.net.leancloud.LeanCloudHandler;
import org.xdty.phone.number.net.soguo.SogouNumberHandler;
import org.xdty.phone.number.util.Settings;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(SpecialNumberHandler specialNumberHandler);

    void inject(CommonHandler commonHandler);

    void inject(CallerHandler callerHandler);

    void inject(MarkedHandler markedHandler);

    void inject(OfflineHandler offlineHandler);

    void inject(MvnoHandler mvnoHandler);

    void inject(GoogleNumberHandler googleNumberHandler);

    void inject(CustomNumberHandler customNumberHandler);

    void inject(JuHeNumberHandler juHeNumberHandler);

    void inject(LeanCloudHandler leanCloudHandler);

    void inject(SogouNumberHandler sogouNumberHandler);

    void inject(PhoneNumber phoneNumber);

    void inject(Settings settings);
}
