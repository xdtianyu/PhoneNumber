package org.xdty.phone.number.model.baidu;

import org.xdty.phone.number.model.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BDNumberInfo {
    Map<String, BDResponse> response;
    BDResponseHeader responseHeader;
    boolean isOffline = false;

    public Map<String, BDResponse> getResponse() {
        return response;
    }

    public void setResponse(
            Map<String, BDResponse> response) {
        this.response = response;
    }

    public BDResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(BDResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public List<BDResponse> getNumbers() {
        List<BDResponse> numbers = new ArrayList<>();

        for (String s : response.keySet()) {
            BDResponse number = response.get(s);
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
            BDResponse r = response.get(k);
            BDLocation l = r.getLocation();
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

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }
}