package org.xdty.phone.number.net.leancloud;

// https://leancloud.cn/data.html?appid=WL1lx9d2KiVXy2vfyd2yIepE-gzGzoHsz#/caller
// https://leancloud.cn/docs/rest_api.html#基础查询
// https://leancloud.cn/apionline/#!/classes/创建或更新对象_post_0

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.net.cloud.CloudNumber;
import org.xdty.phone.number.net.cloud.ICloudService;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Utils;

import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LeanCloudHandler implements NumberHandler<CloudNumber>, ICloudService {
    public transient final static String META_DATA_APP_KEY_URI =
            "org.xdty.phone.number.LEANCLOUD_APP_KEY";
    public transient final static String META_DATA_APP_ID_URI =
            "org.xdty.phone.number.LEANCLOUD_APP_ID";
    public transient final static String APP_KEY = "leancloud_app_key";
    public transient final static String APP_ID = "leancloud_app_id";
    private static final String TAG = LeanCloudHandler.class.getSimpleName();
    private final static String API_URL = "https://leancloud.cn/1.1/classes/";
    public final MediaType JSON;

    @Inject OkHttpClient mOkHttpClient;
    @Inject Context mContext;

    public LeanCloudHandler() {
        App.getAppComponent().inject(this);
        JSON = MediaType.parse("application/json; charset=utf-8");
    }

    private String appId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = pref.getString(APP_ID, "");
        if (apiKey.isEmpty()) {
            apiKey = Utils.get().getMetadata(mContext, META_DATA_APP_ID_URI);
        }
        return apiKey;
    }

    private String appKey() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = pref.getString(APP_KEY, "");
        if (apiKey.isEmpty()) {
            apiKey = Utils.get().getMetadata(mContext, META_DATA_APP_KEY_URI);
        }
        return apiKey;
    }

    @Override
    public boolean put(CloudNumber number) {
        String url = API_URL + "caller";
        RequestBody body = RequestBody.create(JSON, Utils.gson().toJson(number));
        Request.Builder request = new Request.Builder()
                .url(url)
                .addHeader("X-LC-Id", appId())
                .addHeader("X-LC-Key", appKey())
                .post(body);
        try {
            Response response = mOkHttpClient.newCall(request.build()).execute();
            return response.code() == 201;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CloudNumber get(String number) {
        String url = API_URL
                + "caller?limit=3&keys=-uid,-objectId,-updatedAt&where={\"number\":\""
                + number
                + "\"}";
        Request.Builder request = new Request.Builder()
                .url(url)
                .addHeader("X-LC-Id", appId())
                .addHeader("X-LC-Key", appKey())
                .get();
        okhttp3.Response response = null;
        CloudNumber cloudNumber = null;
        try {
            response = mOkHttpClient.newCall(
                    request.build()).execute();
            String s = response.body().string();
            Log.e(TAG, "CloudNumber: " + s);
            LCResult result = Utils.gson().fromJson(s, LCResult.class);

            if (result != null && result.results != null && result.results.size() > 0) {
                cloudNumber = result.results.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
        return cloudNumber;
    }

    @Override
    public boolean patch(CloudNumber cloudNumber) {
        return false;
    }

    @Override
    public boolean delete(CloudNumber cloudNumber) {
        return false;
    }

    @Override
    public List<CloudNumber> getAll(String deviceId) {
        return null;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public CloudNumber find(String number) {
        return get(number);
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CLOUD;
    }
}
