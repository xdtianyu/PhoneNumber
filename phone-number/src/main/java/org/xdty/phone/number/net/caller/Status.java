package org.xdty.phone.number.net.caller;

public class Status {
    public int count;
    public int new_count;
    public long timestamp;
    public int version;
    public String md5;
    public String url;
    public int code;

    @Override
    public String toString() {
        return "Status{" +
                "count=" + count +
                ", new_count=" + new_count +
                ", timestamp=" + timestamp +
                ", version=" + version +
                ", md5='" + md5 + '\'' +
                ", url='" + url + '\'' +
                ", code=" + code +
                '}';
    }
}
