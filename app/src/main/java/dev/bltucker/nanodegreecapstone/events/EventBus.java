package dev.bltucker.nanodegreecapstone.events;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Inspired by this blog post: http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 * linked from square otto's github page.
 */
public class EventBus {

    private final Subject<Object, Object> eventSubject = new SerializedSubject<>(PublishSubject.create());

    public void publish(Object event) {
        eventSubject.onNext(event);
    }

    public Observable<Object> subscribeToAll() {
        return eventSubject.asObservable();
    }

    public Observable<Object> subscribeTo(final Class eventClass) {
        return eventSubject.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return o.getClass().equals(eventClass);
            }
        }).asObservable();
    }
}
