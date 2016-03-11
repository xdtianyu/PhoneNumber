package org.xdty.phone.number.model.marked;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.Utils;

import java.io.File;
import java.io.IOException;

public class MarkedHandler implements NumberHandler<MarkedNumber> {

    private Context mContext;

    public MarkedHandler(Context context) {
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
    public MarkedNumber find(String number) {
        number = number.replaceAll("\\+86", "");
        if (number.contains("+")) {
            return null;
        }

        MarkedNumber markedNumber = null;

        try {
            File dbFile = Utils.createCacheFile(mContext, "marked.db", R.raw.marked);

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            Cursor cur = db.rawQuery("SELECT * FROM phone_number WHERE number = ? OR number = ? ",
                    new String[]{number, "0" + number});

            if (cur.getCount() == 1 && cur.moveToFirst()) {
                int type = cur.getInt(cur.getColumnIndex("type"));
                markedNumber = new MarkedNumber(number, type);
            }
            cur.close();

        } catch (IOException e) {
            e.printStackTrace();
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
