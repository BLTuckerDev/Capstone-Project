package dev.bltucker.nanodegreecapstone.events;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * Inspired by this blog post: http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 * linked from square otto's github page.
 */
@ApplicationScope
public class EventBus {

    private final Subject<Object> eventSubject = PublishSubject.create();

    @Inject
    public EventBus(){
        //dagger
    }

    public void publish(Object event) {
        Timber.d("Publishing event: %s", event.getClass().toString());
        eventSubject.onNext(event);
    }

    public Observable<Object> subscribeTo(final Class eventClass) {
        Timber.d("Subscribing to events of type: %s", eventClass.toString());

        return eventSubject.filter(o -> {
            Timber.d("Creating subscriber to events of type: %s", eventClass.toString());
            return o.getClass().toString().equals(eventClass.toString());            });
    }
}
