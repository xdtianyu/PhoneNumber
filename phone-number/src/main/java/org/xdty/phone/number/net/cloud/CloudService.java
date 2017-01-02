package org.xdty.phone.number.net.cloud;

public interface CloudService {

    boolean put(CloudNumber cloudNumber);

    CloudNumber get(String number);

}
