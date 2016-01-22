package org.xdty.phone.number.model;

public interface INumber {

    int API_ID_SPECIAL = -1000;
    int API_ID_OFFLINE = -2;
    int API_ID_GOOGLE = -1;
    int API_ID_BD = 0;
    int API_ID_JH = 1;
    int API_ID_CUSTOM = 1000;

    String getName();

    String getProvince();

    Type getType();

    String getCity();

    String getNumber();

    String getProvider();

    int getCount();

    boolean isValid();
}
