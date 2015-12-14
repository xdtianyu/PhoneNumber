package org.xdty.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.xdty.phone.number.PhoneNumber;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        new Thread(new Runnable() {
            @Override
            public void run() {
                PhoneNumber number = PhoneNumber.key("YOUR_API_KEY");
                String s = number.get("10086", "PHONE_NUMBER");
                updateText(s);
            }
        }).start();

    }

    void updateText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
}
