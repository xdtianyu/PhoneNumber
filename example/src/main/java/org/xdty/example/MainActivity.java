package org.xdty.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.Number;
import org.xdty.phone.number.model.NumberInfo;
import org.xdty.phone.number.model.ResponseHeader;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        new PhoneNumber(this, new PhoneNumber.Callback() {
            @Override
            public void onResponse(NumberInfo numberInfo) {
                // Do your jobs here
                textView.setText(numberInfo.toString());

                List<Number> numbers = numberInfo.getNumbers();
                for (Number number : numbers) {
                    Log.d(TAG, number.getNumber() +
                               ": " +
                               number.getType().getText() +
                               ", " +
                               number.getName() +
                               ", " +
                               number.getCount());
                }
            }

            @Override
            public void onResponseFailed(NumberInfo numberInfo) {
                ResponseHeader responseHeader = numberInfo.getResponseHeader();
                if (responseHeader != null) {
                    textView.setText(responseHeader.getStatus());
                }
            }
        }).fetch("10086", "10000, 10001", "OTHER_PHONE_NUMBER");
    }

}
