package org.xdty.phone.number.model.cloud;

import org.xdty.phone.number.model.cloud.CloudNumber;

public interface CloudService {
    boolean put(CloudNumber cloudNumber);

    CloudNumber get(String number);
}
