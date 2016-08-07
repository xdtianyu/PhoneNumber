package org.xdty.phone.number.model.special;

import android.content.Context;
import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class SpecialNumber implements INumber {

    private Zone mZone;
    private Context mContext;

    protected SpecialNumber(Context context, Zone zone) {
        mContext = context;
        mZone = zone;
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
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mZone.number);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasGeo() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_SPECIAL;
    }

    @Override
    public void patch(INumber i) {

    }

}
