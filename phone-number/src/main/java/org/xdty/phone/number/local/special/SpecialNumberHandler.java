package org.xdty.phone.number.local.special;

import android.content.Context;

import org.xdty.phone.number.R;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;
import org.xdty.phone.number.util.App;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SpecialNumberHandler implements NumberHandler<SpecialNumber> {

    private final List<Zone> specialList = new ArrayList<Zone>() {
        {
            add(new Zone(-9999, 0, R.string.private_number, true));
            add(new Zone(550, 570, R.string.family_number, false));
            add(new Zone(661, 669, R.string.family_number, false));
            add(new Zone(680, 689, R.string.family_number, false));
        }
    };

    @Inject
    Context mContext;

    public SpecialNumberHandler() {
        App.getAppComponent().inject(this);
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
    public SpecialNumber find(String number) {
        for (Zone z : specialList) {
            if (z.inZone(number)) {
                return new SpecialNumber(mContext, z.copy(number));
            }
        }
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_SPECIAL;
    }
}
