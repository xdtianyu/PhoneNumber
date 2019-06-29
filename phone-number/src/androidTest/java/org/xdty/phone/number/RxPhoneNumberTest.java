package org.xdty.phone.number;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xdty.phone.number.model.INumber;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class RxPhoneNumberTest {

    private static final String TAG = RxPhoneNumberTest.class.getSimpleName();

    private RxPhoneNumber mRxPhoneNumber;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        mRxPhoneNumber = new RxPhoneNumber();
        mRxPhoneNumber.init(context);
    }

    @Test
    public void getNumberTest() {
        final long start = System.currentTimeMillis();

        TestSubscriber<INumber> testSubscriber = new TestSubscriber<INumber>() {

            @Override
            public void onNext(INumber iNumber) {
                super.onNext(iNumber);

                Log.d(TAG, (System.currentTimeMillis() - start) + " - " + iNumber.toString() + ", " + iNumber.getApiId());
                assertThat(iNumber, notNullValue());
            }
        };


        Log.d(TAG, "start time: " + start);

        mRxPhoneNumber.getNumber("02985791396").subscribe(testSubscriber);

        testSubscriber.assertSubscribed();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertTerminated();
    }

    @Test
    public void getNumberListTest() {
        final long start = System.currentTimeMillis();

        TestSubscriber<List<INumber>> testSubscriber = new TestSubscriber<List<INumber>>() {
            @Override
            public void onNext(List<INumber> iNumbers) {
                super.onNext(iNumbers);

                for (INumber iNumber : iNumbers) {
                    Log.d(TAG, (System.currentTimeMillis() - start) + " - " + iNumber.toString() + ", " + iNumber.getApiId());
                }

                assertThat(iNumbers, notNullValue());
            }
        };

        Log.d(TAG, "start time: " + start);

        mRxPhoneNumber.getNumber("95194049").toList().toFlowable().subscribe(testSubscriber);

        testSubscriber.assertSubscribed();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertTerminated();
    }

    @Test
    public void getNumberListBlockingTest() {

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();

        Single.fromCallable(new Callable<Void>() {
            @Override
            public Void call() {

                final long start = System.currentTimeMillis();

                Log.d(TAG, "start time: " + start);

                List<INumber> numbers = mRxPhoneNumber.getNumber("95194049").toList().blockingGet();

                for (INumber iNumber : numbers) {
                    Log.d(TAG, (System.currentTimeMillis() - start) + " - " + iNumber.toString() + ", " + iNumber.getApiId());
                }

                assertThat(numbers, notNullValue());

                return null;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).toFlowable().subscribe(testSubscriber);

        testSubscriber.assertSubscribed();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertTerminated();
    }
}