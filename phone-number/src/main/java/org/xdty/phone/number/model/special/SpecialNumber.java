package org.xdty.phone.number.model.special;

import android.content.Context;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.Type;

import java.util.ArrayList;
import java.util.List;

public class SpecialNumber {

    private final List<Zone> specialList = new ArrayList<Zone>() {
        {
            add(new Zone(-9999, 0, R.string.private_number, true));
            add(new Zone(550, 570, R.string.family_number, false));
        }
    };
    private Context mContext;

    public SpecialNumber(Context context) {
        mContext = context;
    }

    public Zone find(String number) {
        for (Zone z : specialList) {
            if (z.inZone(number)) {
                return z;
            }
        }
        return null;
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
            long n = Long.parseLong(number);
            if (n >= min && n <= max) {
                this.number = number;
                return true;
            }
            return false;
        }

        public Number toNumber() {
            Number number = new Number();
            number.setNumber(this.number);
            number.setName(mContext.getString(desId));
            number.setType(isWarning ? Type.REPORT : Type.POI);
            return number;
        }
    }

}
