package org.xdty.phone.number.model.leancloud;

import org.xdty.phone.number.model.cloud.CloudNumber;

public class LeanCloudNumber extends CloudNumber {
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
