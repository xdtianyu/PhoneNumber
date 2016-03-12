package org.xdty.phone.number.model.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.File;
import java.io.IOException;

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

        try {
            File dbFile = Utils.createCacheFile(mContext, "common.db", R.raw.marked);

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            Cursor cur = db.rawQuery("SELECT * FROM phone_number WHERE number = ? OR number = ? ",
                    new String[]{number, "0" + number});

            if (cur.getCount() == 1 && cur.moveToFirst()) {
                String name = cur.getString(cur.getColumnIndex("name"));
                commonNumber = new CommonNumber(number, name);
            }
            cur.close();

        } catch (IOException e) {
            e.printStackTrace();
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
