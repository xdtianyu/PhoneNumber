package org.xdty.phone.number.model.google;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;

import java.util.Locale;

public class GoogleNumberHandler implements NumberHandler<GooglePhoneNumber> {
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

    @Override
    public String url() {
        return null;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public GooglePhoneNumber find(String number) {
        try {
            number = number.replaceAll("\\+", "");
            String geo = getGeo(number, "86");
            String carrier = getCarrier(number, "86");

            if (!geo.isEmpty() || !carrier.isEmpty()) {
                return new GooglePhoneNumber(number, carrier, geo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_GOOGLE;
    }
}
