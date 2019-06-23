package org.xdty.phone.number;

import android.annotation.SuppressLint;
import android.content.Context;

import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.model.caller.CallerHandler;
import org.xdty.phone.number.model.common.CommonHandler;
import org.xdty.phone.number.model.custom.CustomNumberHandler;
import org.xdty.phone.number.model.google.GoogleNumberHandler;
import org.xdty.phone.number.model.juhe.JuHeNumberHandler;
import org.xdty.phone.number.model.leancloud.LeanCloudHandler;
import org.xdty.phone.number.model.marked.MarkedHandler;
import org.xdty.phone.number.model.mvno.MvnoHandler;
import org.xdty.phone.number.model.offline.OfflineHandler;
import org.xdty.phone.number.model.soguo.SogouNumberHandler;
import org.xdty.phone.number.model.special.SpecialNumberHandler;
import org.xdty.phone.number.model.web.WebFactory;
import org.xdty.phone.number.model.web.WebNumberHandler;
import org.xdty.phone.number.util.OkHttp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

@SuppressLint("UseSparseArrays")
public class NumberProvider {
    private final static Map<Integer, NumberHandler> ONLINE_PROVIDERS = new HashMap<>();
    private final static Map<Integer, NumberHandler> OFFLINE_PROVIDERS = new HashMap<>();
    private final static Map<Integer, NumberHandler> PROVIDERS = new HashMap<>();

    public static void init(Context context) {

        OkHttpClient okHttpClient = OkHttp.get().client();

        registerOffline(new SpecialNumberHandler(context));
        registerOffline(new CommonHandler(context));
        registerOffline(new CallerHandler(context, okHttpClient));
        registerOffline(new MarkedHandler(context));
        registerOffline(new OfflineHandler(context));
        registerOffline(new MvnoHandler(context));
        registerOffline(new GoogleNumberHandler(context));

        register(new CustomNumberHandler(context, okHttpClient));
        register(new WebNumberHandler(WebFactory.SEARCH_360));
        register(new WebNumberHandler(WebFactory.SEARCH_BAIDU));
        // remove Baidu api because it's dead.
        //register(new BDNumberHandler(mContext, mOkHttpClient));
        register(new JuHeNumberHandler(context, okHttpClient));
        register(new SogouNumberHandler(context, okHttpClient));
        register(new LeanCloudHandler(context, okHttpClient));

        PROVIDERS.putAll(OFFLINE_PROVIDERS);
        PROVIDERS.putAll(ONLINE_PROVIDERS);
    }

    public static void register(NumberHandler handler) {
        register(ONLINE_PROVIDERS, handler);
    }

    public static void registerOffline(NumberHandler handler) {
        register(OFFLINE_PROVIDERS, handler);
    }

    private static void register(Map<Integer, NumberHandler> providers, NumberHandler handler) {
        providers.put(handler.getApiId(), handler);
    }

    public static void clear() {
        ONLINE_PROVIDERS.clear();
        OFFLINE_PROVIDERS.clear();
        PROVIDERS.clear();
    }

    public static Collection<NumberHandler> providers() {
        return PROVIDERS.values();
    }

    public static Collection<NumberHandler> providers(boolean isOnline) {
        if (isOnline) {
            return ONLINE_PROVIDERS.values();
        } else {
            return OFFLINE_PROVIDERS.values();
        }
    }

    public static int size() {
        return PROVIDERS.size();
    }

    public static int size(boolean isOnline) {
        if (isOnline) {
            return ONLINE_PROVIDERS.size();
        } else {
            return OFFLINE_PROVIDERS.size();
        }
    }
}
