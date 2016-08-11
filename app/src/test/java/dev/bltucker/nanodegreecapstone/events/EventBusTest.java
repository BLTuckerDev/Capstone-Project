package dev.bltucker.nanodegreecapstone.events;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;

public class EventBusTest {

    EventBus objectUnderTest = new EventBus();

    @Test
    public void testPublishWithSubscriptionShouldGetEvent() throws Exception {

        final Object fakeEvent = new Object();

        objectUnderTest.subscribeTo(fakeEvent.getClass())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(Object o) {
                        assertEquals(o, fakeEvent);
                    }
                });


        objectUnderTest.publish(fakeEvent);

    }

    @Test
    public void testPublishWithDifferentClassSubscriptionShouldNotGetEvent(){

        final Object fakeEvent = new Object();

        objectUnderTest.subscribeTo(Integer.class.getClass())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(Object o) {
                        fail();
                    }
                });


        objectUnderTest.publish(fakeEvent);

    }


}