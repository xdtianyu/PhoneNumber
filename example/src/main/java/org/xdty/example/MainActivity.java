package org.xdty.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.INumber;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        new PhoneNumber(this, new PhoneNumber.Callback() {

            String result = "";

            @Override
            public void onResponseOffline(INumber number) {
                String s = number.getNumber() +
                        ": " +
                        number.getType().getText() +
                        ", " +
                        number.getName() +
                        " : " +
                        number.getProvince() + " " + number.getCity() + " " + number.getProvider();
                Log.d(TAG, s);
            }

            @Override
            public void onResponse(INumber number) {
                // Do your jobs here
                String s = number.getNumber() +
                        ": " +
                        number.getType().getText() +
                        ", " +
                        number.getName() +
                        ", " +
                        number.getCount() +
                        " : " +
                        number.getProvince() + " " + number.getCity() + " " + number.getProvider();
                result += s + "\n";
                textView.setText(result);
                Log.d(TAG, s);
            }

            @Override
            public void onResponseFailed(INumber number, boolean isOnline) {
            }
        }).fetch("10086", "10000", "10001", "02151860253", "4001001673", "-1", "-2", "550", "551",
                "559", "569", "+16505551212", "+8615829812345");
    }

}
