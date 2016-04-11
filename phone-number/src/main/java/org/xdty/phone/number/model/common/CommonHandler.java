package org.xdty.phone.number.model.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.File;

public class CommonHandler implements NumberHandler<CommonNumber> {

    private Context mContext;

    public CommonHandler(Context context) {
        mContext = context;
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
    public CommonNumber find(String number) {
        number = number.replaceAll("\\+86", "");
        if (number.contains("+")) {
            return null;
        }

        CommonNumber commonNumber = null;

        Cursor cur = null;
        SQLiteDatabase db = null;
        try {
            File dbFile = Utils.createCacheFile(mContext, "common.db", R.raw.common);

            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            cur = db.rawQuery("SELECT * FROM phone_number WHERE number = ? OR number = ? ",
                    new String[]{number, "0" + number});

            if (cur.getCount() == 1 && cur.moveToFirst()) {
                String name = cur.getString(cur.getColumnIndex("name"));
                commonNumber = new CommonNumber(number, name);
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

        return commonNumber;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_COMMON;
    }
}
