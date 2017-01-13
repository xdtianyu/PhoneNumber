package org.xdty.phone.number.net.cloud;

import java.util.List;

public interface ICloudService {

    boolean put(CloudNumber cloudNumber);

    CloudNumber get(String number);

    boolean patch(CloudNumber cloudNumber);

    boolean delete(CloudNumber cloudNumber);

    List<CloudNumber> getAll(String deviceId);
}
