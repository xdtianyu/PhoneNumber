package org.xdty.phone.number;

import android.content.Context;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xdty.phone.number.model.INumber;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.functions.Consumer;

@RunWith(AndroidJUnit4.class)
public class RxPhoneNumberTest {

    private static final String TAG = RxPhoneNumberTest.class.getSimpleName();

    RxPhoneNumber mRxPhoneNumber;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        mRxPhoneNumber = new RxPhoneNumber();
        mRxPhoneNumber.init(context);
    }

    @Test
    public void getNumberTest() throws InterruptedException {
        Consumer<INumber> testSubscriber = new Consumer<INumber>() {
            @Override
            public void accept(INumber iNumber) throws Exception {
                Log.d(TAG, iNumber.toString());
            }
        };

        mRxPhoneNumber.getNumber("95194049").subscribe(testSubscriber);

        Thread.sleep(30000);
    }
}