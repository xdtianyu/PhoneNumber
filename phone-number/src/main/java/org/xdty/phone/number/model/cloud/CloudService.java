package org.xdty.phone.number.model.cloud;

public interface CloudService {
    boolean put(CloudNumber cloudNumber);

    CloudNumber get(String number);
}
