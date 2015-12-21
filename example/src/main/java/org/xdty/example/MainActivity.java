package org.xdty.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.xdty.phone.number.PhoneNumber;
import org.xdty.phone.number.model.NumberInfo;
import org.xdty.phone.number.model.ResponseHeader;

public class MainActivity extends AppCompatActivity {

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
            }

            @Override
            public void onResponseFailed(NumberInfo numberInfo) {
                ResponseHeader responseHeader = numberInfo.getResponseHeader();
                if (responseHeader != null) {
                    textView.setText(responseHeader.getStatus());
                }
            }
        }).fetch("10086", "PHONE_NUMBER");
    }

}
