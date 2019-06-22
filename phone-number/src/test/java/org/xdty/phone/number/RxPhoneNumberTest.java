package org.xdty.phone.number;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxPhoneNumberTest {

    RxPhoneNumber mRxPhoneNumber;

    @Before
    public void setup() {
        Context context = Mockito.mock(Context.class);
        SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);

        mRxPhoneNumber = Mockito.spy(new RxPhoneNumber());

        Mockito.doReturn(sharedPreferences).when(mRxPhoneNumber).provideSharedPreferences();
        Mockito.doReturn(context).when(context).getApplicationContext();

        mRxPhoneNumber.init(context);
    }

    @Test
    public void getNumberHandlerTest() throws Exception {

    }

    @Test
    public void RxParallelTest() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);

        final long current = System.currentTimeMillis();

        Flowable.fromIterable(list)
                .parallel()
                .runOn(Schedulers.computation())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        int time = new Random().nextInt(5000);
                        System.out.println(
                                "Calculating " + integer + " on " + Thread.currentThread().getName()
                                        + " : " + time);
                        Thread.sleep(time);
                        return integer;
                    }
                })
                .sequential()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(integer + ": " + (System.currentTimeMillis() - current));
                    }
                });

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}