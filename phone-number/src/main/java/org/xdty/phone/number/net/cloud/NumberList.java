package org.xdty.phone.number.net.cloud;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NumberList {
    @SerializedName("_items")
    private List<CloudNumber> items;

    public List<CloudNumber> getItems() {
        return items;
    }

    public void setItems(List<CloudNumber> items) {
        this.items = items;
    }
}
