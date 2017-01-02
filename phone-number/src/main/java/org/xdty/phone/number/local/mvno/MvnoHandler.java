package org.xdty.phone.number.local.mvno;

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

public class MvnoHandler implements NumberHandler<MvnoNumber> {

    public final static String MVNP_VERSION_CODE_KEY = "mvnp_db_version_code_key";
    public final static int MVNP_VERSION_CODE = 1;
    public final static String DB_NAME = "mvnp.db";

    private Context mContext;

    public MvnoHandler(Context context) {
        mContext = context.getApplicationContext();
        checkVersion();
    }

    private void checkVersion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (prefs.getInt(MVNP_VERSION_CODE_KEY, 0) < MVNP_VERSION_CODE) {
            Utils.get().removeCacheFile(mContext, DB_NAME);
        }
        prefs.edit().putInt(MVNP_VERSION_CODE_KEY, MVNP_VERSION_CODE).apply();
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
    public MvnoNumber find(String number) {
        number = Utils.get().fixNumberPlus(number);

        if (number.length() != 11 || (!number.startsWith("170") && !number.startsWith("171"))) {
            return null;
        }

        MvnoNumber mvnoNumber = null;

        Cursor cur = null;
        SQLiteDatabase db = null;
        try {
            File dbFile = Utils.get().createCacheFile(mContext, DB_NAME, R.raw.mvno);

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            cur = db.rawQuery(
                    "SELECT number.number, province.province, city.city, provider.provider "
                            + "FROM number "
                            + "JOIN province "
                            + "ON province.province_id = number.province "
                            + "JOIN city "
                            + "ON city.city_id = number.city "
                            + "JOIN provider "
                            + "ON provider.provider_id = number.provider "
                            + "WHERE number.number = ?",
                    new String[] { number.substring(2, 7) });

            if (cur.getCount() == 1 && cur.moveToFirst()) {
                String province = cur.getString(cur.getColumnIndex("province"));
                String city = cur.getString(cur.getColumnIndex("city"));
                String provider = cur.getString(cur.getColumnIndex("provider"));
                mvnoNumber = new MvnoNumber(number, province, city, provider);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cur != null) {
                    cur.close();
                }
                if (db != null) {
                    db.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return mvnoNumber;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_MVNP;
    }
}
