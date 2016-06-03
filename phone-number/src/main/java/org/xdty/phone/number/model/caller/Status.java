package org.xdty.phone.number.model.caller;

public class Status {
    int count;
    int new_count;
    long timestamp;
    int version;

    public String toString() {
        return "count: " + count + ", new_count: " + new_count + ", timestamp: " + timestamp
                + ", version: " + version;
    }
}
