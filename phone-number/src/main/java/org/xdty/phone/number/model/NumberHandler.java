package org.xdty.phone.number.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface NumberHandler<T extends INumber> {

    Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    String url();

    String key();

    T find(String number);

    boolean isOnline();

    int getApiId();
}
