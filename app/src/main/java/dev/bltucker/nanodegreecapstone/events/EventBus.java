package dev.bltucker.nanodegreecapstone.events;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import timber.log.Timber;

/**
 * Inspired by this blog post: http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 * linked from square otto's github page.
 */
public class EventBus {

    private final Subject<Object, Object> eventSubject = new SerializedSubject<>(PublishSubject.create());

    public void publish(Object event) {
        Timber.d("Publishing event: %s", event.getClass().toString());
        eventSubject.onNext(event);
    }

    public Observable<Object> subscribeToAll() {
        return eventSubject.asObservable();
    }

    public Observable<Object> subscribeTo(final Class eventClass) {
        Timber.d("Subscribing to events of type: %s", eventClass.toString());
        return eventSubject.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                Timber.d("Creating subscriber to events of type: %s", eventClass.toString());
                return o.getClass().toString().equals(eventClass.toString());
            }
        }).asObservable();
    }
}
