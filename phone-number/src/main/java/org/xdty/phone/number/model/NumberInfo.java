package org.xdty.phone.number.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NumberInfo {
    Map<String, Number> response;
    ResponseHeader responseHeader;

    public Map<String, Number> getResponse() {
        return response;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public List<Number> getNumbers() {
        List<Number> numbers = new ArrayList<>();

        for (String s : response.keySet()) {
            Number number = response.get(s);
            number.setNumber(s);
            numbers.add(number);
        }

        return numbers;
    }

    public String toString() {
        String s = "";
        for (String k : response.keySet()) {
            if (!s.isEmpty()) {
                s += "\n";
            }
            s += k;
            Number r = response.get(k);
            Location l = r.getLocation();
            Type type = r.getType();

            switch (type) {
                case NORMAL:
                    break;
                case POI:
                    s += ": " + r.getName();
                    break;
                case REPORT:
                    s += ": " + r.getName() + "-" + r.getCount();
                    break;
            }

            if (l != null) {
                s += ": " + l.getProvince() + "-" + l.getCity() + "-" + l.getOperators();
            }
        }
        return s;
    }
}