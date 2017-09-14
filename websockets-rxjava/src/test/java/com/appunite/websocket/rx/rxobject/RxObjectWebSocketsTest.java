package com.appunite.websocket.rx.rxobject;

import org.junit.Test;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.TestSubscriber;

public class RxObjectWebSocketsTest {

    @Test
    public void testLiftOperator() {
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();

        Flowable.range(0, 5)
                .lift(operator -> new FlowableSubscriber<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        operator.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        operator.onNext(integer + "!");
                    }

                    @Override
                    public void onError(Throwable t) {
                        operator.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        operator.onComplete();
                    }
                }).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueAt(0, "0!");
        testSubscriber.assertValueAt(1, "1!");
        testSubscriber.assertValueAt(2, "2!");
        testSubscriber.assertValueAt(3, "3!");
        testSubscriber.assertValueAt(4, "4!");
    }
}