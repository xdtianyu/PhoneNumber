package org.xdty.phone.number.model.cloud.leancloud;


// https://leancloud.cn/data.html?appid=WL1lx9d2KiVXy2vfyd2yIepE-gzGzoHsz#/caller
// https://leancloud.cn/docs/rest_api.html#基础查询
// https://leancloud.cn/apionline/#!/classes/创建或更新对象_post_0

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.cloud.CloudNumber;
import org.xdty.phone.number.model.cloud.CloudService;

public class LeanCloud implements CloudService {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public transient final static String META_DATA_APP_KEY_URI =
            "org.xdty.phone.number.LEANCLOUD_APP_KEY";
    public transient final static String META_DATA_APP_ID_URI =
            "org.xdty.phone.number.LEANCLOUD_APP_ID";
    public transient final static String APP_KEY = "leancloud_app_key";
    public transient final static String APP_ID = "leancloud_app_id";
    private static final String TAG = LeanCloud.class.getSimpleName();
    private final static String API_URL = "https://leancloud.cn/1.1/classes/";
    private OkHttpClient mOkHttpClient;
    private Context mContext;

    public LeanCloud(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    private String appId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = pref.getString(APP_ID, "");
        if (apiKey.isEmpty()) {
            apiKey = PhoneNumber.getMetadata(mContext, META_DATA_APP_ID_URI);
        }
        return apiKey;
    }

    private String appKey() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = pref.getString(APP_KEY, "");
        if (apiKey.isEmpty()) {
            apiKey = PhoneNumber.getMetadata(mContext, META_DATA_APP_KEY_URI);
        }
        return apiKey;
    }

    @Override
    public boolean put(CloudNumber number) {
        String url = API_URL + "caller";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(number));
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
        return true;
    }

    @Override
    public CloudNumber get(String number) {
        return null;
    }
}
