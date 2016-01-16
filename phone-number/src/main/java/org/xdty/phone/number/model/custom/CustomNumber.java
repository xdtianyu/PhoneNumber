package org.xdty.phone.number.model.custom;

import org.xdty.phone.number.model.Location;
import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.Type;

public class CustomNumber {
    String reason;
    int error_code;
    Result result;

    public org.xdty.phone.number.model.Number toNumber() {
        Number number = null;
        if (result != null) {
            number = new Number();
            number.setNumber(result.phone);
            number.setName(result.company + " " + result.name);
            number.setType(Type.POI);
            number.setCount(0);
            Location location = new Location();
            location.setProvince(result.province);
            location.setCity(result.city);
            location.setOperators(result.provider);
            number.setLocation(location);
        }
        return number;
    }

    class Result {
        String province;
        String city;
        String provider;
        String phone;
        String name;
        String company;
        String info;
    }
}
