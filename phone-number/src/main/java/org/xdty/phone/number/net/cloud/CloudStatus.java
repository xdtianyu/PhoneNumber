package org.xdty.phone.number.net.cloud;

import com.google.gson.annotations.SerializedName;

public class CloudStatus {
    @SerializedName("_etag")
    private String eTag;
    @SerializedName("_status")
    private String status;
    @SerializedName("_updated")
    private String updated;
    @SerializedName("_created")
    private String created;
    @SerializedName("_id")
    private String id;

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
