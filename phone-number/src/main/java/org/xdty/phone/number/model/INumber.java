package org.xdty.phone.number.model;

public interface INumber {

    int API_ID_SPECIAL = -1000;
    int API_ID_COMMON = -200;
    int API_ID_CALLER = -150;
    int API_ID_MARKED = -100;
    int API_ID_MVNP = -50;
    int API_ID_OFFLINE = -2;
    int API_ID_GOOGLE = -1;
    // baidu api is gone.
    //int API_ID_BD = 0;
    int API_ID_BD_DEAD = 0;
    int API_ID_JH = 1;
    int API_ID_SG = 2;
    int API_ID_CLOUD = 8;
    int API_ID_WEB = 16;
    int API_ID_CUSTOM = 1000;

    String getName();

    String getProvince();

    Type getType();

    String getCity();

    String getNumber();

    String getProvider();

    int getCount();

    boolean isValid();

    boolean isOnline();

    boolean hasGeo();

    int getApiId();

    void patch(INumber i);
}
