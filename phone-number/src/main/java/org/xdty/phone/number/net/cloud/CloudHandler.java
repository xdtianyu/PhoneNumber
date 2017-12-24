package org.xdty.phone.number.net.cloud;

import android.content.Context;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;
import org.xdty.phone.number.util.Utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

public final class CloudHandler implements NumberHandler<CloudNumber>, ICloudService {

    private final static String API_URL = "https://cn.xdty.org:2443/api/v1/";
    //private final static String API_URL = "http://192.168.9.65/api/v1/";
    private final static String HMAC_SHA1 = "HmacSHA1";

    @Inject CloudService mCloudService;
    @Inject Context mContext;

    public CloudHandler() {
        App.getAppComponent().inject(this);
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private static String hmac(String data, String key) {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(signingKey);
            return toHexString(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String url() {
        return API_URL;
    }

    private String token() {
        return "vHc7RS5f66uIeUu5z95B";
    }

    @Override
    public String key() {
        return "hPMOqSPMu4";
    }

    @Override
    public CloudNumber find(String number) {
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CLOUD;
    }

    @Override
    public boolean put(CloudNumber number) {
        String data = Utils.gson().toJson(number);
        String auth = key() + ":" + hmac(data, token());
        try {
            CloudStatus status = mCloudService.put(number, auth).execute().body();
            return status.getStatus().equals("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CloudNumber get(String number) {
        String auth = key() + ":" + hmac("", token());

        try {
            return mCloudService.get(number, auth).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean patch(CloudNumber cloudNumber) {
        String eTag = cloudNumber.getEtag();
        cloudNumber.setEtag(null);
        String data = Utils.gson().toJson(cloudNumber);
        String auth = key() + ":" + hmac(data, token());
        try {
            CloudStatus status = mCloudService.patch(cloudNumber.getId(), cloudNumber, auth, eTag)
                    .execute()
                    .body();
            return status.getStatus().equals("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(CloudNumber number) {
        String auth = key() + ":" + hmac("", token());
        try {
            int code = mCloudService.delete(number.getId(), auth, number.getEtag())
                    .execute()
                    .code();
            return code == 204;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<CloudNumber> getAll(String uid) {
        try {
            return mCloudService.getAll(uid, key() + ":" + hmac("", token()))
                    .execute()
                    .body()
                    .getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
