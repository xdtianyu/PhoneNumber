package org.xdty.phone.number.model.juhe;

import org.xdty.phone.number.model.Location;
import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.Type;

public class JuHeNumber {

    String reason;
    Result result;
    int error_code;

    public Number toNumber() {
        Number number = null;
        if (result != null) {
            number = new Number();
            number.setNumber(result.phone);
            number.setName(result.hy == null ? result.rpt_type : result.hy.name);
            number.setType(result.iszhapian == 0 ? Type.NORMAL : Type.REPORT);
            number.setCount(result.rpt_cnt);
            Location location = new Location();
            location.setProvince(result.province);
            location.setCity(result.city);
            location.setOperators(result.sp);
            number.setLocation(location);
        }
        return number;
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
