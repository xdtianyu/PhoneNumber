package org.xdty.phone.number.model.soguo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class SogouNumber implements INumber {

    @Expose
    String number;
    @SerializedName(value = "NumInfo")
    String name;
    @SerializedName(value = "errorCode")
    int error;
    @SerializedName(value = "Amount")
    int count;

    @Override
    public String getName() {
        if (name != null && name.contains("号码通用户数据：")) {
            name = name.replace("号码通用户数据：", "");
        }
        if (name != null && name.contains("：0")) {
            name = name.replace("：0", "");
        }
        if (name != null && name.equals("该号码暂无标记")) {
            name = null;
        }
        return name;
    }

    @Override
    public String getProvince() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.REPORT;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public String getProvider() {
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isValid() {
        return error == 0 && getName() != null;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public int getApiId() {
        return API_ID_SG;
    }
}
