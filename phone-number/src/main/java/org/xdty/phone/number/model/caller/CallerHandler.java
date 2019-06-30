package org.xdty.phone.number.model.caller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class CallerHandler implements NumberHandler<CallerNumber> {

    private final static String DB_NAME = "caller.db";
    private static final String TAG = CallerHandler.class.getSimpleName();
    private final static String DEFAULT_DOWNLOAD_URL = "https://cn.xdty.org/";
    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;
    private transient Status mStatus = null;

    public CallerHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
    }

    @Override
    public String url() {
        return DEFAULT_DOWNLOAD_URL;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public CallerNumber find(String number) {
        number = number.replaceAll("\\+86", "");
        if (number.contains("+")) {
            return null;
        }

        CallerNumber callerNumber = null;
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            File dbFile = new File(mContext.getCacheDir(), DB_NAME);

            if (!dbFile.exists()) {
                return null;
            }

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            cur = db.rawQuery("SELECT * FROM caller WHERE number = ? OR number = ? ",
                    new String[] { number });
            if (cur.getCount() == 0 && !number.startsWith("+") && !number.startsWith("0")) {
                cur.close();
                cur = db.rawQuery("SELECT * FROM caller WHERE number = ? OR number = ? ",
                        new String[] { "0" + number });
            }

            if (cur.getCount() >= 1 && cur.moveToFirst()) {
                int type = cur.getInt(cur.getColumnIndex("type"));
                int source = cur.getInt(cur.getColumnIndex("source"));
                int count = cur.getInt(cur.getColumnIndex("count"));
                long time = cur.getLong(cur.getColumnIndex("time"));
                String name = cur.getString(cur.getColumnIndex("name"));
                callerNumber = new CallerNumber(number);
                callerNumber.setType(type);
                callerNumber.setName(name);
                callerNumber.setSource(source);
                callerNumber.setTime(time);
                callerNumber.setCount(count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
                if (cur != null) {
                    cur.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return callerNumber;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CALLER;
    }

    public boolean upgradeData() {

        if (mStatus == null) {
            return false;
        }

        String url = mStatus.url;
        if (!TextUtils.isEmpty(url)) {
            String filename = "caller_" + mStatus.version + ".db.zip";
            url = url + filename;
            Request.Builder request = new Request.Builder().url(url);
            try {
                Response response = mOkHttpClient.newCall(
                        request.build()).execute();
                File downloadedFile = new File(mContext.getCacheDir(), filename);
                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                sink.writeAll(response.body().source());
                sink.close();
                response.body().close();

                // check md5
                if (!Utils.checkMD5(mStatus.md5, downloadedFile)) {
                    Log.e(TAG, "Offline file md5 not match!");
                    return false;
                }

                Utils.unzip(downloadedFile.getAbsolutePath(),
                        mContext.getCacheDir().getAbsolutePath());
                if (!downloadedFile.delete()) {
                    Log.e(TAG, "downloaded file delete failed.");
                }
                File db_new = new File(mContext.getCacheDir(), "caller_" + mStatus.version + ".db");
                File db = new File(mContext.getCacheDir(), DB_NAME);
                if (db_new.exists() && db_new.renameTo(db)) {
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Status checkUpdate() {
        String url = url();
        Status status = null;
        String s = null;
        if (!TextUtils.isEmpty(url)) {
            url = url + "status_2.json?timestamp=" + System.currentTimeMillis();
            Request.Builder request = new Request.Builder().url(url);
            try {
                Response response = mOkHttpClient.newCall(
                        request.build()).execute();
                s = response.body().string();
                status = Utils.gson().fromJson(s, Status.class);
                Status dbStatus = getDBStatus();
                if (dbStatus != null && status != null && dbStatus.version >= status.version) {
                    status = new Status();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "checkUpdate: " + s);
                status = null;
            }
        }
        mStatus = status;
        return status;
    }

    private Status getDBStatus() {
        Status status = null;
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            File dbFile = new File(mContext.getCacheDir(), DB_NAME);

            if (!dbFile.exists()) {
                return null;
            }

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            cur = db.rawQuery("SELECT * FROM status where id = ?", new String[] { "1" });

            if (cur.getCount() >= 1 && cur.moveToFirst()) {
                status = new Status();
                status.count = cur.getInt(cur.getColumnIndex("count"));
                status.new_count = cur.getInt(cur.getColumnIndex("new_count"));
                status.timestamp = cur.getLong(cur.getColumnIndex("time"));
                status.version = cur.getInt(cur.getColumnIndex("version"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
                if (cur != null) {
                    cur.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return status;
    }
}
