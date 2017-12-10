package dev.bltucker.nanodegreecapstone.events

import com.jakewharton.rxrelay2.PublishRelay

import javax.inject.Inject

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

/**
 * Inspired by this blog post: http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 * linked from square otto's github page.
 */
@ApplicationScope
class EventBus @Inject constructor() {

    private val eventSubject = PublishRelay.create<Any>()

    fun publish(event: Any) {
        Timber.d("Publishing event: %s", event.javaClass.toString())
        eventSubject.accept(event)
    }

    fun subscribeTo(eventClass: Class<*>): Observable<Any> {
        Timber.d("Subscribing to events of type: %s", eventClass.toString())

        return eventSubject.filter { o ->
            Timber.d("Creating subscriber to events of type: %s", eventClass.toString())
            o.javaClass.toString() == eventClass.toString()
        }
    }
}
