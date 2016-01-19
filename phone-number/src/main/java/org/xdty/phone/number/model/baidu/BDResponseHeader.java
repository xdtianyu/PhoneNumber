package org.xdty.phone.number.model.baidu;

public class BDResponseHeader {
    int status;
    long time;
    String version;

    public int getStatus() {
        return status;
    }

    public long getTime() {
        return time;
    }

    public String getVersion() {
        return version;
    }
}