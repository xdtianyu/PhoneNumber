package org.xdty.phone.number.model;

public interface NumberHandler<T extends INumber> {

    String url();

    String key();

    T find(String number);

    boolean isOnline();

    int getApiId();
}
