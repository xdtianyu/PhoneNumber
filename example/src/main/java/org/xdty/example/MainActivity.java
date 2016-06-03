package org.xdty.example;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.caller.Status;
import org.xdty.phone.number.model.cloud.CloudNumber;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        // set prefer api
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("api_type", INumber.API_ID_SG)
                .apply();

        PhoneNumber.init(this);
        final PhoneNumber phoneNumber = PhoneNumber.getInstance();
        phoneNumber.setCallback(new PhoneNumber.Callback() {

            String result = "";

            @Override
            public void onResponseOffline(INumber number) {
                String s = "onResponseOffline: " +
                        number.getNumber() +
                        ": " +
                        number.getType().getText() +
                        ", " +
                        number.getName() +
                        " : " +
                        number.getProvince() +
                        " " +
                        number.getCity() +
                        " " +
                        number.getProvider() +
                        ", " +
                        number.getApiId();
                Log.e(TAG, s);
            }

            @Override
            public void onResponse(INumber number) {
                // Do your jobs here
                String s = "onResponse: " +
                        number.getNumber() +
                        ": " +
                        number.getType().getText() +
                        ", " +
                        number.getName() +
                        ", " +
                        number.getCount() +
                        " : " +
                        number.getProvince() +
                        " " +
                        number.getCity() +
                        " " +
                        number.getProvider() +
                        ", " +
                        number.getApiId();
                result += s + "\n";
                textView.setText(result);
                Log.e(TAG, s);
            }

            @Override
            public void onResponseFailed(INumber number, boolean isOnline) {
            }
        });
        //phoneNumber.fetch("10086", "10000", "10001", "02151860253", "4001001673", "-1", "-2", "550",
        //        "551",
        //        "559", "569", "4000838114", "+16505551212", "10021", "+8615829812345",
        //        "+18057518222", "1050861064", "13375971846", "05923598645");

        CloudNumber cloudNumber = new CloudNumber();
        cloudNumber.setUid("dadasdasfadsfsad");
        cloudNumber.setCount(0);
        cloudNumber.setFrom(1);
        cloudNumber.setType(1);
        cloudNumber.setName("骚扰");
        cloudNumber.setNumber("1222033");

        phoneNumber.addCloudListener(new PhoneNumber.CloudListener() {
            @Override
            public void onPutResult(CloudNumber number, boolean result) {
                Log.d(TAG, number.getName() + " - " + result);
            }
        });

        //phoneNumber.put(cloudNumber);

        phoneNumber.setCheckUpdateCallback(new PhoneNumber.CheckUpdateCallback() {
            @Override
            public void onCheckResult(Status status) {
                if (status != null) {
                    Log.e(TAG, "new update available: " + status.toString());
                    phoneNumber.upgradeData();
                } else {
                    Log.e(TAG, "already latest.");
                }
            }

            @Override
            public void onUpgradeData(boolean result) {
                Log.e(TAG, "onUpgradeData: " + result);
            }
        });
        phoneNumber.checkUpdate();

        phoneNumber.fetch("6683350368");
    }

}
