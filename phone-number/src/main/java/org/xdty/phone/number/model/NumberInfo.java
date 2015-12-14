package org.xdty.phone.number.model;

import java.util.Map;

public class NumberInfo {
    Map<String, Response> response;
    ResponseHeader responseHeader;

    public Map<String, Response> getResponse() {
        return response;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public String toString() {
        String s = "";
        for (String k : response.keySet()) {
            if (!s.isEmpty()) {
                s += "\n";
            }
            s += k;
            Response r = response.get(k);
            Location l = r.getLocation();
            String type = r.getType();
            if (type != null) {
                if (type.equals("poi")) {
                    s += ": " + r.getName();
                } else if (type.equals("report")) {
                    s += ": " + r.getName() + "-" + r.getCount();
                }
            }
            if (l != null) {
                s += ": " + l.getProvince() + "-" + l.getCity() + "-" + l.getOperators();
            }
        }
        return s;
    }
}