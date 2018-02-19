package dev.bltucker.nanodegreecapstone.common.events;

import org.junit.Test;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EventBusTest {

    EventBus objectUnderTest = new EventBus();

    @Test
    public void testPublishWithSubscriptionShouldGetEvent() throws Exception {

        final Object fakeEvent = new Object();

        objectUnderTest.subscribeTo(fakeEvent.getClass())
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        assertEquals(o, fakeEvent);
                    }
                });


        objectUnderTest.publish(fakeEvent);

    }

    @Test
    public void testPublishWithDifferentClassSubscriptionShouldNotGetEvent() {

        final Object fakeEvent = new Object();

        objectUnderTest.subscribeTo(Integer.class.getClass())
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        fail();
                    }
                });


        objectUnderTest.publish(fakeEvent);

    }


}