package org.xdty.phone.number.model.caller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.okhttp.OkHttpClient;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;

import java.io.File;

public class CallerHandler implements NumberHandler<CallerNumber> {

    public final static String DB_NAME = "caller.db";

    private transient Context mContext;
    private transient OkHttpClient mOkHttpClient;

    public CallerHandler(Context context, OkHttpClient okHttpClient) {
        mContext = context;
        mOkHttpClient = okHttpClient;
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
                    new String[] { number, "0" + number });

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
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CALLER;
    }
}
