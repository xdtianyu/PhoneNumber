package org.xdty.phone.number.model.google;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import org.xdty.phone.number.model.Location;
import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.Type;

import java.util.Locale;

public class GooglePhoneNumber {
    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private static PhoneNumberToCarrierMapper carrierMapper =
            PhoneNumberToCarrierMapper.getInstance();

    private static PhoneNumberOfflineGeocoder geoCoder = PhoneNumberOfflineGeocoder.getInstance();

    public static boolean checkPhoneNumber(String phoneNumber, String countryCode) {

        int cCode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(cCode);
        pn.setNationalNumber(phone);

        return phoneNumberUtil.isValidNumber(pn);
    }

    public static String getCarrier(String phoneNumber, String countryCode) {

        int cCode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(cCode);
        pn.setNationalNumber(phone);
        String carrierEn = carrierMapper.getNameForNumber(pn, Locale.ENGLISH);
        String carrierZh = "";
        switch (carrierEn) {
            case "China Mobile":
                carrierZh += "移动";
                break;
            case "China Unicom":
                carrierZh += "联通";
                break;
            case "China Telecom":
                carrierZh += "电信";
                break;
            default:
                break;
        }
        return carrierZh;
    }

    public static String getGeo(String phoneNumber, String countryCode) {

        int cCode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(cCode);
        pn.setNationalNumber(phone);

        return geoCoder.getDescriptionForNumber(pn, Locale.CHINESE);
    }

    public static Number getNumber(String phone) {
        Number number = null;
        String geo = getGeo(phone, "86");
        String carrier = getCarrier(phone, "86");

        if (!geo.isEmpty() || !carrier.isEmpty()) {
            number = new Number();
            number.setNumber(phone);
            number.setName("");
            number.setCount(0);
            number.setType(Type.NORMAL);
            Location location = new Location();
            location.setOperators(carrier);
            location.setProvince(geo);
            location.setCity("");
            number.setLocation(location);
        }

        return number;
    }
}