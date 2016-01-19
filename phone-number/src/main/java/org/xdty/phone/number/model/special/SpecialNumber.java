package org.xdty.phone.number.model.special;

import android.content.Context;
import android.text.TextUtils;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

import java.util.ArrayList;
import java.util.List;

public class SpecialNumber implements INumber<SpecialNumber> {

    private final List<Zone> specialList = new ArrayList<Zone>() {
        {
            add(new Zone(-9999, 0, R.string.private_number, true));
            add(new Zone(550, 570, R.string.family_number, false));
        }
    };
    private Zone mZone;
    private Context mContext;

    public SpecialNumber(Context context) {
        mContext = context;
    }

    public SpecialNumber find(String number) {
        for (Zone z : specialList) {
            if (z.inZone(number)) {
                mZone = z;
                return this;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return mContext.getString(mZone.desId);
    }

    @Override
    public String getProvince() {
        return null;
    }

    @Override
    public Type getType() {
        return mZone.isWarning ? Type.REPORT : Type.POI;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getNumber() {
        return mZone.number;
    }

    @Override
    public String getProvider() {
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
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mZone.number);
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_SPECIAL;
    }

    public class Zone {
        public int max;
        public int min;
        public int desId;
        public String number;
        public boolean isWarning = false;

        Zone(int min, int max, int desId, boolean isWarning) {
            this.max = max;
            this.min = min;
            this.desId = desId;
            this.isWarning = isWarning;
        }

        public boolean inZone(String number) {
            try {
                long n = Long.parseLong(number);
                if (n >= min && n <= max) {
                    this.number = number;
                    return true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}
