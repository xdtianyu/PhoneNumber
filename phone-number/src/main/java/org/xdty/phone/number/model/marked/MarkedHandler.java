package org.xdty.phone.number.model.marked;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.File;

public class MarkedHandler implements NumberHandler<MarkedNumber> {

    public final static String MARKED_VERSION_CODE_KEY = "marked_db_version_code_key";
    public final static int MARKED_VERSION_CODE = 1;
    public final static String DB_NAME = "marked.db";

    private Context mContext;

    public MarkedHandler(Context context) {
        mContext = context.getApplicationContext();
        checkVersion();
    }

    private void checkVersion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (prefs.getInt(MARKED_VERSION_CODE_KEY, 0) < MARKED_VERSION_CODE) {
            Utils.removeCacheFile(mContext, DB_NAME);
        }
        prefs.edit().putInt(MARKED_VERSION_CODE_KEY, MARKED_VERSION_CODE).apply();
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
    public MarkedNumber find(String number) {
        number = Utils.fixNumberPlus(number);

        MarkedNumber markedNumber = null;
        SQLiteDatabase db = null;
        Cursor cur = null;
        try {
            File dbFile = Utils.createCacheFile(mContext, DB_NAME, R.raw.marked);

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            cur = db.rawQuery("SELECT * FROM phone_number WHERE number = ? OR number = ? ",
                    new String[] { number });

            if (cur.getCount() == 0 && !number.startsWith("+") && !number.startsWith("0")) {
                cur.close();
                cur = db.rawQuery("SELECT * FROM phone_number WHERE number = ? OR number = ? ",
                        new String[] { "0" + number });
            }

            if (cur.getCount() >= 1 && cur.moveToFirst()) {
                int type = cur.getInt(cur.getColumnIndex("type"));
                markedNumber = new MarkedNumber(number, type);
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

        return markedNumber;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_MARKED;
    }
}
