package org.xdty.phone.number.local.google;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.NumberHandler;

import java.util.Locale;

public class GoogleNumberHandler implements NumberHandler<GooglePhoneNumber> {
    private static final String TAG = GoogleNumberHandler.class.getSimpleName();
    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private static PhoneNumberToCarrierMapper carrierMapper =
            PhoneNumberToCarrierMapper.getInstance();

    private static PhoneNumberOfflineGeocoder geoCoder = PhoneNumberOfflineGeocoder.getInstance();
    private Context mContext;

    public GoogleNumberHandler(Context context) {
        mContext = context;
    }

    public static boolean checkPhoneNumber(String phoneNumber, String countryCode) {

        int cCode = Integer.parseInt(countryCode);
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(cCode);
        pn.setNationalNumber(phone);

        return phoneNumberUtil.isValidNumber(pn);
    }

    public static String getCarrier(Context context, String number, Locale locale) {

        Phonenumber.PhoneNumber pn = getPhoneNumber(context, number, locale);

        if (pn == null) {
            return null;
        }

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
                carrierZh = carrierEn;
                break;
        }
        return carrierZh;
    }

    public static String getGeo(Context context, String number, Locale locale) {

        Phonenumber.PhoneNumber pn = getPhoneNumber(context, number, locale);

        if (pn == null) {
            return null;
        }
        return geoCoder.getDescriptionForNumber(pn, locale);
    }

    public static Phonenumber.PhoneNumber getPhoneNumber(Context context, String number,
            Locale locale) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }

        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String countryIso = getCurrentCountryIso(context, locale);
        Phonenumber.PhoneNumber pn = null;
        try {
            pn = util.parse(number, countryIso);
        } catch (NumberParseException e) {
            Log.v(TAG, "getGeoDescription: NumberParseException for incoming number '" +
                    number + "'");
        }
        return pn;
    }

    public static String getCurrentCountryIso(Context context, Locale locale) {
        final TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getNetworkCountryIso().toUpperCase();

        if (TextUtils.isEmpty(countryIso)) {
            countryIso = locale.getCountry();
            Log.w(TAG, "No CountryDetector; falling back to countryIso based on locale: "
                    + countryIso);
        }
        return countryIso;
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
        GooglePhoneNumber i = find(number, mContext.getResources().getConfiguration().locale);
        if (i == null) {
            i = find(number, Locale.CHINA);
        }
        if (i == null && !number.startsWith("+86")) {
            i = find("+86" + number, Locale.CHINA);
            if (i != null) {
                i.setNumber(number);
            }
        }
        return i;
    }

    private GooglePhoneNumber find(String number, Locale locale) {
        try {
            String geo = getGeo(mContext, number, locale);
            String carrier = getCarrier(mContext, number, locale);

            if (!TextUtils.isEmpty(geo) || !TextUtils.isEmpty(carrier)) {
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
