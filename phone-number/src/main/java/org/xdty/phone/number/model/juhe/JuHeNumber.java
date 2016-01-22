package org.xdty.phone.number.model.juhe;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class JuHeNumber implements INumber {

    String reason;
    Result result;
    int error_code;

    protected JuHeNumber() {

    }

    @Override
    public String getName() {
        return result.hy == null ? result.rpt_type : result.hy.name;
    }

    @Override
    public String getProvince() {
        return result.province;
    }

    @Override
    public Type getType() {
        return result.iszhapian == 0 ? Type.NORMAL : Type.REPORT;
    }

    @Override
    public String getCity() {
        return result.city;
    }

    @Override
    public String getNumber() {
        return result.phone;
    }

    @Override
    public String getProvider() {
        return result.sp;
    }

    @Override
    public int getCount() {
        return result.rpt_cnt;
    }

    @Override
    public boolean isValid() {
        return result != null && error_code == 0;
    }

    class Result {
        int iszhapian;
        String province;
        String city;
        String sp;
        String phone;
        String rpt_type;
        String rpt_comment;
        int rpt_cnt;
        Hy hy;
        String countDesc;

    }

    class Hy {
        String city;
        String lng;
        String lat;
        String name;
        String addr;
        String tel;
    }

}
