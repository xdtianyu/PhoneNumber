package org.xdty.phone.number;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.xdty.phone.number.model.NumberInfo;

import java.io.IOException;
import java.util.Arrays;

public class PhoneNumber {

    private final static String API_URL =
            "http://apis.baidu.com/baidu_mobile_security/phone_number_service/phone_information_query?location=true&tel=";
    static PhoneNumber number;
    static String apiKey;
    OkHttpClient okHttpClient;
    NumberInfo numberInfo;

    private PhoneNumber() {
        okHttpClient = new OkHttpClient();

    }

    public static PhoneNumber instance() {
        if (number == null) {
            number = new PhoneNumber();
        }
        return number;
    }

    public static PhoneNumber key(String apiKey) {
        PhoneNumber.apiKey = apiKey;
        return instance();
    }

    public NumberInfo getNumberInfo() {
        return numberInfo;
    }

    public String get(String... numbers) {

        String url = API_URL + Arrays.toString(numbers).replaceAll(" |\\[|\\]", "");

        Request.Builder request = new Request.Builder()
                .url(url);
        request.header("apikey", apiKey);
        try {
            com.squareup.okhttp.Response response = okHttpClient.newCall(request.build()).execute();
            String s = response.body().string();
            Gson gson = new Gson();
            numberInfo = gson.fromJson(s, NumberInfo.class);
            return numberInfo.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}